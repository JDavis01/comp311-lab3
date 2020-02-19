import java.util.Comparator;
/**
 * @author jacob
 * @version 7/16/19
 * @param <E> elements to be compared
 */
public class TxComparator<E> implements Comparator<E> {
    /**
     * compares based on priority of tx fee
     * @param o1 compared with o2
     * @param o2 compared with o1
     * @return int negative 0 or positive based on the biggest fee
     */
    @Override
    public int compare(E o1, E o2) {
        Transaction tx1 = (Transaction) o1;
        Transaction tx2 = (Transaction) o2;
        Integer fee1 = tx1.getTransactionFee();
        Integer fee2 = tx2.getTransactionFee();

        return fee1.compareTo(fee2);
    }

}
