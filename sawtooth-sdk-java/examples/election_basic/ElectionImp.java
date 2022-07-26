package election_basic;

import com.google.protobuf.ByteString;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import election_basic.Paillier.PaillierPrivateKey;
import election_basic.Paillier.PaillierPublicKey;
import election_basic.util.BlockchainEncoder;
import sawtooth.sdk.processor.Utils;
import sawtooth.sdk.protobuf.*;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

public class ElectionImp {

    private static final Logger logger = Logger.getLogger(ElectionImp.class.getName());

    private BlockchainEncoder encoder = new BlockchainEncoder();

    /**
    public void list(String[] args) throws UnirestException {
        String address = encoder.getBlockchainAddressFromKey("election", args[1]);
        GetRequest getRequest = Unirest.get("http://localhost:8008/state");
        getRequest.queryString("address", address);
        String serverResponse = getRequest.asString().getBody();
        logger.info(serverResponse);

    }
*/
// (action) (name) (pubKey) (privKey)
    public void create(String[] args) throws UnirestException, IOException, ClassNotFoundException {
        PaillierPrivateKey privateKey = PaillierPrivateKey.fromFile(args[3]);
        String publicKeyHex = PaillierPublicKey.fromFile(args[2]).toHex();

        // Parameters in sequence : action, name
        String payload = args[0] + "," + args[1];
        logger.info("Sending payload as - " + payload);
        String payloadBytes = Utils.hash512(payload.getBytes());
        ByteString payloadByteString = ByteString.copyFrom(payload.getBytes());

        // Get unique address
        String address = encoder.getBlockchainAddressFromKey("election", args[1]);

        logger.info("Sending address as - " + address);
        TransactionHeader txnHeader = TransactionHeader.newBuilder().clearBatcherPublicKey()
                .setBatcherPublicKey(publicKeyHex).setFamilyName("election") // Idem Family
                .setFamilyVersion("1.0").addInputs(address).setNonce(String.valueOf(new Random().nextInt())).addOutputs(address)
                .setPayloadSha512(payloadBytes).setSignerPublicKey(publicKeyHex).build();

        ByteString txnHeaderBytes = txnHeader.toByteString();

        String value = privateKey.sign(txnHeader.toString()).toString();
        Transaction txn = Transaction.newBuilder().setHeader(txnHeaderBytes).setPayload(payloadByteString)
                .setHeaderSignature(value).build();

        //TpProcessRequest tp = TpProcessRequest.newBuilder().setHeader(txnHeader).setPayload(payloadByteString).setSignature(value).build();

        BatchHeader batchHeader = BatchHeader.newBuilder().clearSignerPublicKey().setSignerPublicKey(publicKeyHex)
                .addTransactionIds(txn.getHeaderSignature()).build();

        ByteString batchHeaderBytes = batchHeader.toByteString();

        String valueBatch = privateKey.sign(batchHeaderBytes.toString()).toString();
        Batch batch = Batch.newBuilder().setHeader(batchHeaderBytes).setHeaderSignature(valueBatch).setTrace(true)
                .addTransactions(txn).build();
        BatchList batchList = BatchList.newBuilder().addBatches(batch).build();
        ByteString batchBytes = batchList.toByteString();

        String serverResponse = Unirest.post("http://validator:4004")
                .header("Content-Type", "application/octet-stream").body(batchBytes.toByteArray()).asString()
                .getBody();

        logger.info("Service Reponse :" + serverResponse);
    }

    public void vote(String[] args) throws IOException, ClassNotFoundException, UnirestException {
        String publicKeyHex = PaillierPublicKey.fromFile(args[2]).toHex();
        PaillierPrivateKey privateKey = PaillierPrivateKey.fromFile(args[3]);


        // Parameters in sequence : action, name, electionPubKey
        String payload = args[0] + "," + args[1] + "," + PaillierPublicKey.fromFile(args[2]).toString();

        logger.info("Sending payload as - " + payload);
        String payloadBytes = Utils.hash512(payload.getBytes());
        ByteString payloadByteString = ByteString.copyFrom(payload.getBytes());

        // Get unique address
        String address = encoder.getBlockchainAddressFromKey("election", args[1]);

        logger.info("Sending address as - " + address);
        TransactionHeader.Builder txnHeaderb = TransactionHeader.newBuilder().clearBatcherPublicKey()
                .setBatcherPublicKey(publicKeyHex).setFamilyName("election")
                .setFamilyVersion("1.0").addInputs(address).setNonce(String.valueOf(new Random().nextInt())).addOutputs(address)
                .setPayloadSha512(payloadBytes).setSignerPublicKey(publicKeyHex);

        logger.info("before building");

        TransactionHeader txnHeader = txnHeaderb.build();

        logger.info("Created transaction header - " + txnHeader);
        ByteString txnHeaderBytes = txnHeader.toByteString();

        String value = privateKey.sign(txnHeader.toString()).toString();
        Transaction txn = Transaction.newBuilder().setHeader(txnHeaderBytes).setPayload(payloadByteString)
                .setHeaderSignature(value).build();
        logger.info("Created transaction - " + txn);

        //TpProcessRequest tp = TpProcessRequest.newBuilder().setHeader(txnHeader).setPayload(payloadByteString).setSignature(value).build();
        BatchHeader batchHeader = BatchHeader.newBuilder().clearSignerPublicKey().setSignerPublicKey(publicKeyHex)
                .addTransactionIds(txn.getHeaderSignature()).build();

        logger.info("Created batch header - " + batchHeader);

        ByteString batchHeaderBytes = batchHeader.toByteString();

        String valueBatch = privateKey.sign(batchHeaderBytes.toString()).toString();
        Batch batch = Batch.newBuilder().setHeader(batchHeaderBytes).setHeaderSignature(valueBatch).setTrace(true)
                .addTransactions(txn).build();
        logger.info("Created batch - " + batch);

        BatchList batchList = BatchList.newBuilder().addBatches(batch).build();
        ByteString batchBytes = batchList.toByteString();

        logger.info("Posting to \"http://validator:4004\"");
        String serverResponse = Unirest.post("http://validator:4004")
                .header("Content-Type", "application/octet-stream").body(batchBytes.toByteArray()).asString()
                .getBody();

        logger.info("Service Reponse :" + serverResponse);
    }
}
