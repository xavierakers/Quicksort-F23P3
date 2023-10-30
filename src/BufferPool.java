import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Xavier AKers
 * 
 * @version Last Updated
 * 
 * @since 10-16-2023
 * 
 *        BufferPool implementing message-passing interface
 *        Implementing LRU updating
 */
public class BufferPool {
    private final int BUFFER_SIZE;
    private Buffer[] pool;
    private int numBuffers;
    private RandomAccessFile raf;
    private byte[] taxi;

    private int[] cacheHits;
    private int[] diskReads;
    private int[] diskWrites;

    public BufferPool(
        int numBuffers,
        int blockSize,
        String filePath,
        int[] cacheHits,
        int[] diskReads,
        int[] diskWrites) {

        this.cacheHits = cacheHits;
        this.diskReads = diskReads;
        this.diskWrites = diskWrites;

        this.BUFFER_SIZE = blockSize;
        this.numBuffers = numBuffers;
        this.taxi = new byte[4];
        try {
            this.raf = new RandomAccessFile(filePath, "rw");
            this.pool = new Buffer[numBuffers];

            for (int i = 0; i < numBuffers; i++) {
                pool[i] = new Buffer(BUFFER_SIZE);
            }
        }
        catch (Exception e) {
            System.out.println("error in constructor");
            e.printStackTrace();
        }
    }


    public void insert(byte[] space, int size, int pos) throws Exception {
        int blockNum = pos / BUFFER_SIZE;
        int relPos = pos - (blockNum * BUFFER_SIZE);

        Buffer buff = null;
        int bufferIndex = numBuffers - 1;
        for (int i = 0; i < numBuffers; i++) {
            if (pos >= pool[i].getPos() && pos < pool[i].getPos() + BUFFER_SIZE
                && pool[i].getPos() >= 0) {
                buff = pool[i];
                bufferIndex = i;
                cacheHits[0]++;
            }
        }
        updateLRU(bufferIndex);
        buff = pool[0];
        buff.setIsDirty(true);
        System.arraycopy(space, 0, buff.getData(), relPos, size);
        // buff.insertBytes(space, size, relPos);
    }


    public void getBytes(byte[] space, int size, int pos) throws Exception {
        
        int blockNum = pos / BUFFER_SIZE;
        int relPos = pos - (blockNum * BUFFER_SIZE);
        // finding a possible used buffer
        Buffer buff = null;
        int buffIndex = numBuffers - 1;
        for (int i = 0; i < numBuffers; i++) {
            if (pos >= pool[i].getPos() && pos < pool[i].getPos() + BUFFER_SIZE
                && pool[i].getPos() >= 0) {

                buff = pool[i];
                buffIndex = i;
                cacheHits[0]++;

            }
        }

        if (buff == null) {
            updateLRU(buffIndex);
            buff = pool[0];
            buff.setPos(blockNum * BUFFER_SIZE);
            buff.setIsDirty(false);
            raf.seek(buff.getPos());
            raf.read(buff.getData());
            diskReads[0]++;
            // raf.read(buff.getData(), buff.getPos(), BUFFER_SIZE);
        }

        buff.getBytes(space, size, relPos);
    }


    /**
     * Moves buffer from the end of the pool to the front
     * Writes to RAF if is dirty
     * 
     * @throws Exception
     */
    private void updateLRU(int index) throws Exception {
        Buffer buff = pool[index];
        for (int i = index; i > 0; i--) {
            pool[i] = pool[i - 1];
        }
        pool[0] = buff;
        if (buff != null) {
            if (buff.isDirty()) {
                raf.seek(buff.getPos());
                raf.write(buff.getData());
                diskWrites[0]++;
            }
        }
    }


    public byte[] getPivot(int pivotIndex) throws Exception {
        getBytes(taxi, 4, pivotIndex);
        return taxi;
    }


    public void flushAll() throws IOException {
        for (int i = 0; i < pool.length; i++) {
            raf.seek(pool[i].getPos());
            raf.write(pool[i].getData());
            diskWrites[0]++;
        }
        raf.close();
    }


    public void printBuffers() {
        System.out.println();
        for (int i = 0; i < numBuffers; i++) {
            String str = new String(pool[i].getData());
            // System.out.println("Buffer: " + i);
            System.out.println("Buffer: " + pool[i].getPos());
            System.out.println(str);
        }

    }


    public int getSize() throws IOException {
        return (int)raf.length();
    }
}
