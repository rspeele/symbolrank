package symbolrank;

import java.util.HashMap;
import java.io.*;

class CompStats {
    protected long F;
    protected long[] L;
    protected static final int[] Lsizes = { 1, 2, 3, 4 };
    protected static final int Fsize = 12;
    CompStats () {
        L = new long[4];
        L[0] = L[1] = L[2] = L[3] = F = 0;
    }
    private void show(String name, long i, int b) {
        System.out.println(name +
                " : " +
                Long.toString(i) +
                " matches taking " +
                Long.toString(i * b / 0x2000) +
                " KB");
    }
    public long expandedSize() {
        return L[0] + L[1] + L[2] + L[3] + F;
    }

    public long compressedSize() {
        return (  L[0] * Lsizes[0]
                + L[1] * Lsizes[1]
                + L[2] * Lsizes[2]
                + L[3] * Lsizes[3]
                + F * Fsize) / 010;
    }
    public long compressionRatio() {
        return 100 * compressedSize() / expandedSize();
    }
    public long expansionRatio() {
        return 100 * expandedSize() / compressedSize();
    }
}

public class Compressor extends CompStats {
    private HashMap<Context, Cache> dictionary;
    private Context context;
//    private CompStats stats;
    private static final boolean debug = false;
    Compressor() {
        dictionary = new HashMap<Context, Cache>();
        context = new Context();
    }
    private Cache currentContextCache() {
        Cache likely = dictionary.get(context);
        if(null == likely) {
            likely = new Cache();
            dictionary.put(context, likely);
        }
        return likely;
    }
    private void debuginf(int level, int c, boolean in) {
        if(!debug) return;
        String out = (in ? "IN :" : "OUT:") + (level < 0 ? "Literal  : " : "Cached L" + level + ": ");
        System.out.println(out + Integer.toHexString(c) + ' ' + (char)c);
    }
    // Compress one byte of data into a BitOut
    public void compress(int c, BitOut output) throws IOException {
        Cache likely = currentContextCache();
        int level = likely.update(c);
        if(level < 0) {
            output.put(0xf00 | c, CompStats.Fsize);
            F++;
        } else {
            output.put(~1L, CompStats.Lsizes[level]);
            L[level]++;
        }
        debuginf(level, c, false);
        context.update(c);
    }
    // Align the end of the output
    public void end(BitOut output) throws IOException {
        output.end(true);
    }
    // Decompress one byte of data from a BitIn, returns -1 on EOF
    public int decompress(BitIn input) throws IOException {
        Cache likely = currentContextCache();
        int[] info = {1};
        for(int level = 0; level < 4; level++) {
            long bit = input.read(1, info);
            if(info[0] < 1) return -1; // EOF hit
            if(1 == bit) continue; // Either a higher hit level or no hits
            int cached = likely.get(level);
            likely.update(cached);
            context.update(cached);
            L[level]++;
            debuginf(level, cached, true);
            return cached;
        }
        // Not cached, was literal
        int literal = (int)input.read(8, info);
        if(info[0] != 8) return -1; // EOF hit
        likely.update(literal);
        context.update(literal);
        F++;
        debuginf(-1, literal, true);
        return literal;
    }
}
