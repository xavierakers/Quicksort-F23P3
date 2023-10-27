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
    private RandomAccessFile raf;
    private Buffer[] pool;
    private int numBuffers;
    private byte[] taxi;

    public BufferPool(int numBuffers, int blockSize, String filePath) {
        this.BUFFER_SIZE = blockSize;
        try {
            this.numBuffers = numBuffers;
            this.raf = new RandomAccessFile(filePath, "rw");
            this.taxi = new byte[BUFFER_SIZE];
            pool = new Buffer[numBuffers];
            for (int i = 0; i < numBuffers; i++) {
                pool[i] = new Buffer(BUFFER_SIZE);
            }

        }
        catch (Exception e) {
            System.out.println("error in constructor");
            e.printStackTrace();
        }
    }


    public void insert(byte[] space, int size, int pos) throws IOException {
        int blockNum = pos / BUFFER_SIZE;
        int relPos = pos - (blockNum * BUFFER_SIZE);

        Buffer buff = null;
        // checking if block is already stored in bufferPool
        for (int i = 0; i < numBuffers; i++) {
            if (pool[i].getBlockID() == blockNum) {
                buff = pool[i];
                break;
            }
        }
        // sector not already buffed, get LRU
        if (buff == null) {
            buff = pool[numBuffers - 1];
            flush(numBuffers - 1);
            buff.setBlockID(blockNum);
            raf.seek(blockNum * BUFFER_SIZE);
            raf.read(buff.getData(), 0, BUFFER_SIZE);
            buff.setBlockID(blockNum);
            buff.setPos(blockNum * BUFFER_SIZE);
        }
        for (int i = numBuffers - 1; i > 0; i--) {
            pool[i] = pool[i - 1];
        }
        pool[0] = buff;
        System.arraycopy(space, 0, buff.getData(), relPos, size);
        buff.setIsDirty(true);
    }


    public void getBytes(byte[] space, int size, int pos) throws IOException {
        int blockNum = pos / BUFFER_SIZE;
        int relPos = pos - (blockNum * BUFFER_SIZE);

        Buffer buff = null;
        // checking if block is already stored in buffer
        for (int i = 0; i < numBuffers; i++) {
            if (pool[i].getBlockID() == blockNum) {
                // System.out.println("found buffer");
                buff = pool[i];
                break;
            }
        }
        // sector not already buffed, get LRU
        if (buff == null) {
            // System.out.println("loading new buffer");
            buff = pool[numBuffers - 1];
            flush(numBuffers - 1);
            buff.setBlockID(blockNum);
            raf.seek(blockNum * BUFFER_SIZE);
            raf.read(buff.getData(), 0, BUFFER_SIZE);
            buff.setBlockID(blockNum);
            buff.setPos(blockNum * BUFFER_SIZE);

        }
        for (int i = numBuffers - 1; i > 0; i--) {
            pool[i] = pool[i - 1];
        }
        pool[0] = buff;
        System.arraycopy(buff.getData(), relPos, space, 0, size);
        // printBuffers();
    }


    public byte[] getPivot(int pivotIndex) throws Exception {
        getBytes(taxi, 4, pivotIndex);
        return taxi;
    }


    public void flush(int buffNum) throws IOException {
        if (buffNum >= 0 && pool[buffNum].isDirty()) {
            raf.seek(pool[buffNum].getPos());
            raf.write(pool[buffNum].getData());
        }
    }


    public void flushAll() throws IOException {
        for (int i = 0; i < pool.length; i++) {
            raf.seek(pool[i].getPos());
            raf.write(pool[i].getData());
        }
        raf.close();
    }


    public void printBuffers() {
        System.out.println();
        for (int i = 0; i < numBuffers; i++) {
            String str = new String(pool[i].getData());
            System.out.println("Buffer: " + pool[i].getBlockID());
            System.out.println(str);

        }

    }


    public int getSize() throws IOException {
        return (int)raf.length();
    }
}
