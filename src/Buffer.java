/**
 * @author Xavier Akers
 * @author Zoe Hite
 * 
 * @version Last Updated 10-19-2023
 * 
 * @since 10-16-2023
 * 
 *        Linked Buffers Implementing a Linked Buffer Pool
 * 
 */
public class Buffer {
    private final int bufferSize;
    private byte[] data;
    private int pos;
    private boolean isDirty;

    /**
     * Constructor
     * 
     * @param bufferSize
     *            The size of the buffer
     */
    public Buffer(int bufferSize) {
        this.bufferSize = bufferSize;
        this.data = new byte[bufferSize];
        this.isDirty = false;
        this.pos = -1;
    }


    /**
     * Position setter
     * 
     * @param pos
     *            Absolute Position within the data file
     */
    public void setPos(int pos) {
        this.pos = pos;
    }


    /**
     * Position getting
     * 
     * @return Absolute Position within the data file
     */
    public int getPos() {
        return pos;
    }


    /**
     * @return True if the data has been edited
     */
    public boolean isDirty() {
        return isDirty;
    }


    /**
     * Setter for is dirty
     * 
     * @param isDirty
     *            True if data has been edited
     */
    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }


    /**
     * 
     * @return Reference to the byte array
     */
    public byte[] getData() {
        return data;
    }


    /**
     * @param space
     *            Array with data to set
     */
    public void setData(byte[] space) {
        System.arraycopy(space, 0, this.data, 0, bufferSize);
    }


    /**
     * Gets bytes from byte array
     * 
     * @param space
     *            Array to carry bytes requested
     * @param size
     *            The number of bytes requested
     * @param off
     *            The relative position of bytes requested
     */
    public void getBytes(byte[] space, int size, int off) {
        System.arraycopy(data, off, space, 0, size);
    }

}
