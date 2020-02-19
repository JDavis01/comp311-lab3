/**
 * @author jacob
 * @version 7/16/19
 */
public class TxInput extends TxIO {
    private TxOutput spentOutput;
    /**
     * constructs the tx input
     * @param address addr of the input
     * @param amount amt input holds
     */
    public TxInput(String address, int amount) {
        super(address, amount);
        spentOutput = null;
    }
    /**
     * gets the output spent by this
     * @return TxOutput output spent
     */
    public TxOutput getSpentOutput() {
        return spentOutput;
    }
    /**
     * sets the output spent by this
     * @param source the output spent
     */
    public void setSpentOutput(TxOutput source) {
        spentOutput = source;
    }
}
