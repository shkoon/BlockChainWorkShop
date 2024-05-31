package ma.enset.blockchainworkshop.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionPool {

    List<Transaction> pendingTransactions;

    public TransactionPool(){
        pendingTransactions=new ArrayList<>();
    }
    public void addTransaction(Transaction transaction){
        pendingTransactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction){
        pendingTransactions.remove(transaction);
    }
    public void removeTransactions(List<Transaction> transactions) {
        pendingTransactions.removeAll(transactions);
    }
    public List<Transaction> getPendingTransactions() {
        return new ArrayList<>(pendingTransactions);
    }

}
