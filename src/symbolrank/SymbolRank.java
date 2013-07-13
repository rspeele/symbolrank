package symbolrank;
import java.io.*;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.ProgressMonitor;
import javax.swing.JOptionPane;

public class SymbolRank {
    static final String fileExtension = ".cmp";
    static final int progressChunks = 0x1000; // longs are converted to ints inside this range for progress bars
    public static Compressor compress(String inpath, String outpath) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(inpath));
        BitOut out = new BitOut(outpath);
        Compressor cmp = new Compressor();
        long totalSize = new File(inpath).length();
        ProgressMonitor monitor = new ProgressMonitor(null, "Compressing " + inpath, "", 0, progressChunks);
        monitor.setMillisToDecideToPopup(125);
        monitor.setMillisToPopup(250);
        monitor.setProgress(0);
        long start = new Date().getTime();
        for(long count = 0;; count++) {
            int b = in.read();
            if(b < 0 || monitor.isCanceled()) break;
            cmp.compress((int)b, out);
            if(count == (count & ~0xfffL)) { // only update the progress bar once per 4KB block
                monitor.setProgress((int)(count * progressChunks / totalSize));
            }
        }
        cmp.end(out);
        in.close();
        out.close();
        long end = new Date().getTime();
        monitor.close();
        String msg = String.format(
            "Compression to %s complete.\n" +
            "Input: %d KB\n" +
            "Output: %d KB\n" +
            "Compressed: %d%%\n" +
            "Time: %.1fs",
            outpath,
            cmp.expandedSize() / 1024,
            cmp.compressedSize() / 1024,
            cmp.compressionRatio(),
            (end - start) * 0.001
        );
        JOptionPane.showMessageDialog(null, msg);
        return cmp;
    }
    public static Compressor decompress(String inpath, String outpath) throws IOException {
        BitIn in = new BitIn(inpath);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outpath));
        Compressor cmp = new Compressor();
        long totalSize = new File(inpath).length();
        ProgressMonitor monitor = new ProgressMonitor(null, "Decompressing " + inpath, "", 0, progressChunks);
        monitor.setMillisToDecideToPopup(125);
        monitor.setMillisToPopup(250);
        monitor.setProgress(0);
        long start = new Date().getTime();
        for(long count = 0;; count++) {
            int c = cmp.decompress(in);
            if(c < 0 || monitor.isCanceled()) break;
            out.write(c);
            if(count == (count & ~0xfffL)) { // update the progress once per 4KB of decompressed data
                monitor.setProgress((int)(in.tell() * progressChunks / totalSize));
            }
        }
        in.close();
        out.close();
        long end = new Date().getTime();
        monitor.close();
        String msg = String.format(
            "Decompression to %s complete.\n" +
            "Input: %d KB\n" +
            "Output: %d KB\n" +
            "Expanded: %d%%\n" +
            "Time: %.1fs",
            outpath,
            cmp.compressedSize() / 1024,
            cmp.expandedSize() / 1024,
            cmp.expansionRatio(),
            (end - start) * 0.001
        );
        JOptionPane.showMessageDialog(null, msg);
        return cmp;
    }
    public static void main(String[] args) throws IOException {
        JFileChooser choose = new JFileChooser();
        if(choose.showOpenDialog(null) != 0) return;
        String inpath = choose.getSelectedFile().getPath();
        if(inpath.endsWith(fileExtension)) {
            decompress(inpath, inpath.substring(0, inpath.lastIndexOf(fileExtension)));
        } else {
            compress(inpath, inpath + fileExtension);
        }
    }
}
