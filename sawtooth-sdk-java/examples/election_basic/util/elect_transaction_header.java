package election_basic.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import sawtooth.sdk.protobuf.TransactionHeaderOrBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class elect_transaction_header implements TransactionHeaderOrBuilder {

    final String familyName = "election";
    final String familyVersion = "1.0";

    String batcherPubKey;
    List<String> dependencies;
    List<String> inputs;
    List<String> outputs;
    String nonce;
    String payload;
    String signerPubKey;

    public elect_transaction_header(String b, List<String> d, List<String> i, List<String> o, String no, String p, String pub) {
        batcherPubKey = b;
        dependencies = d;
        inputs = i;
        outputs = o;
        nonce = no;
        payload = p;
        signerPubKey = pub;
    }

    @Override
    public String getBatcherPublicKey() {
        return batcherPubKey;
    }

    @Override
    public ByteString getBatcherPublicKeyBytes() {
        return ByteString.copyFrom(batcherPubKey.getBytes());
    }

    @Override
    public List<String> getDependenciesList() {
        return dependencies;
    }

    @Override
    public int getDependenciesCount() {
        return dependencies.size();
    }

    @Override
    public String getDependencies(int i) {
        return dependencies.get(i);
    }

    @Override
    public ByteString getDependenciesBytes(int i) {
        return ByteString.copyFrom(dependencies.get(i).getBytes());
    }

    @Override
    public String getFamilyName() {
        return familyName;
    }

    @Override
    public ByteString getFamilyNameBytes() {
        return ByteString.copyFrom(familyName.getBytes());
    }

    @Override
    public String getFamilyVersion() {
        return familyVersion;
    }

    @Override
    public ByteString getFamilyVersionBytes() {
        return ByteString.copyFrom(familyVersion.getBytes());
    }

    @Override
    public List<String> getInputsList() {
        return inputs;
    }

    @Override
    public int getInputsCount() {
        return inputs.size();
    }

    @Override
    public String getInputs(int i) {
        return inputs.get(i);
    }

    @Override
    public ByteString getInputsBytes(int i) {
        return ByteString.copyFrom(inputs.get(i).getBytes());
    }

    @Override
    public String getNonce() {
        return nonce;
    }

    @Override
    public ByteString getNonceBytes() {
        return ByteString.copyFrom(nonce.getBytes());
    }

    @Override
    public List<String> getOutputsList() {
        return outputs;
    }

    @Override
    public int getOutputsCount() {
        return outputs.size();
    }

    @Override
    public String getOutputs(int i) {
        return outputs.get(i);
    }

    @Override
    public ByteString getOutputsBytes(int i) {
        return ByteString.copyFrom(outputs.get(i).getBytes());
    }

    @Override
    public String getPayloadSha512() {
        return payload;
    }

    @Override
    public ByteString getPayloadSha512Bytes() {
        return ByteString.copyFrom(payload.getBytes());
    }

    @Override
    public String getSignerPublicKey() {
        return signerPubKey;
    }

    @Override
    public ByteString getSignerPublicKeyBytes() {
        return ByteString.copyFrom(signerPubKey.getBytes());
    }

    @Override
    public Message getDefaultInstanceForType() {
        return null;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public List<String> findInitializationErrors() {
        return null;
    }

    @Override
    public String getInitializationErrorString() {
        return null;
    }

    @Override
    public Descriptors.Descriptor getDescriptorForType() {
        return null;
    }

    @Override
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
        return null;
    }

    @Override
    public boolean hasOneof(Descriptors.OneofDescriptor oneofDescriptor) {
        return false;
    }

    @Override
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneofDescriptor) {
        return null;
    }

    @Override
    public boolean hasField(Descriptors.FieldDescriptor fieldDescriptor) {
        return false;
    }

    @Override
    public Object getField(Descriptors.FieldDescriptor fieldDescriptor) {
        return null;
    }

    @Override
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor fieldDescriptor) {
        return 0;
    }

    @Override
    public Object getRepeatedField(Descriptors.FieldDescriptor fieldDescriptor, int i) {
        return null;
    }

    @Override
    public UnknownFieldSet getUnknownFields() {
        return null;
    }

    public String serializeToString() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        String serialized = bos.toString();
        oos.close();
        bos.close();
        return serialized;
    }
}
