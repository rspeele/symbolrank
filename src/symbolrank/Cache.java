package symbolrank;

public class Cache {
    public int storage;
    private final int[] high = {
        0xffffff00,
        0xffff0000,
        0xff000000,
        0x00000000
    };
    private final int[] low = {
        0x00000000,
        0x000000ff,
        0x0000ffff,
        0x00ffffff
    };
    Cache() {
        storage = 0x6f617465; // e t a o
    }
    public int update(int data) { // return the hit level and modify the cache
        data &= 0xff;
        int local = storage;
        for(int i = 0; i < 4; i++) {
            if((local & 0xff) == data) {
                storage = data
                        | (storage & high[i])
                        | ((storage & low[i]) << 010);
                return i;
            }
            local >>= 010;
        }
        storage = (storage << 010) | data;
        return -1;
    }
    public int get(int level) {
        return storage >> (level * 8) & 0xff;
    }
}
