import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * @author jacob
 * @version 7/16/19
 */
public class PQTest {

    private Transaction tx1;
    private Transaction tx2;
    private Transaction tx3;
    private Transaction tx4;
    private PQ<Transaction> pq;
    /**
     * sets up some tests
     */
    @Before
    public void setUp() {
        tx1 = new Transaction("1");
        tx2 = new Transaction("2");
        tx3 = new Transaction("3");
        tx4 = new Transaction("4");

        pq = new PQ<Transaction>(new TxComparator<Transaction>());

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

        pq.offer(tx1);
        pq.offer(tx2);
        pq.offer(tx3);
        pq.offer(tx4);
    }
    /**
     * tests offer
     */
    @Test
    public void testOffer() {
        assertEquals(5, tx1.getTransactionFee());
        assertEquals(120, tx2.getTransactionFee());
        assertEquals(25, tx3.getTransactionFee());
        assertEquals(0, tx4.getTransactionFee());

        assertEquals(tx2, pq.peek());
    }
    /**
     * tests poll
     */
    @Test
    public void testPoll() {
        assertEquals(tx2, pq.poll());
        assertEquals(tx3, pq.peek());

        assertEquals(tx3, pq.poll());
        assertEquals(tx1, pq.poll());
        assertEquals(tx4, pq.peek());

        pq.poll();

        assertNull(pq.peek());
    }
    /**
     * tests remove
     */
    @Test
    public void testRemove() {
        assertTrue(pq.remove(tx3));
        assertFalse(pq.remove(tx3));
    }
}
