package symbolrank;
import java.io.*;

public class BitIn {
    private BufferedInputStream file;
    private BitBuffer bits;
    private long bytes;
    BitIn(String path) throws FileNotFoundException {
        file = new BufferedInputStream(new FileInputStream(path));
        bits = new BitBuffer();
        bytes = 0L;
    }
    private int loadbits() throws IOException {
        // Used to refill the bit buffer when it is running low
        while(bits.spare() >= 010) {
            int sh = file.read();
            if(sh < 0) break;
            bytes++;
            bits.put(sh, 010);
        }
        return bits.has();
    }
    public long read(int n, int[] info) throws IOException {
        // Reads n bits from the file and returns them in a long
        // n must be < 0100
        // The number of bits successfully read is stored in info[0]
        // This number is less than n only if the end of the file was reached
        long out = 0L;
        int has = bits.has();
        info[0] = 0;
        if(has < n) { // Not enough bits in the BitBuffer to fulfill request
            // First step is to empty the contents of the BitBuffer into
            // the output, to make as much room as possible for loadbits().
            info[0] = has;
            out = bits.get(has);
            // Next, refill the BitBuffer, and reduce n.
            // We are still *supposed* to read (n - h) additional bits,
            // but are limited by how many we got from the file.
            n = Math.min(loadbits(), n - has);
            out <<= n;
        }
        out |= bits.get(n);
        info[0] += n;
        return out;
    }
    public long tell() {
        return bytes;
    }
    public void close() throws IOException {
        file.close();
    }
}
