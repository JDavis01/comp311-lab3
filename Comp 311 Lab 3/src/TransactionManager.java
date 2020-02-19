import java.util.ArrayList;
import java.util.List;
/**
 * @author jacob
 * @version 7/16/19
 */
public class TransactionManager {
    /** Max transactions per block */
    public static final int MAX_TX_PER_BLOCK = 3;
    /** Reward for producing block */
    public static final int BLOCK_REWARD = 25;
    private PQ<Transaction> pending;
    private PQ<Transaction> queued;
    private List<TxOutput> unspentOuts;
    /**
     * constructs the tx manager
     */
    public TransactionManager() {
        pending = new PQ<Transaction>(new TxComparator<Transaction>());
        queued = new PQ<Transaction>(new TxComparator<Transaction>());
        unspentOuts = new ArrayList<TxOutput>();
    }
    /**
     * checks if block is valid
     * @param newBlock block to be checked
     * @return boolean true or false
     */
    public boolean isBlockValid(Block newBlock) {
        List<Transaction> txs = newBlock.getTransactions();
        List<TxOutput> outs = new ArrayList<TxOutput>();
        List<TxInput> ins = new ArrayList<TxInput>();
        int outputAmt = newBlock.getTransactions().get(0)
                .getOutputs().get(0).getAmount();
        int txFee = 0;
        if (!txs.get(0).getInputs().isEmpty()) {
            return false;
        }
        for (Transaction tx : newBlock.getTransactions()) {
            outs.addAll(tx.getOutputs());
            ins.addAll(tx.getInputs());
        }
        for (int i = 1; i < txs.size(); i++) {
            txFee += txs.get(i).getTransactionFee();
        }
        if (BLOCK_REWARD + txFee != outputAmt) {
            return false;
        }
        boolean exists = false;
        for (int i = 0; i < unspentOuts.size(); i++) {
            for (int j = 0; j < ins.size(); j++) {
                if (ins.get(j).getAddress().equals(unspentOuts
                        .get(i).getAddress())) {
                    exists = true;
                }
            }
        }
        if (!exists) {
            return exists;
        }

        boolean isSameAmt = false;
        for (int i = 0; i < unspentOuts.size(); i++) {
            for (int j = 0; j < ins.size(); j++) {
                if (ins.get(j).getAmount() == unspentOuts.get(i).getAmount()) {
                    isSameAmt = true;
                }
            }
        }
        if (!isSameAmt) {
            return isSameAmt;
        }

        return true;
    }
    /**
     * executes the block
     * @param block to be executed
     */
    public void executeBlock(Block block) {
        boolean found = false;
        List<TxInput> ins = new ArrayList<TxInput>();
        for (Transaction tx : block.getTransactions()) {
            ins.addAll(tx.getInputs());
            unspentOuts.addAll(tx.getOutputs());

            for (int i = 0; i < pending.size() && !found; i++) {
                if (tx.getId().equals(pending.getPQAsList().get(i).getId())) {
                    pending.getPQAsList().remove(i);
                    pending.offer(queued.poll());
                    found = true;
                }
            }

            found = false;

            for (int i = 0; i < queued.size() && !found; i++) {
                if (tx.getId().equals(queued.getPQAsList().get(i).getId())) {
                    queued.getPQAsList().remove(i);
                    found = true;
                }
            }

        }
        if (!ins.isEmpty() && !unspentOuts.isEmpty()) {
            for (int i = 0; i < ins.size(); i++) {
                boolean unmatched = true;
                for (int j = 0; j < unspentOuts.size() && unmatched; j++) {
                    if (ins.get(i).getAddress()
                            .equals(unspentOuts.get(j).getAddress())) {
                        ins.get(i).setSpentOutput(unspentOuts.get(j));
                        int newBalance = ins.get(i).getAmount() -
                                unspentOuts.get(j).getAmount();
                        unspentOuts.set(j, new TxOutput(ins.get(i)
                                .getAddress(), newBalance));
                        unmatched = false;
                    }
                }
            }
        }
    }
    /**
     * undoes the queue
     * @param block to be undid
     */
    private void undoQueue(Block block) {
        List<TxOutput> outs = new ArrayList<TxOutput>();
        for (Transaction tx : block.getTransactions()) {
            outs.addAll(tx.getOutputs());
        }
        List<Transaction> toBeRemoved = new ArrayList<Transaction>();
        for (int i = 0; i < queued.size(); i++) {
            String addr = queued.getPQAsList().get(i)
                    .getInputs().get(0).getAddress();
            if (outs.get(i).getAddress().equals(addr)) {
                toBeRemoved.add(queued.getPQAsList().get(i));
            }
        }
        queued.removeAll(toBeRemoved);
    }
    /**
     * undoes the pending
     * @param block to be undid
     */
    private void undoPending(Block block) {
        List<TxOutput> outs = new ArrayList<TxOutput>();
        for (Transaction tx : block.getTransactions()) {
            outs.addAll(tx.getOutputs());
        }
        List<Transaction> toBeRemoved = new ArrayList<Transaction>();
        for (int i = 0; i < pending.size(); i++) {
            String addr = pending.getPQAsList().get(i)
                    .getInputs().get(0).getAddress();
            for (int j = 0; j < outs.size(); j++) {
                if (outs.get(j).getAddress().equals(addr)) {
                    toBeRemoved.add(pending.getPQAsList().get(i));
                }
            }
        }
        pending.removeAll(toBeRemoved);

    }
    /**
     * undoes the UTXO
     * @param block to be undid
     */
    private void undoUTXO(Block block) {
        List<TxOutput> outs = new ArrayList<TxOutput>();
        for (Transaction tx : block.getTransactions()) {
            outs.addAll(tx.getOutputs());
        }
        for (int i = 0; i < unspentOuts.size(); i++) {
            for (int j = 0; j < outs.size(); j++) {
                if (unspentOuts.get(i).getAddress()
                        .equals(outs.get(j).getAddress())) {
                    unspentOuts.remove(i);
                }
            }
        }
    }
    /**
     * undoes the block
     * @param block to be undid
     */
    public void undoBlock(Block block) {
        List<TxInput> ins = new ArrayList<TxInput>();
        for (Transaction tx : block.getTransactions()) {
            ins.addAll(tx.getInputs());
        }
        undoUTXO(block);

        undoPending(block);

        undoQueue(block);

        if (!ins.isEmpty() && !unspentOuts.isEmpty()) {
            for (int i = 0; i < ins.size(); i++) {
                boolean unmatched = true;
                for (int j = 0; j < unspentOuts.size() && unmatched; j++) {
                    if (ins.get(i).getAddress()
                            .equals(unspentOuts.get(j).getAddress())) {
                        int newBalance =
                                ins.get(i).getSpentOutput().getAmount();
                        unspentOuts.set(j, new TxOutput(ins
                                .get(i).getAddress(), newBalance));
                        ins.get(i).setSpentOutput(null);
                        unmatched = false;
                    }
                }
            }
        }
    }
    /**
     * adds a tx to pending
     * @param tx to be added to pending
     */
    public void addPendingTransaction(Transaction tx) {
        boolean placed = false;
        if (pending.size() < MAX_TX_PER_BLOCK) {
            pending.offer(tx);
            placed = true;
        }
        else {
            for (int i = 1; i < pending.size() && !placed; i++) {
                if (pending.getPQAsList().get(i).getTransactionFee() <
                        tx.getTransactionFee()) {
                    Transaction toBeQueued = pending.getPQAsList().get(i);
                    pending.remove(toBeQueued);
                    pending.offer(tx);
                    queued.offer(toBeQueued);
                    placed = true;
                }
            }
        }
        if (!placed && !queued.contains(tx)) {
            queued.offer(tx);
        }
    }
    /**
     * gets the txs for next block
     * @return List<Transaction> txs with top 3 tx fees
     */
    public List<Transaction> getTransactionsForNextBlock() {
        return pending.getPQAsList();
    }
    /**
     * gets the queued txs
     * @return List<Transaction> the txs that are not in pending
     */
    public List<Transaction> getQueuedTransactions() {
        return queued.getPQAsList();
    }
    /**
     * gets the outputs in UTXO
     * @param addr the addr to be compared
     * @return int the amount in this addr
     */
    public int getUTXO(String addr) {
        for (int i = 0; i < unspentOuts.size(); i++) {
            if (unspentOuts.get(i).getAddress().equals(addr)) {
                return unspentOuts.get(i).getAmount();
            }
        }
        return 0;
    }
    /**
     * gets the pq
     * @return PQ<Transaction> top 3 priorities
     */
    public PQ<Transaction> getPQ() {
        return pending;
    }
}
