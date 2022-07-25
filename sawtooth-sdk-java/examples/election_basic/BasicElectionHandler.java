/* Copyright 2017 Intel Corporation
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
------------------------------------------------------------------------------*/

package election_basic;

import com.google.protobuf.ByteString;
import election_basic.Paillier.PaillierCipherText;
import election_basic.Paillier.PaillierPublicKey;
import org.apache.commons.lang3.StringUtils;
import sawtooth.sdk.processor.Context;
import sawtooth.sdk.processor.TransactionHandler;
import sawtooth.sdk.processor.Utils;
import sawtooth.sdk.processor.exceptions.InternalError;
import sawtooth.sdk.processor.exceptions.InvalidTransactionException;
import sawtooth.sdk.protobuf.TpProcessRequest;
import sawtooth.sdk.protobuf.TransactionHeader;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class BasicElectionHandler implements TransactionHandler {

	private final Logger logger = Logger.getLogger(BasicElectionHandler.class.getName());
	private String xoNameSpace;

	/**
	 * constructor.
	 */
	public BasicElectionHandler() {
		try {
			this.xoNameSpace = Utils.hash512(
					this.transactionFamilyName().getBytes("UTF-8")).substring(0, 6);
		} catch (UnsupportedEncodingException usee) {
			usee.printStackTrace();
			this.xoNameSpace = "";
		}
	}

	@Override
	public String transactionFamilyName() {
		return "election";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public Collection<String> getNameSpaces() {
		ArrayList<String> namespaces = new ArrayList<>();
		namespaces.add(this.xoNameSpace);
		return namespaces;
	}

	class TransactionData {
		final String electionName;
		final String action;
		final String ballotOrKey;

		TransactionData(String gameName, String action, String ballotOrKey) {
			this.electionName = gameName;
			this.action = action;
			this.ballotOrKey = ballotOrKey;
		}
	}

	class ElectionData {
		final String electionName;
		final String cipher;
		final String pubKey;

		ElectionData(String electionName, String cipher, String pubKey) {
			this.electionName = electionName;
			this.cipher = cipher;
			this.pubKey = pubKey;
		}
	}

	@Override
	public void apply(TpProcessRequest transactionRequest, Context context)
			throws InvalidTransactionException, InternalError {

		TransactionData transactionData = getUnpackedTransaction(transactionRequest);

		// The transaction signer is the voter or the election creator (person)
		String person;
		TransactionHeader header = transactionRequest.getHeader();
		person = header.getSignerPublicKey();
		PaillierCipherText[] ciphers = null;

		if (transactionData.action.equals("vote")) {
			try {
				byte[] rawCipher = Base64.getDecoder().decode(transactionData.ballotOrKey);
				ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
				ObjectInput in = new ObjectInputStream(bis);
				ciphers = (PaillierCipherText[]) in.readObject();
				in.close();
			} catch (NumberFormatException | IOException | ClassNotFoundException e) {
				e.printStackTrace();
				throw new InvalidTransactionException("Ballot array is invalid. 4");
			}
		}
		if (transactionData.electionName.equals("")) {
			throw new InvalidTransactionException("Election name is required");
		}
		if (transactionData.electionName.contains("|")) {
			throw new InvalidTransactionException("Election name cannot contain '|'");
		}
		if (transactionData.action.equals("")) {
			throw new InvalidTransactionException("Action is required");
		}
		if (!transactionData.action.equals("vote") && !transactionData.action.equals("create")) {
			throw new InvalidTransactionException(
					String.format("Invalid action: %s", transactionData.action));
		}

		String address = makeElectionAddress(transactionData.electionName);
		// context.get() returns a list.
		// If no data has been stored yet at the given address, it will be empty.
		String stateEntry = context.getState(Collections.singletonList(address)).get(address).toStringUtf8();
		ElectionData stateData = getStateData(stateEntry, transactionData.electionName);

		ElectionData updatedElectionData = runElection(transactionData, stateData, person);
//		System.out.printf("In updatedElectionData:\n\n%s\n%s\n%s\n\n",updatedElectionData.electionName,updatedElectionData.,updatedElectionData.get(2));

		storeElectionData(address, updatedElectionData, stateEntry, context);
	}

	/**
	 * Helper function to retrieve game gameName, action, and space from transaction request.
	 */
	private TransactionData getUnpackedTransaction(TpProcessRequest transactionRequest)
			throws InvalidTransactionException {
		String payload =  transactionRequest.getPayload().toStringUtf8();
		ArrayList<String> payloadList = new ArrayList<>(Arrays.asList(payload.split(",")));
		if (payloadList.size() > 3) {
			throw new InvalidTransactionException("Invalid payload serialization");
		}
		while (payloadList.size() < 3) {
			payloadList.add("");
		}
		return new TransactionData(payloadList.get(0), payloadList.get(1), payloadList.get(2));
	}

	/**
	 * Helper function to retrieve the board, state, playerOne, and playerTwo from state store.
	 */
	private ElectionData getStateData(String stateEntry, String gameName)
			throws InternalError, InvalidTransactionException {
		if (stateEntry.length() == 0) {
			return new ElectionData("", "", "");
		} else {
			try {
				String electionCsv = getGameCsv(stateEntry, gameName);
				ArrayList<String> electionList = new ArrayList<>(Arrays.asList(electionCsv.split(",")));
				while (electionList.size() < 3) {
					electionList.add("");
				}
				return new ElectionData(electionList.get(0), electionList.get(1), electionList.get(2));
			} catch (Error e) {
				throw new InternalError("Failed to deserialize game data");
			}
		}
	}

	/**
	 * Helper function to generate game address.
	 */
	private String makeElectionAddress(String gameName) throws InternalError {
		try {
			String hashedName = Utils.hash512(gameName.getBytes("UTF-8"));
			return xoNameSpace + hashedName.substring(0, 64);
		} catch (UnsupportedEncodingException e) {
			throw new InternalError("Internal Error: " + e.toString());
		}
	}

	/**
	 * Helper function to retrieve the correct game info from the list of game data CSV.
	 */
	private String getGameCsv(String stateEntry, String gameName) {
		ArrayList<String> gameCsvList = new ArrayList<>(Arrays.asList(stateEntry.split("\\|")));
		for (String gameCsv : gameCsvList) {
			if (gameCsv.regionMatches(0, gameName, 0, gameName.length())) {
				return gameCsv;
			}
		}
		return "";
	}

	/** Helper function to store state data. */
	private void storeElectionData(
			String address, ElectionData gameData, String stateEntry, Context context)
			throws InternalError, InvalidTransactionException {
		String gameDataCsv = String.format("%s,%s,%s", gameData.electionName, gameData.cipher, gameData.pubKey);
		if (stateEntry.length() == 0) {
			stateEntry = gameDataCsv;
		} else {
			ArrayList<String> dataList = new ArrayList<>(Arrays.asList(stateEntry.split("\\|")));
			for (int i = 0; i <= dataList.size(); i++) {
				if (i == dataList.size()
						|| dataList.get(i).regionMatches(0, gameData.electionName, 0, gameData.electionName.length())) {
					dataList.set(i, gameDataCsv);
					break;
				}
			}
			stateEntry = StringUtils.join(dataList, "|");
		}

		ByteString csvByteString = ByteString.copyFromUtf8(stateEntry);
		Map.Entry<String, ByteString> entry = new AbstractMap.SimpleEntry<>(address, csvByteString);
		Collection<Map.Entry<String, ByteString>> addressValues = Collections.singletonList(entry);
		Collection<String> addresses = context.setState(addressValues);
		if (addresses.size() < 1) {
			throw new InternalError("State Error");
		}
	}

	/**
	 * Function that handles game logic.
	 */
	private ElectionData runElection(TransactionData transactionData, ElectionData gameData, String player)
			throws InvalidTransactionException, InternalError {
		switch (transactionData.action) {
			case "create":
				return applyCreate(transactionData, gameData, player);
			case "vote":
				return applyVote(transactionData, gameData, player);
			default:
				throw new InvalidTransactionException(String.format("Invalid action: %s", transactionData.action));
		}
	}

	/**
	 * Function that handles game logic for 'create' action.
	 * @return
	 */
	private ElectionData applyCreate(TransactionData transactionData, ElectionData electionData, String player) throws InvalidTransactionException {
		if (!electionData.cipher.equals("")) {
			throw new InvalidTransactionException("Invalid Action: Election already exists");
		}

		PaillierPublicKey pub_key;

		try {
			byte[] rawCipher = Base64.getDecoder().decode(transactionData.ballotOrKey);
			ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
			ObjectInput in = new ObjectInputStream(bis);
			pub_key = (PaillierPublicKey) in.readObject();
			in.close();
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new InvalidTransactionException("PubKey is invalid");
		}
		PaillierCipherText[] ciphers = new PaillierCipherText[5];

		PaillierCipherText empty = pub_key.getEmptyCipherText();
		for(int i = 0; i < ciphers.length; i++) {
			ciphers[i] = empty;
		}

		display(String.format("%s created an election", abbreviate(player)));

		byte[] rawByteArray;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(ciphers);
			rawByteArray = bos.toByteArray();
			out.close();
			bos.close();
		} catch (IOException e) {
			throw new InvalidTransactionException("Internal error: ballot array is invalid.");
		}
		return new ElectionData(transactionData.electionName, transactionData.ballotOrKey, Base64.getEncoder().encodeToString(rawByteArray));
	}

	/**
	 * Function that handles game logic for 'take' action.
	 * @return
	 */
	private ElectionData applyVote(TransactionData transactionData, ElectionData electionData, String player) throws InvalidTransactionException, InternalError {
		PaillierCipherText[] ciphers = null;

		try {
			byte[] rawCipher = Base64.getDecoder().decode(transactionData.ballotOrKey);
			ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
			ObjectInput in = new ObjectInputStream(bis);
			ciphers = (PaillierCipherText[]) in.readObject();
			in.close();
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
			throw new InvalidTransactionException("Ballot array is invalid. 1");
		}

		PaillierCipherText[] stateCiphers = null;

		try {
			byte[] rawCipher = Base64.getDecoder().decode(electionData.cipher);
			ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
			ObjectInput in = new ObjectInputStream(bis);
			stateCiphers = (PaillierCipherText[]) in.readObject();
			in.close();
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
			throw new InvalidTransactionException("Ballot array is invalid. 2");
		}
		if(stateCiphers.length != ciphers.length) {
			throw new InvalidTransactionException("Ballot array is invalid, Incorrect number of votes.");
		}
		for(int i = 0; i < stateCiphers.length; i++) {
			stateCiphers[i] = stateCiphers[i].hAdd(ciphers[i]);
		}
		byte updatedCipherRaw[] = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(stateCiphers);
			updatedCipherRaw = bos.toByteArray();
			out.close();
			bos.close();
		} catch (IOException e) {
			throw new InvalidTransactionException("Ballot array is invalid. 3");
		}
		String updatedCipher = Base64.getEncoder().encodeToString(updatedCipherRaw);

		ElectionData updatedElectionData = new ElectionData(electionData.electionName, electionData.pubKey, updatedCipher);

		display(electionDataToString(updatedElectionData));
		return updatedElectionData;
	}

	private String electionDataToString(ElectionData electionData) {
		String out = "";

		out += String.format("Election: %s\n", electionData.electionName);
		out += "\n";
		PaillierCipherText[] ciphers = null;
		try {
			byte[] rawCipher = Base64.getDecoder().decode(electionData.cipher);
			ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
			ObjectInput in = new ObjectInputStream(bis);
			ciphers = (PaillierCipherText[]) in.readObject();
			in.close();
			for(int i = 0; i < ciphers.length; i++) {
				out += (ciphers[i].toString() + "\n");
			}
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
		}
		return out;
	}

	private void display(String msg) {
		String displayMsg = "";
		int length = 0;
		String[] msgLines = msg.split("\n");
		if (msg.contains("\n")) {
			for (String line : msgLines) {
				if (line.length() > length) {
					length = line.length();
				}
			}
		} else {
			length = msg.length();
		}

		displayMsg = displayMsg.concat("\n+" + printDashes(length + 2) + "+\n");
		for (String line : msgLines) {
			displayMsg = displayMsg.concat("+" + StringUtils.center(line, length + 2) + "+\n");
		}
		displayMsg = displayMsg.concat("+" + printDashes(length + 2) + "+");
		logger.info(displayMsg);
	}

	/**
	 * Helper function to create a string with a specified number of dashes (for logging purposes).
	 */
	private String printDashes(int length) {
		String dashes = "";
		for (int i = 0; i < length; i++) {
			dashes = dashes.concat("-");
		}
		return dashes;
	}

	/**
	 * Helper function to shorten a string to a max of 6 characters for logging purposes.
	 */
	private Object abbreviate(String player) {
		return player.substring(0, Math.min(player.length(), 6));
	}
}
