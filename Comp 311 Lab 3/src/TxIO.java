/**
 * @author jacob
 * @version 7/16/19
 */
public abstract class TxIO {
    private String address;
    private int amount;
    /**
     * constructs a Tx input output
     * @param address addr of txio
     * @param amount amt held by txio
     */
    public TxIO(String address, int amount) {
        this.address = address;
        this.amount = amount;
    }
    /**
     * gets the addr of txio
     * @return String addr of txio
     */
    public String getAddress() {
        return address;
    }
    /**
     * gets the amt of txio
     * @return int the amt held by txio
     */
    public int getAmount() {
        return amount;
    }
}
