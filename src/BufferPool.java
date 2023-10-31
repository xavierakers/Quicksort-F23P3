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
    private int bufferSize;
    private Buffer[] pool;
    private int numBuffers;
    private RandomAccessFile raf;
    private byte[] taxi;

    private int[] cacheHits;
    private int[] diskReads;
    private int[] diskWrites;

    /**
     * Constructor
     * 
     * @param numBuffers
     *            Number of buffers within the pool
     * @param blockSize
     *            Size of each data block/buffer
     * @param filePath
     *            Data filename
     * @param cacheHits
     *            Stores number of cacheHits
     * @param diskReads
     *            Stores number of diskReads
     * @param diskWrites
     *            Stores number of diskWrites
     * @throws Exception
     */
    public BufferPool(
        int numBuffers,
        int blockSize,
        String filePath,
        int[] cacheHits,
        int[] diskReads,
        int[] diskWrites)
        throws Exception {

        this.cacheHits = cacheHits;
        this.diskReads = diskReads;
        this.diskWrites = diskWrites;
        this.bufferSize = blockSize;
        this.numBuffers = numBuffers;
        this.taxi = new byte[4];
        this.raf = new RandomAccessFile(filePath, "rw");
        this.pool = new Buffer[numBuffers];

        for (int i = 0; i < numBuffers; i++) {
            pool[i] = new Buffer(bufferSize);
        }
    }


    /**
     * Insert new bytes into data pool
     * 
     * @param space
     *            Array holding new bytes
     * @param size
     *            Number of bytes to insert
     * @param pos
     *            Absolute position of bytes to insert
     * @throws Exception
     */
    public void insert(byte[] space, int size, int pos) throws Exception {
        int blockNum = pos / bufferSize;
        int relPos = pos - (blockNum * bufferSize);

        Buffer buff = findBuffer(pos, blockNum);
        buff.setIsDirty(true);
        System.arraycopy(space, 0, buff.getData(), relPos, size);
    }


    /**
     * Retrieve bytes from the data
     * 
     * @param space
     *            Array to carry requested data
     * @param size
     *            Number of bytes requested
     * @param pos
     *            Absolute position of bytes requested
     * @throws Exception
     */
    public void getBytes(byte[] space, int size, int pos) throws Exception {

        int blockNum = pos / bufferSize;
        int relPos = pos - (blockNum * bufferSize);
        Buffer buff = findBuffer(pos, blockNum);
        buff.getBytes(space, size, relPos);
    }


    /**
     * Finds necessary buffer
     * 
     * @param pos
     *            Relative position of data requested
     * @param blockNum
     *            Block number data is in
     * @return Buffer containing data, or LRU Buffer
     * @throws Exception
     */
    public Buffer findBuffer(int pos, int blockNum) throws Exception {
        Buffer buff = null;
        int bufferIndex = numBuffers - 1;
        for (int i = 0; i < numBuffers; i++) {
            if (pos >= pool[i].getPos() && pos < pool[i].getPos() + bufferSize
                && pool[i].getPos() >= 0) {
                buff = pool[i];
                bufferIndex = i;
                cacheHits[0]++;
            }
        }

        updateLRU(bufferIndex, blockNum * bufferSize);
        if (buff == null) {
            buff = pool[0];
            buff.setPos(blockNum * bufferSize);
            buff.setIsDirty(false);
            raf.seek(buff.getPos());
            raf.read(buff.getData());
            diskReads[0]++;
        }
        return buff;
    }


    /**
     * Moves buffer from the end of the pool to the front
     * Writes to RAF if is dirty
     * 
     * @param index
     *            Buffer index
     * @param pos
     *            New position needed in the data file
     * @throws Exception
     */
    private void updateLRU(int index, int pos) throws Exception {
        Buffer buff = pool[index];
        for (int i = index; i > 0; i--) {
            pool[i] = pool[i - 1];
        }

        pool[0] = buff;
        if (buff != null && index == numBuffers - 1 && buff.getPos() != pos) {
            if (buff.isDirty()) {
                raf.seek(buff.getPos());
                raf.write(buff.getData());
                diskWrites[0]++;
            }
        }
    }


    /**
     * Gets the pivot of the current partition
     * 
     * @param pivotIndex
     *            Index of the pivot
     * @return array of bytes
     * @throws Exception
     */
    public byte[] getPivot(int pivotIndex) throws Exception {
        getBytes(taxi, 4, pivotIndex);
        return taxi;
    }


    /**
     * Writes all dirty blocks back to the RAF
     * 
     * @throws IOException
     */
    public void flushAll() throws IOException {
        for (int i = 0; i < pool.length; i++) {
            if (pool[i].isDirty() && pool[i] != null) {
                raf.seek(pool[i].getPos());
                raf.write(pool[i].getData());
                diskWrites[0]++;
            }
        }
        raf.close();
    }


    /**
     * Prints contents of the buffers
     */
    public void printBuffers() {
        System.out.println();
        for (int i = 0; i < numBuffers; i++) {
            String str = new String(pool[i].getData());
            System.out.println("Buffer: " + pool[i].getPos());
            System.out.println(str);
        }

    }


    /**
     * @return Absolute size of the data set
     * @throws IOException
     */
    public int getSize() throws IOException {
        return (int)raf.length();
    }
}
