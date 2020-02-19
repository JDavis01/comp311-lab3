
import java.util.LinkedList;
import java.util.List;
/**
 * @author jacob
 * @version 7/16/19
 */
public class Transaction {
    private String id;
    private List<TxInput> ins;
    private List<TxOutput> outs;
    /**
     * constructs a tx
     * @param id id of tx
     */
    public Transaction(String id) {
        this.id = id;
        ins = new LinkedList<TxInput>();
        outs = new LinkedList<TxOutput>();
    }
    /**
     * adds an input
     * @param input the input added
     */
    public void addInput(TxInput input) {
        ins.add(input);
    }
    /**
     * adds an output
     * @param output the output added
     */
    public void addOutput(TxOutput output) {
        outs.add(output);
    }
    /**
     * gets tx fee
     * @return int the amount for fee
     */
    public int getTransactionFee() {
        int inTotal = 0;
        int outTotal = 0;
        for (TxInput in : this.getInputs()) {
            inTotal += in.getAmount();
        }
        for (TxOutput out : this.getOutputs()) {
            outTotal += out.getAmount();
        }
        if (inTotal - outTotal < 0) {
            return 0;
        }
        return inTotal - outTotal;
    }
    /**
     * gets id
     * @return id the tx id
     */
    public String getId() {
        return id;
    }
    /**
     * gets list of inputs
     * @return List<TxInput> the inputs
     */
    public List<TxInput> getInputs() {
        return ins;
    }
    /**
     * gets list of outputs
     * @return List<TxOutput> the outputs
     */
    public List<TxOutput> getOutputs() {
        return outs;
    }
}
