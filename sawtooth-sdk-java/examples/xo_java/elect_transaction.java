package xo_java;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import sawtooth.sdk.protobuf.TransactionOrBuilder;

import java.util.List;
import java.util.Map;

public class elect_transaction implements TransactionOrBuilder {

    ByteString header;
    String headerSignature;
    ByteString payload;

    public elect_transaction(ByteString h, String hs, ByteString p) {
        header = h;
        headerSignature = hs;
        payload = p;
    }

    @Override
    public ByteString getHeader() {
        return header;
    }

    @Override
    public String getHeaderSignature() {
        return headerSignature;
    }

    @Override
    public ByteString getHeaderSignatureBytes() {
        return ByteString.copyFrom(headerSignature.getBytes());
    }

    @Override
    public ByteString getPayload() {
        return payload;
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
}
