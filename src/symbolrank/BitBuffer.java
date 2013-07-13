package symbolrank;

public class BitBuffer {
    /* FIFO storage for bit sequences */
    private long storage;
    private int filled;
    BitBuffer() {
        storage = 0L;
        filled = 0;
    }
    private long truncate(long data, int count) {
        return (count == 0) ? 0L : (data & (~0L >>> (0100 - count)));
    }
    public boolean put(long data, int count) {
        storage = (storage << count) | truncate(data, count);
        return (filled += count) > 0100; // indicate overflow status
    }
    public long get(int count) {
        return truncate(storage >>> (filled -= count), count);
    }
    public int has() {
        return filled;
    }
    public int spare() {
        return 0100 - filled;
    }
    public String debug() {
        return "0" +
                Integer.toOctalString(filled) +
                " bits of 0x" +
                Long.toHexString(storage);
    }
}
