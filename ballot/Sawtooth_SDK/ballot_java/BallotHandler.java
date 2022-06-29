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
    public void apply(TpProcessRequest transactionRequest, Context state) throws InvalidTransactionException, InternalError {

    }

    class TransactionData {
        final String gameName;
        final String action;
        final String space;
    
        TransactionData(String gameName, String action, String space) {
          this.gameName = gameName;
          this.action = action;
          this.space = space;
        }
      }
}