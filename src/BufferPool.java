import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Xavier Akers
 * @author Zoe Hite
 * 
 * @version Last Updated 11-1-2023
 * 
 * @since 10-16-2023
 * 
 *        BufferPool implementing message-passing interface
 *        Implementing LRU updating
 */
public class BufferPool {
    // The size of the buffers/blocks
    private int bufferSize;
    // The array of Buffers
    private Buffer[] pool;
    // The number of Buffers
    private int numBuffers;
    // Access to the data file
    private RandomAccessFile raf;
    // Number of access to the buffers
    private int[] cacheHits;
    // Numbers of disk reads
    private int[] diskReads;
    // Number of disk Writes
    private int[] diskWrites;
    // Used to carry bytes around
    private byte[] taxi;

    /**
     * Bufferpool Constructor
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
        // Initializing the Buffers in the array
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
        // Finding the respective block
        int blockNum = pos / bufferSize;
        // Finding the position within the block
        int relPos = pos - (blockNum * bufferSize);
        // Buffer available to use
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
        // Finding the respective block
        int blockNum = pos / bufferSize;
        // Finding the position within the block
        int relPos = pos - (blockNum * bufferSize);
        // Buffer available to use
        Buffer buff = findBuffer(pos, blockNum);
        System.arraycopy(buff.getData(), relPos, space, 0, size);
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
        // Attempting to find a buffer that exists
        // If position is within the range
        for (int i = 0; i < numBuffers; i++) {
            if (pos >= pool[i].getPos() && pos < pool[i].getPos() + bufferSize
                && pool[i].getPos() >= 0) {
                buff = pool[i];
                bufferIndex = i;
                cacheHits[0]++;
            }
        }
        // Updating list by moving blockIndex to the front
        updateLRU(bufferIndex, blockNum * bufferSize);

        // If we did not find a buffer with our data loaded
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
        // Grab buffer at given index, move all buffers infront of that buffer
        // down one index
        for (int i = index; i > 0; i--) {
            pool[i] = pool[i - 1];
        }

        // replace the first buffer
        pool[0] = buff;
        // if buffer exists and does not contain the block we want
        if (buff != null && buff.getPos() != pos) {
            // if buffer is dirty
            if (buff.isDirty()) {
                raf.seek(buff.getPos());
                raf.write(buff.getData());
                buff.setIsDirty(false);
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
     * Close RAF
     * 
     * @throws IOException
     */
    public void flushAll() throws IOException {
        // iterate through all the buffers
        for (int i = 0; i < pool.length; i++) {
            // if the buffer has been edited
            if (pool[i].isDirty() && pool[i] != null) {
                raf.seek(pool[i].getPos());
                raf.write(pool[i].getData());
                diskWrites[0]++;
            }
        }
        raf.close();
    }


    /**
     * @return Absolute size of the data set
     * @throws IOException
     */
    public int getSize() throws IOException {
        return (int)raf.length();
    }
}
