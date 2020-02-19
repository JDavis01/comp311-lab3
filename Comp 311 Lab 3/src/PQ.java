import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
/**
 * @author jacob
 * @version 7/16/19
 * @param <E> elements to be held by this
 */
public class PQ<E> extends AbstractQueue<E> {
    private Comparator<E> comp;
    private List<E> elems;
    /**
     * constructs pq
     * @param comp compares elements
     */
    public PQ(Comparator<E> comp) {
        elems = new ArrayList<E>();
        this.comp = comp;
    }
    /**
     * adds elem to pq checking for priority
     * @param elem the element added
     * @return boolean returns true if added
     */
    @Override
    public boolean offer(E elem) {
        int pos = elems.size();
        elems.add(elem);

        while (pos > 0) {
            int par = (pos + 1) / 2 - 1;
            E parent = elems.get(par);
            E child = elems.get(pos);
            if (comp.compare(parent, child) >= 0) {
                break;
            }
            swapParentAndChild(parent, child);
            pos = par;
        }

        return true;
    }

    /**
     * swaps parent with the child
     * @param parent parent to be swapped
     * @param child child to be swapped
     */
    private void swapParentAndChild(E parent, E child) {
        int tempP = elems.indexOf(parent);
        int tempC = elems.indexOf(child);
        elems.set(tempP, child);
        elems.set(tempC, parent);
    }

    /**
     * returns but not remove first element
     * @return E the element
     */
    @Override
    public E peek() {
        if (elems.size() == 0) {
            return null;
        }
        return elems.get(0);
    }

    /**
     * returns and removes first element
     * @return E the first element
     */
    @Override
    public E poll() {
        if (elems.size() == 0) {
            return null;
        }

        E priority = elems.get(0);
        elems.set(0, elems.get(elems.size() - 1));

        int pos = 0;
        while (pos < elems.size() / 2) {
            int leftC = pos * 2 + 1;
            int rightC = leftC + 1;
            E parent = elems.get(pos);
            E leftChild = elems.get(leftC);
            E rightChild = null;
            if (rightC < elems.size()) {
                rightChild = elems.get(rightC);
            }
            if (rightC < elems.size() && comp.compare(
                    leftChild, rightChild) < 0) {
                if (comp.compare(parent, rightChild) >= 0) {
                    break;
                }
                swapParentAndChild(parent, rightChild);
                pos = rightC;
            }
            else {
                if (comp.compare(parent, leftChild) >= 0) {
                    break;
                }
                swapParentAndChild(parent, leftChild);
                pos = leftC;
            }
        }
        elems.remove(elems.size() - 1);
        return priority;
    }

    /**
     * removes argument
     * @param o the param to be removed
     * @return boolean true if removed
     */
    @Override
    public boolean remove(Object o) {
        if (!elems.contains(o)) {
            return false;
        }
        boolean isRemoved = false;
        int pos = elems.indexOf(o);
        int leftC = pos * 2 + 1;
        int rightC = leftC + 1;
        E parent = elems.get(pos);
        E leftChild = null;
        E rightChild = null;
        if (leftC < elems.size()) {
            leftChild = elems.get(leftC);
        }
        if (rightC < elems.size()) {
            rightChild = elems.get(rightC);
        }
        if (rightC < elems.size() && comp.compare(leftChild, rightChild) < 0) {
            swapParentAndChild(parent, rightChild);
            isRemoved = elems.remove(parent);
        }
        else if (leftC < elems.size()) {
            swapParentAndChild(parent, leftChild);
            isRemoved = elems.remove(parent);
        }
        else {
            isRemoved = elems.remove(parent);
        }

        return isRemoved;
    }

    /**
     * returns pq as a list
     * @return List<E> the list
     */
    public List<E> getPQAsList() {
        return elems;
    }
    /**
     * returns an iterator
     * @return Iterator<E> the iterator
     */
    @Override
    public Iterator<E> iterator() {
        return elems.iterator();
    }

    /**
     * returns the size
     * @return int the size
     */
    @Override
    public int size() {
        return elems.size();
    }
}
