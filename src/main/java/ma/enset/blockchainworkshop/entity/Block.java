package ma.enset.blockchainworkshop.entity;


import lombok.Data;

import ma.enset.blockchainworkshop.encryption.HashUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Data
public class Block {

    private int index;
    private Instant timeStamp;
    private String previousHash;
    private String currentHash;
    private List<Transaction> transactions;
    private String data;
    private int nonce;

    public Block(){
      this.transactions=  new ArrayList<>();
        this.timeStamp=Instant.now();
    }
    public Block(int index, String previousHash, List<Transaction> transactions, int nonce) {
        this.index = index;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.nonce = nonce;
        this.timeStamp=Instant.now();

    }

    public String calculateHash(){
        String input = index + timeStamp.toString() + previousHash + transactions.toString() + nonce;
        return HashUtil.calculateSHA256(input);
    }

    public Block(int index, String previousHash, String data) {
        this.index = index;
        this.previousHash = previousHash;
        this.data = data;
        this.transactions = new ArrayList<>();
        this.timeStamp = Instant.now();
        this.currentHash = calculateHash();
    }
    public boolean validateBlock(int difficulty, Block previousBlock) {
        String prefix = "0".repeat(difficulty);
        String calculatedHash = calculateHash();

        // Check if the calculated hash satisfies the difficulty requirement
        if (!calculatedHash.startsWith(prefix)) {
            return false;
        }

        // Check if the calculated hash matches the stored hash
        if (!calculatedHash.equals(currentHash)) {
            return false;
        }

        // Check if the block's index is correct
        if (index != previousBlock.getIndex() + 1) {
            return false;
        }

        // Check if the previous hash matches
        if (!previousHash.equals(previousBlock.getCurrentHash())) {
            return false;
        }

        // Check if the timestamp is valid (not in the future)
        if (timeStamp.isAfter(Instant.now())) {
            return false;
        }

        return true;
    }



    public void incrementNonce() {
        nonce++;}

}
