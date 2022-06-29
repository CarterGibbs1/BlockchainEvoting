package sawtooth.examples.xo;

import sawtooth.examples.xo.XoHandler;
import sawtooth.sdk.processor.TransactionProcessor;

public class BallotTransactionProcessor {
  /**
   * the method that runs a Thread with a TransactionProcessor in it.
   */
  public static void main(String[] args) {
    TransactionProcessor transactionProcessor = new TransactionProcessor(args[0]);
    transactionProcessor.addHandler(new BallotHandler());
    Thread thread = new Thread(transactionProcessor);
    thread.start();
  }
}
