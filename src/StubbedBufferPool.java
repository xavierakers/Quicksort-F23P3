import java.io.RandomAccessFile;

/**
 * @author Xavier Akers
 * 
 * @version Last Updated 10-19-2023
 * 
 * @since 10-16-2023
 * 
 *        BufferPool implementing message-passing interface
 *        Implementing LRU
 */
public class StubbedBufferPool {

    private RandomAccessFile raf;
    private byte[] stubbedArray;
    private byte[] taxi;

    /**
     * Constructor
     * 
     * @param numBuffers
     *            the number of buffers in the pool
     * @param blockSize
     *            the size of each block
     *            in this scenario, it is constant 4096
     * @param filePath
     *            input data file
     */
    public StubbedBufferPool(int numBuffers, int blockSize, String filePath) {
        try {
            raf = new RandomAccessFile(filePath, "rw");
            stubbedArray = new byte[(int)raf.length()];
            raf.readFully(stubbedArray);
        }
        catch (Exception e) {
            System.out.println("error in constrcutor");
            e.printStackTrace();
        }
        
        this.taxi = new byte[4];
    }


    /**
     * Copy size bytes form space to position in the buffered storage
     * 
     * @param space
     *            array holding data to insert
     * @param size
     *            the amount of data
     * @param pos
     *            where to insert data in buffered storage
     */
    public void insert(byte[] space, int size, int pos) {
        try {
            if (pos >= 0 && size > 0 && pos + size <= stubbedArray.length) {
                System.arraycopy(space, 0, stubbedArray, pos, size);
                return;
            }
        }
        catch (Exception e) {
            System.out.println("error in insert");
        }
    }


    /**
     * Copy size bytes from position pos of the buffered storage to space
     * 
     * @param space
     *            array retrieving the data
     * @param size
     *            the amount of data
     * @param pos
     *            where to get data in buffered storage
     */
    public void getBytes(byte[] space, int size, int pos) {
        try {
            if (pos >= 0 && size > 0 && pos + size <= stubbedArray.length) {
                System.arraycopy(stubbedArray, pos, space, 0, size);
                return;
            }
        }
        catch (Exception e) {
            System.out.println("error in getBytes");
        }

    }


    /**
     * Gets the record at the pivot within buffered storage
     * 
     * @param pivotIndex
     *            the absolute index of the pivot
     * @return the pivot KV
     */
    public byte[] getPivot(int pivotIndex) {
        getBytes(taxi, 4, pivotIndex);
        return taxi;
    }


    /**
     * @return the length of the bufferPool
     */
    public int getSize() {
        return stubbedArray.length;
    }


    /**
     * Prints contents of the stubbedBuffer
     */
    public void printStub() {
        for (int i = 0; i < stubbedArray.length; i++) {
            System.out.print((char)stubbedArray[i]);
        }
        System.out.println();
    }


    /**
     * Writes modified contents of the bufferPool back to the disk
     */
    public void flush() {
        System.out.println("Flushing");
        try {
            raf.seek(0);
            raf.write(stubbedArray);
            raf.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
