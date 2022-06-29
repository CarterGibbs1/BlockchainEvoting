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
    /**
     * Returns the transaction family's name.
     * @return the transaction family's name
     */
    public String transactionFamilyName() {
        return null;
    }

    /**
     * Returns the transaction family's version.
     * @return the transaction family's version
     */
    public String getVersion() {
        return null;

    }

    /**
     * Returns the namespaces for this transaction handler.
     * @return the namespaces for this transaction handler
     */
    public Collection<String> getNameSpaces() {
        return null;

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
}