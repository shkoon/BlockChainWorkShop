package ma.enset.blockchainworkshop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
public class BlockChain {

    private List<Block> chain;
    private TransactionPool transactionPool;
    private int difficulty;
    private static final int DIFFICULTY_ADJUSTMENT_INTERVAL = 10;

    public BlockChain(int difficulty) {
        this.chain = new ArrayList<>();
        this.difficulty = difficulty;
        this.transactionPool = new TransactionPool();
        chain.add(createGenesisBlock());
    }
    private Block createGenesisBlock() {
        return new Block(0, "0", "Genesis Block");
    }
    private String getDifficultyPrefix(int difficulty) {
        return "0".repeat(difficulty);
    }
    public Block addBlock(Block newBlock) {
        mineBlock(newBlock,difficulty);
        chain.add(newBlock);
        return newBlock;
    }

    private void adjustDifficulty() {
        if (chain.size() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0 && chain.size() > 0) {
            Block lastAdjustedBlock = chain.get(chain.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
            Block latestBlock = getLatestBlock();
            long timeExpected = DIFFICULTY_ADJUSTMENT_INTERVAL * 10 * 60;
            long timeTaken = Duration.between(lastAdjustedBlock.getTimeStamp(), latestBlock.getTimeStamp()).getSeconds();

            if (timeTaken < timeExpected / 2) {
                difficulty++;
            } else if (timeTaken > timeExpected * 2) {
                difficulty--;
            }
        }
    }
    public Block getBlockByIndex(int index){
        if(index>chain.size()){
            return null;
        }
        else return chain.get(index);
    }
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            if (!currentBlock.validateBlock(difficulty, previousBlock)) {
                return false;
            }
        }
        return true;
    }
    public boolean isChainValid(List<Block> chain) {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.getCurrentHash().equals(currentBlock.calculateHash())) {
                return false;
            }

            if (!currentBlock.getPreviousHash().equals(previousBlock.getCurrentHash())) {
                return false;
            }
        }
        return true;
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }
    public void addTransaction(Transaction transaction) {
        transactionPool.addTransaction(transaction);
    }

    public Block mineBlock() {
        Block newBlock = new Block(
                chain.size(),
                getLatestBlock().getCurrentHash(),
                transactionPool.getPendingTransactions(),
                0
        );

        mineBlock(newBlock, difficulty);
        return addBlock(newBlock);
    }

    public void mineBlock(Block block, int difficulty) {
        String prefix = getDifficultyPrefix(difficulty);
        String hash;

        do {
            block.incrementNonce();
            hash = block.calculateHash();
        } while (!hash.startsWith(prefix));

        block.setCurrentHash(hash);
    }

    public void replaceChain(Block[] newBlocks) {
        List<Block> newChain = new ArrayList<>(List.of(newBlocks));
        if (newChain.size() > this.chain.size() && isChainValid(newChain)) {
            this.chain = newChain;
        }
    }
}
