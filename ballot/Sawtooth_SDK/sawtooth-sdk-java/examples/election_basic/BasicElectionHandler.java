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

package sawtooth.examples.election_basic;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import blah.*;

import org.apache.commons.lang3.StringUtils;

import sawtooth.sdk.processor.Context;
import sawtooth.sdk.processor.TransactionHandler;
import sawtooth.sdk.processor.Utils;
import sawtooth.sdk.processor.exceptions.InternalError;
import sawtooth.sdk.processor.exceptions.InvalidTransactionException;
import sawtooth.sdk.protobuf.TpProcessRequest;
import sawtooth.sdk.protobuf.TransactionHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

public class BasicElectionHandler implements TransactionHandler {

	private final Logger logger = Logger.getLogger(BasicElectionHandler.class.getName());
	private String electionNameSpace;

	/**
	 * constructor.
	 */
	public BasicElectionHandler() {
		try {
			this.electionNameSpace = Utils.hash512(
					this.transactionFamilyName().getBytes("UTF-8")).substring(0, 6);
		} catch (UnsupportedEncodingException usee) {
			usee.printStackTrace();
			this.electionNameSpace = "";
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
		namespaces.add(this.electionNameSpace);
		return namespaces;
	}

	class TransactionData {
		final String action;
		final String name;
		final String ciphersOrKey;

		TransactionData(String action, String name, String pub_key_or_ciphers) {
			this.action = action;
			this.name = name;
			this.ciphersOrKey = pub_key_or_ciphers;
		}
	}

	class ElectionData {
		final String electionName;
		final String cipher;
		final String electionPub;

		ElectionData(String electionName, String electionPub, String cipher) {
			this.electionName = electionName;
			this.electionPub = electionPub;
			this.cipher = cipher;
			
//			System.out.println(electionPub);
//			System.out.println(cipher);
		}
	}

	@Override
	public void apply(TpProcessRequest transactionRequest, Context context) throws InvalidTransactionException, InternalError { 
		
		TransactionData transactionData = getUnpackedTransaction(transactionRequest);

		// The transaction signer is the voter or the election creator (person)
		String person;
		TransactionHeader header = transactionRequest.getHeader();
		person = header.getSignerPublicKey();
		AdditiveCiphertext[] ciphers = null;

		if (transactionData.action.equals("vote")) {
			try {
				byte[] rawCipher = Base64.getDecoder().decode(transactionData.ciphersOrKey);
				ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
				ObjectInput in = new ObjectInputStream(bis);
				ciphers = (AdditiveCiphertext[]) in.readObject();
				in.close();
			} catch (NumberFormatException | IOException | ClassNotFoundException e) {
				e.printStackTrace();
				throw new InvalidTransactionException("Ballot array is invalid. 4");
			}
		}
		if (transactionData.name.equals("")) {
			throw new InvalidTransactionException("Election name is required");
		}
		if (transactionData.name.contains("|")) {
			throw new InvalidTransactionException("Election name cannot contain '|'");
		}
		if (transactionData.action.equals("")) {
			throw new InvalidTransactionException("Action is required");
		}
		if (!transactionData.action.equals("vote") && !transactionData.action.equals("create")) {
			throw new InvalidTransactionException(
					String.format("Invalid action: %s", transactionData.action));
		}

		String address = makeAddress(transactionData.name);
		// context.get() returns a list.
		// If no data has been stored yet at the given address, it will be empty.
		String stateEntry = context.getState(
				Collections.singletonList(address)
				).get(address).toStringUtf8();
		ElectionData stateData = getStateData(stateEntry, transactionData.name);

		ElectionData updatedElectionData = runElection(transactionData, stateData, person);
//		System.out.printf("In updatedElectionData:\n\n%s\n%s\n%s\n\n",updatedElectionData.electionName,updatedElectionData.,updatedElectionData.get(2));
		
		storeElectionData(address, updatedElectionData, stateEntry, context);
	}

	/**
	 * Helper function to retrieve election electionName, action, and pub_key from transaction request.
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
		return new TransactionData(payloadList.get(1), payloadList.get(0), payloadList.get(2).replaceAll("\\R", ""));
	}

	/**
	 * Helper function to retrieve the electionData from state store.
	 */
	private ElectionData getStateData(String stateEntry, String name)
			throws InternalError, InvalidTransactionException {
		if (stateEntry.length() == 0) {
			return new ElectionData("", "", "");
		} else {
			try {
				String electionCsv = getElectionCsv(stateEntry, name);
				ArrayList<String> electionList = new ArrayList<>(Arrays.asList(gameCsv.split(",")));
				while (electionList.size() < 3) {
					electionList.add("");
				}
//				System.out.printf("In getStateData:\n\n%s\n%s\n%s\n\n",electionList.get(0),electionList.get(1),electionList.get(2));
				return new ElectionData(electionList.get(0), electionList.get(1), electionList.get(2));
			} catch (Error e) {
				throw new InternalError("Failed to deserialize election data");
			}
		}
	}

	/**
	 * Helper function to generate game address.
	 */
	private String makeAddress(String name) throws InternalError {
		try {
			String hashedName = Utils.hash512(name.getBytes("UTF-8"));
			return electionNameSpace + hashedName.substring(0, 64);
		} catch (UnsupportedEncodingException e) {
			throw new InternalError("Internal Error: " + e.toString());
		}
	}

	/**
	 * Helper function to retrieve the correct election info from the list of game data CSV.
	 */
	private String getElectionCsv(String stateEntry, String electionName) {
		ArrayList<String> electionCsvList = new ArrayList<>(Arrays.asList(stateEntry.split("\\|")));
		for (String electionCsv : electionCsvList) {
			if (electionCsv.regionMatches(0, electionName, 0, electionName.length())) {
				return electionCsv;
			}
		}
		return "";
	}

	/** Helper function to store state data. */
	private void storeElectionData(
			String address, ElectionData electionData, String stateEntry, Context context)
					throws InternalError, InvalidTransactionException {
		String electionDataCsv = String.format("%s,%s,%s",
				electionData.electionName, electionData.electionPub, electionData.cipher);
		if (stateEntry.length() == 0) {
			stateEntry = electionDataCsv;
		} else {
			ArrayList<String> dataList = new ArrayList<>(Arrays.asList(stateEntry.split("\\|")));
			for (int i = 0; i <= dataList.size(); i++) {
				if (i == dataList.size()
						|| dataList.get(i).regionMatches(0, electionData.electionName, 0, electionData.electionName.length())) {
					dataList.set(i, electionDataCsv);
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
	private ElectionData runElection(TransactionData transactionData, ElectionData electionData, String person)
			throws InvalidTransactionException, InternalError {
		switch (transactionData.action) {
		case "create":
			return applyCreate(transactionData, electionData, person);
		case "vote":
			return applyVote(transactionData, electionData, person);
		default:
			throw new InvalidTransactionException(String.format(
					"Invalid action: %s gadhgfkdajhgjk", transactionData.action));
		}
	}

	/**
	 * Function that handles creating an election transaction.
	 */
	private ElectionData applyCreate(TransactionData transactionData, ElectionData electionData, String person)
			throws InvalidTransactionException {
		if (!electionData.cipher.equals("")) {
			throw new InvalidTransactionException("Invalid Action: Election already exists");
		}
		
		Additive_Pub_Key pub_key = null;

		try {
			byte[] rawCipher = Base64.getDecoder().decode(transactionData.ciphersOrKey);
			ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
			ObjectInput in = new ObjectInputStream(bis);
			pub_key = (PaillierPubKey) in.readObject();
			in.close();
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new InvalidTransactionException("PubKey is invalid");
		}
		AdditiveCiphertext[] ciphers = new AdditiveCiphertext[5];
		
		AdditiveCiphertext empty = pub_key.getEmptyCiphertext();
		for(int i = 0; i < ciphers.length; i++) {
			ciphers[i] = empty;
		}
		
		display(String.format("%s created an election", abbreviate(person)));
		
		byte[] rawByteArray = null;
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
		return new ElectionData(transactionData.name, transactionData.ciphersOrKey, Base64.getEncoder().encodeToString(rawByteArray));
	}

	/**
	 * Function that handles vote transactions.
	 */
	private ElectionData applyVote(TransactionData transactionData, ElectionData electionData, String person) throws InvalidTransactionException, InternalError {

		AdditiveCiphertext[] ciphers = null;

		try {
			byte[] rawCipher = Base64.getDecoder().decode(transactionData.ciphersOrKey);
			ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
			ObjectInput in = new ObjectInputStream(bis);
			ciphers = (AdditiveCiphertext[]) in.readObject();
			in.close();
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
			throw new InvalidTransactionException("Ballot array is invalid. 1");
		}

		AdditiveCiphertext[] stateCiphers = null;

		try {
			byte[] rawCipher = Base64.getDecoder().decode(electionData.cipher);
			ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
			ObjectInput in = new ObjectInputStream(bis);
			stateCiphers = (AdditiveCiphertext[]) in.readObject();
			in.close();
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
			throw new InvalidTransactionException("Ballot array is invalid. 2");
		}
		if(stateCiphers.length != ciphers.length) {
			throw new InvalidTransactionException("Ballot array is invalid, Incorrect number of votes.");
		}
		for(int i = 0; i < stateCiphers.length; i++) {
			stateCiphers[i] = stateCiphers[i].homomorphicAdd(ciphers[i]);
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
		
		ElectionData updatedElectionData = new ElectionData(electionData.electionName, electionData.electionPub, updatedCipher);

		display(electionDataToString(updatedElectionData));
		return updatedElectionData;
	}

	/**
	 * Helper function to create an ASCII representation of the election.
	 */
	private String electionDataToString(ElectionData electionData) {
		String out = "";

		out += String.format("Election: %s\n", electionData.electionName);
		out += "\n";
		AdditiveCiphertext[] ciphers = null;
		try {
			byte[] rawCipher = Base64.getDecoder().decode(electionData.cipher);
			ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
			ObjectInput in = new ObjectInputStream(bis);
			ciphers = (AdditiveCiphertext[]) in.readObject();
			in.close();
			for(int i = 0; i < ciphers.length; i++) {
				out += (ciphers[i].toString() + "\n");
			}
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
		}
		return out;
	}

	/**
	 * Helper function to print election data to the logger.
	 */
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
	private Object abbreviate(String person) {
		return person.substring(0, Math.min(person.length(), 6));
	}
}
