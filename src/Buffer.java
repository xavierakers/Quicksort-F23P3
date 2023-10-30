/**
 * @author Xavier Akers
 * 
 * @version Last Updated 10-19-2023
 * 
 * @since 10-16-2023
 * 
 *        Linked Buffers Implementing a Linked Buffer Pool
 * 
 */
public class Buffer {
    private final int BUFFER_SIZE;
    private byte[] data;
    private int pos;
    private boolean isDirty;

    /**
     * Default constructor
     */
    public Buffer(int bufferSize) {
        this.BUFFER_SIZE = bufferSize;
        this.data = new byte[BUFFER_SIZE];
        this.isDirty = false;
        this.pos = -1;
    }


    public void setPos(int pos) {
        this.pos = pos;
    }


    public int getPos() {
        return pos;
    }


    public boolean isDirty() {
        return isDirty;
    }


    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }


    public byte[] getData() {
        return data;
    }


    public void setData(byte[] space) {
        System.arraycopy(space, 0, this.data, 0, BUFFER_SIZE);
    }


    /**
     * Gets bytes from byte array
     * 
     * @param space
     * @param size
     * @param pos
     */
    public void getBytes(byte[] space, int size, int pos) {
        System.arraycopy(data, pos, space, 0, size);
    }


    /**
     * Inserts bytes into byte array
     * 
     * @param space
     * @param size
     * @param pos
     */
    public void insertBytes(byte[] space, int size, int pos) {
        System.arraycopy(space, 0, data, pos, size);
    }
}
