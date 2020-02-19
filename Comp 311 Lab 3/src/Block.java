
import java.util.LinkedList;
import java.util.List;

/**
 * @author jacob
 * @version 7/16/19
 */
public class Block {
    private String hash;
    private Block parent;
    private List<Transaction> transactions;
    private List<Block> blocks;

    /**
     * constructs block
     * @param hash hash of block
     */
    public Block(String hash) {
        this.hash = hash;
        parent = null;
        transactions = new LinkedList<Transaction>();
        blocks = new LinkedList<Block>();
    }

    /**
     * adds child
     * @param b the block to be added
     */
    public void addChild(Block b) {
        b.setParent(this);
        blocks.add(b);
    }

    /**
     * adds tx
     * @param tx the tx to be added
     */
    public void addTransaction(Transaction tx) {
        transactions.add(tx);
    }

    /**
     * gets hash
     * @return hash the hash to be returned
     */
    public String getHash() {
        return hash;
    }
    /**
     * gets parent
     * @return the parent
     */
    public Block getParent() {
        return parent;
    }
    /**
     * gets txs
     * @return the txs
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }
    /**
     * sets parent
     * @param parent the parent
     */
    public void setParent(Block parent) {
        this.parent = parent;
    }

}
