import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import org.apache.commons.lang3.StringUtils;

import sawtooth.sdk.processor.Context;
import sawtooth.sdk.processor.TransactionHandler;
import sawtooth.sdk.processor.Utils;
import sawtooth.sdk.processor.exceptions.InternalError;
import sawtooth.sdk.processor.exceptions.InvalidTransactionException;
import sawtooth.sdk.protobuf.TpProcessRequest;
import sawtooth.sdk.protobuf.TransactionHeader;

import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

public class BallotHandler implements TransactionHandler {

    private final Logger logger = Logger.getLogger(BallotHandler.class.getName());
    private String ballotNameSpace;

    /**
     * default constructor
     */
    public BallotHandler() {
        try {
            this.ballotNameSpace = Utils.hash512(this.transactionFamilyName().getBytes("UTF-8")).substring(0, 6);
          } catch (UnsupportedEncodingException usee) {
            usee.printStackTrace();
            this.ballotNameSpace = "";
          }
    }

    /**
     * Returns the transaction family's name.
     * @return the transaction family's name
     */
    public String transactionFamilyName() {
        return "ballot";
    }

    /**
     * Returns the transaction family's version.
     * @return the transaction family's version
     */
    public String getVersion() {
        return "1.0";

    }

    /**
     * Returns the namespaces for this transaction handler.
     * @return the namespaces for this transaction handler
     */
    public Collection<String> getNameSpaces() {
        ArrayList<String> namespaces = new ArrayList<>();
        namespaces.add(this.ballotNameSpace);
        return namespaces;

    }

    /**
     * Applies the given transaction request.
     * @param transactionRequest the transaction request to apply
     * @param state the on-chain state for this transaction
     * @throws InvalidTransactionException an invalid transaction was encountered
     * @throws InternalError something went wrong processing transaction
     */
    public void apply(TpProcessRequest transactionRequest, Context context) throws InvalidTransactionException, InternalError {
        TransactionData transactionData = getUnpackedTransaction(transactionRequest);

        // The transaction signer is the voter
        String voter;
        TransactionHeader header = transactionRequest.getHeader();
        voter = header.getSignerPublicKey();
        if (transactionData.ballot.equals("")) {
            throw new InvalidTransactionException("Ballot is required");
        }
        if (transactionData.ballot.contains("|")) {
            throw new InvalidTransactionException("Ballot cannot contain '|'");
        }
        if (transactionData.action.equals("")) {
            throw new InvalidTransactionException("Action is required");
        }
        if (transactionData.action.equals("vote")) {
            if (transactionData.candidate.equals(""))
                throw new InvalidTransactionException("Must specify a candidate");
        }
        if (!transactionData.action.equals("vote") && !transactionData.action.equals("create") && !transactionData.action.equals("spoil")) {
            throw new InvalidTransactionException(String.format("Invalid action: %s", transactionData.action));
        }
    
        String address = makeBallotAddress(transactionData.ballot);
        // context.get() returns a list.
        // If no data has been stored yet at the given address, it will be empty.
        String stateEntry = context.getState(Collections.singletonList(address)).get(address).toStringUtf8();
        BallotData stateData = getStateData(stateEntry, transactionData.ballot);
        BallotData updatedBallotData = useBallot(transactionData, stateData, voter);
        storeBallotData(address, updatedBallotData, stateEntry, context);
    }

    class TransactionData {
        final String ballot;
        final String action;
        final String candidate;
    
        TransactionData(String ballot, String action, String candidate) {
          this.ballot = ballot;
          this.action = action;
          this.candidate = candidate;
        }
    }

    class BallotData {
        final String ballot;
        final String candidate;
        final String name;
        final String state;
    
        BallotData(String ballot, String candidate, String name, String state) {
          this.ballot = ballot;
          this.candidate = candidate;
          this.state = state;
          this.name = name;
        }
    }

    /**
     * Helper function to generate ballot address.
     */
    private String makeBallotAddress(String ballot) throws InternalError {
        try {
        String hashedBallot = Utils.hash512(ballot.getBytes("UTF-8"));
        return ballotNameSpace + hashedBallot.substring(0, 64);
        } catch (UnsupportedEncodingException e) {
        throw new InternalError("Internal Error: " + e.toString());
        }
    }

    /**
     * Helper function to retrieve the ballot, action, and candidate from transaction request.
     */
    private TransactionData getUnpackedTransaction(TpProcessRequest transactionRequest) throws InvalidTransactionException {
        String payload = transactionRequest.getPayload().toStringUtf8();
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
     * Helper function to retrieve the ballot, state, name, and candidate from state store.
     */
    private BallotData getStateData(String stateEntry, String ballot) throws InternalError, InvalidTransactionException {
        if (stateEntry.length() == 0) {
            return new BallotData("", "", "", "", "");
        } else {
        try {
            String ballotCsv = getBallotCsv(stateEntry, ballot);
            ArrayList<String> ballotList = new ArrayList<>(Arrays.asList(ballotCsv.split(",")));
            while (ballotList.size() < 5) {
                ballotList.add("");
            }
            return new BallotData(ballotList.get(0), ballotList.get(1), ballotList.get(2), ballotList.get(3), ballotList.get(4));
            } catch (Error e) {
                throw new InternalError("Failed to deserialize ballot data");
            }
        }
    }

    /**
    * Helper function to retrieve the correct ballot info from the list of ballot data CSV.
    */
    private String getBallotCsv(String stateEntry, String ballotName) {
        ArrayList<String> ballotCsvList = new ArrayList<>(Arrays.asList(stateEntry.split("\\|")));
        for (String ballotCsv : ballotCsvList) {
            if (ballotCsv.regionMatches(0, ballotName, 0, ballotName.length())) {
                return ballotCsv;
            }
        }
        return "";
    }

    /** Helper function to store state data. */
    private void storeBallotData(String address, BallotData ballotData, String stateEntry, Context context) throws InternalError, InvalidTransactionException {
        String ballotDataCsv = String.format("%s,%s,%s,%s,%s", ballotData.ballot, ballotData.name, ballotData.state, ballotData.candidate);
        if (stateEntry.length() == 0) {
            stateEntry = ballotDataCsv;
        } else {
            ArrayList<String> dataList = new ArrayList<>(Arrays.asList(stateEntry.split("\\|")));
            for (int i = 0; i <= dataList.size(); i++) {
                if (i == dataList.size() || dataList.get(i).regionMatches(0, ballotData.ballot, 0, ballotData.ballot.length())) {
                    dataList.set(i, ballotDataCsv);
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
    * Function that handles ballot arg parsing.
    */
    private BallotData useBallot(TransactionData transactionData, BallotData ballotData, String voter) throws InvalidTransactionException, InternalError {
        switch (transactionData.action) {
            case "create":
                return applyCreate(transactionData, ballotData, voter);
            case "vote":
                return applyVote(transactionData, ballotData, voter);
            case "spoil":
                return applySpoil(transactionData, ballotData, voter);
            default:
                throw new InvalidTransactionException(String.format("Invalid action: %s", transactionData.action));
        }
    }

    /**
     * Function that handles ballot logic for 'create' action.
     */
    private BallotData applyCreate(TransactionData transactionData, BallotData ballotData, String voter) throws InvalidTransactionException {
        if (!ballotData.ballot.equals("")) {
            throw new InvalidTransactionException("Invalid Action: Ballot already exists");
        }
        display(String.format("Voter %s created a ballot", abbreviate(voter)));
        return new BallotData(transactionData.ballot, "", "", "REGISTERED");
    }

    /**
    * Function that handles ballot logic for 'vote' action.
    */
    private BallotData applyVote(TransactionData transactionData, BallotData ballotData, String voter) throws InvalidTransactionException, InternalError {
        // Check if invalid vote
        if ("VOTED".equals(ballotData.state)) {
            throw new InvalidTransactionException("Invalid action: Already voted, did you mean spoil?");
        }
        if (ballotData.ballot.equals("")) {
            throw new InvalidTransactionException("Invalid action: You need to create a ballot to vote.");
        }
        if (!"REGISTERED".equals(ballotData.state)) {
            throw new InternalError(String.format("Internal Error: Ballot has reached an invalid state: %s", ballotData.state));
        }
        
        // Assign Candidate to Vote.
        display(String.format("Voter %s has voted \n", abbreviate(voter)));
        return new BallotData(ballotData.ballot, transactionData.candidate, ballotData.name, "VOTED");
    }

    /**
    * Function that handles ballot logic for 'spoil' action.
    */
    private BallotData applySpoil(TransactionData transactionData, BallotData ballotData, String voter) throws InvalidTransactionException {
        // Check if invalid transaction
        if ("REGISTERED".equals(ballotData.state)) {
            throw new InvalidTransactionException("Invalid action: Haven't voted yet, did you mean vote?");
        }
        if (ballotData.ballot.equals("")) {
            throw new InvalidTransactionException("Invalid action: You need to have a ballot to spoil.");
        }
        if (!"VOTED".equals(ballotData.state)) {
            throw new InternalError(String.format("Internal Error: Ballot has reached an invalid state: %s", ballotData.state));
        }
        
        // Spoil ballot
        display(String.format("Voter %s created a ballot", abbreviate(voter)));
        return new BallotData(transactionData.ballot, "", "", "REGISTERED");
    }


    /**
     * Helper function to print ballot data to the logger.
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
    private Object abbreviate(String voter) {
        return voter.substring(0, Math.min(voter.length(), 6));
    }
}