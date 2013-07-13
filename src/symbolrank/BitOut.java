package symbolrank;
import java.io.*;

public class BitOut {
    private BufferedOutputStream file;
    private BitBuffer bits;
    BitOut(String path) throws IOException {
        file = new BufferedOutputStream(new FileOutputStream(path));
        bits = new BitBuffer();
    }
    private void dumpbits() throws IOException {
        while(bits.has() >= 010) file.write((int)bits.get(010));
    }
    public void put(long data, int count) throws IOException {
        // Write count bits of data to the file
        // count must be < 0100
        int s = bits.spare();
        if(count > s) { // Not enough room in the BitBuffer for this data
            bits.put(data >>> (count -= s), s); // Fill the spare space
            dumpbits(); // Flush to file
        }
        bits.put(data, count);
    }
    // Pad with a bit until the BitBuffer can be emptied
    public void end(boolean fillbit) throws IOException {
        int extra = bits.has() % 010;
        if(extra > 0) {
            bits.put(fillbit ? ~0L : 0L, 010 - extra);
        }
        dumpbits();
    }
    public void close() throws IOException {
        dumpbits();
        file.close();
    }
}
