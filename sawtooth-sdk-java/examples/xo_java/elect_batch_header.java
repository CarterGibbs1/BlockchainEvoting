package xo_java;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import sawtooth.sdk.protobuf.BatchHeaderOrBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class elect_batch_header implements BatchHeaderOrBuilder {

    String pubKey;
    List<String> transactionIds;

    public elect_batch_header(String p, List<String> t) {
        pubKey = p;
        transactionIds = t;
    }

    @Override
    public String getSignerPublicKey() {
        return pubKey;
    }

    @Override
    public ByteString getSignerPublicKeyBytes() {
        return ByteString.copyFrom(pubKey.getBytes());
    }

    @Override
    public List<String> getTransactionIdsList() {
        return transactionIds;
    }

    @Override
    public int getTransactionIdsCount() {
        return transactionIds.size();
    }

    @Override
    public String getTransactionIds(int i) {
        return transactionIds.get(i);
    }

    @Override
    public ByteString getTransactionIdsBytes(int i) {
        return ByteString.copyFrom(transactionIds.get(i).getBytes());
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
