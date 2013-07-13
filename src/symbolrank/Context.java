package symbolrank;

public class Context {
    public int past;
    Context() {
        past = 0;
    }
    public void update(int unit) {
        past <<= 010;
        past |= (unit & 0xff);
        past &= 0xffffff; // only keep three bytes worth of context info
    }
    @Override public int hashCode() {
        return past;
    }
    @Override public boolean equals(Object other) {
        if(this == other) return true;
        if(!(other instanceof Context)) return false;
        Context oc = (Context)other;
        return past == oc.past;
    }
}
