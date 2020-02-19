import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author jacob
 * @version 7/16/19
 */
public class TransactionManagerTest {

    private TransactionManager txM;
    private PQ<Transaction> pq;
    private Transaction tx1;
    private Transaction tx2;
    private Transaction tx3;
    private Transaction tx4;
    /**
     * sets up for tests
     */
    @Before
    public void setUp() {
        txM = new TransactionManager();
        pq = txM.getPQ();
        tx1 = new Transaction("1");
        tx2 = new Transaction("2");
        tx3 = new Transaction("3");
        tx4 = new Transaction("4");

        tx1.addOutput(new TxOutput("out1", 5));
        tx1.addInput(new TxInput("in1", 10));
        tx2.addOutput(new TxOutput("out2", 10));
        tx2.addInput(new TxInput("in2", 100));
        tx2.addOutput(new TxOutput("out3", 20));
        tx2.addInput(new TxInput("in3", 50));
        tx3.addOutput(new TxOutput("out4", 25));
        tx3.addInput(new TxInput("in4", 50));
        tx4.addOutput(new TxOutput("out5", 100));
        tx4.addInput(new TxInput("in5", 50));
    }
    /**
     * tests adding pending txs
     */
    @Test
    public void testAddPendingTx() {
        txM.addPendingTransaction(tx1);
        assertEquals(tx1, pq.peek());

        txM.addPendingTransaction(tx2);
        assertEquals(tx2, pq.peek());
        txM.addPendingTransaction(tx3);
        assertEquals(tx2, pq.peek());
        txM.addPendingTransaction(tx4);
        assertEquals(tx2, pq.peek());
    }
    /**
     * tests getting txs for next block
     */
    @Test
    public void testGetTransactionsForNextBlock() {
        List<Transaction> arrL = new ArrayList<Transaction>();
        assertEquals(arrL, txM.getTransactionsForNextBlock());

        txM.addPendingTransaction(tx1);
        txM.addPendingTransaction(tx2);
        txM.addPendingTransaction(tx4);

        arrL.add(tx2);
        arrL.add(tx1);
        arrL.add(tx4);

        assertEquals(arrL, txM.getTransactionsForNextBlock());

        pq.offer(tx1);
        pq.offer(tx2);
        pq.offer(tx3);
        pq.offer(tx4);
        assertEquals(pq, txM.getPQ());
    }
    /**
     * tests getting queued txs
     */
    @Test
    public void testGetQueuedTransactions() {
        List<Transaction> list = new ArrayList<Transaction>();
        assertEquals(list, txM.getQueuedTransactions());

        txM.addPendingTransaction(tx1);
        txM.addPendingTransaction(tx2);
        txM.addPendingTransaction(tx3);
        assertEquals(list, txM.getQueuedTransactions());

        txM.addPendingTransaction(tx4);
        list.add(tx4);
        assertEquals(list, txM.getQueuedTransactions());

        pq.offer(tx1);
        pq.offer(tx2);
        pq.offer(tx3);
        pq.offer(tx4);
        assertEquals(pq, txM.getPQ());
    }
    /**
     * tests getting UTXO and execute and undo
     */
    @Test
    public void testGetUTXO() {
        TransactionManager tm = new TransactionManager();

        Block b = new Block("hash1");
        Transaction tx = new Transaction("cb1");
        tx.addOutput(new TxOutput("addr1", 100));
        b.addTransaction(tx);
        tm.executeBlock(b);
        assertEquals(100, tm.getUTXO("addr1"));

        b = new Block("hash2");
        tx = new Transaction("cb2");
        tx.addOutput(new TxOutput("addr2", 50));
        b.addTransaction(tx);
        tx = new Transaction("transfer");
        tx.addInput(new TxInput("addr1", 100));
        tx.addOutput(new TxOutput("addr3", 75));
        tx.addOutput(new TxOutput("addr4", 25));
        b.addTransaction(tx);
        tm.executeBlock(b);
        assertEquals(50, tm.getUTXO("addr2"));
        assertEquals(0, tm.getUTXO("addr1"));
        assertEquals(75, tm.getUTXO("addr3"));
        assertEquals(25, tm.getUTXO("addr4"));
        assertEquals(0, tm.getUTXO("addr5"));

        tm.undoBlock(b);
        assertEquals(100, tm.getUTXO("addr1"));
        assertEquals(0, tm.getUTXO("addr2"));
        assertEquals(0, tm.getUTXO("addr3"));
        assertEquals(0, tm.getUTXO("addr4"));
        assertEquals(0, tm.getUTXO("addr5"));
    }
    /**
     * tests validating a block
     */
    @Test
    public void testValidate() {
        TransactionManager tm = new TransactionManager();
        Block b = new Block("b1");
        Transaction tx = new Transaction("txb1");
        tx.addOutput(new TxOutput("1", 100));
        b.addTransaction(tx);
        tm.executeBlock(b);

        b = new Block("b2");
        tx = new Transaction("txb2");
        tx.addOutput(new TxOutput("0", 25));
        b.addTransaction(tx);
        tm.executeBlock(b);
        assertEquals(100, tm.getUTXO("1"));
        assertEquals(25, tm.getUTXO("0"));

        b = new Block("b");
        tx = new Transaction("tx");
        tx.addOutput(new TxOutput("2", 25));
        b.addTransaction(tx);

        tx = new Transaction("tx2");
        tx.addInput(new TxInput("1", 100));
        tx.addOutput(new TxOutput("3", 30));
        tx.addOutput(new TxOutput("4", 70));
        b.addTransaction(tx);

        assertTrue(tm.isBlockValid(b));

        b = new Block("b");
        tx = new Transaction("tx");
        tx.addOutput(new TxOutput("2", 25));
        b.addTransaction(tx);

        tx = new Transaction("tx2");
        tx.addInput(new TxInput("notThere", 25));
        tx.addOutput(new TxOutput("3", 25));
        b.addTransaction(tx);

        assertFalse(tm.isBlockValid(b));

        b = new Block("b");
        tx = new Transaction("tx");
        tx.addOutput(new TxOutput("2", 27));
        b.addTransaction(tx);

        assertFalse(tm.isBlockValid(b));
    }

    /**
     * random tests for webcat
     */
    @Test
    public void tests() {
        Block b = new Block("hash");
        Block bChild = new Block("hash2");
        b.addChild(bChild);

        TransactionManager tm = new TransactionManager();
        Block b1 = new Block("hash1");
        Transaction tx = new Transaction("cb1");
        tx.addInput(new TxInput("addr1", 100));
        tx.addOutput(new TxOutput("addr1", 100));
        b1.addTransaction(tx);
        assertFalse(tm.isBlockValid(b1));
    }

}
