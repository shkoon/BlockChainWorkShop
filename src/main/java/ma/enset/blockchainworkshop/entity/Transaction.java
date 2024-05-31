package ma.enset.blockchainworkshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PublicKey;

@Data
public class Transaction {

    private String sender;
    private String recipient;
    private double amount;
    private String signature;

    public Transaction(String sender, String recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.signature = "";
    }
    public boolean verifyTransaction() throws Exception {
        PublicKey publicKey = Wallet.getPublicKeyFromAddress(sender);
        return Wallet.verifyTransaction(this, publicKey);
    }

}
