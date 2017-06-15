package codeu.chat.server;

import java.util.Vector;

public final class TransactionLog{

  public static Vector<Transaction> transactionLog;

  public TransactionLog(){
    transactionLog = new Vector<Transaction>();
  }

  public void add(Transaction t){
    transactionLog.add(t);
  }

  public void flush(){
    for (Transaction transaction : transactionLog) {
      transaction.execute();
    }
  }
}
