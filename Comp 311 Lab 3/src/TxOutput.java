/**
 * @author jacob
 * @version 7/16/19
 */
public class TxOutput extends TxIO {
    /**
     * constructs the tx output
     * @param address the addr of output
     * @param amount held by output
     */
    public TxOutput(String address, int amount) {
        super(address, amount);
    }
}
