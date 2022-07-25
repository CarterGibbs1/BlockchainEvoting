package xo_java;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import sawtooth.sdk.protobuf.Transaction;
import sawtooth.sdk.protobuf.TransactionListOrBuilder;
import sawtooth.sdk.protobuf.TransactionOrBuilder;

import java.util.List;
import java.util.Map;

public class elect_transaction_list implements TransactionListOrBuilder {

    List<elect_transaction> transactions;

    public elect_transaction_list(List<elect_transaction> t) {
        transactions = t;
    }

    @Override
    public List<Transaction> getTransactionsList() {
        return null;
    }

    @Override
    public Transaction getTransactions(int i) {
        return null;
    }

    @Override
    public int getTransactionsCount() {
        return transactions.size();
    }

    @Override
    public List<? extends TransactionOrBuilder> getTransactionsOrBuilderList() {
        return transactions;
    }

    @Override
    public TransactionOrBuilder getTransactionsOrBuilder(int i) {
        return transactions.get(i);
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
