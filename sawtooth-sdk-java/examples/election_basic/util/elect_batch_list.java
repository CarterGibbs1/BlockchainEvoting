package election_basic.util;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import sawtooth.sdk.protobuf.Batch;
import sawtooth.sdk.protobuf.BatchListOrBuilder;
import sawtooth.sdk.protobuf.BatchOrBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class elect_batch_list implements BatchListOrBuilder {

    List<elect_batch> batches;

    public elect_batch_list(elect_batch b) {
        batches = List.of(b);
    }

    @Override
    public List<Batch> getBatchesList() {
        return null;
    }

    @Override
    public Batch getBatches(int i) {
        return null;
    }

    @Override
    public int getBatchesCount() {
        return batches.size();
    }

    @Override
    public List<? extends BatchOrBuilder> getBatchesOrBuilderList() {
        return batches;
    }

    @Override
    public BatchOrBuilder getBatchesOrBuilder(int i) {
        return batches.get(i);
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
