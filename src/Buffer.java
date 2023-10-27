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
    private int blockID;
    private boolean isDirty;

    /**
     * Default constructor
     */
    public Buffer(int bufferSize) {
        this.BUFFER_SIZE = bufferSize;
        this.data = new byte[BUFFER_SIZE];
        this.isDirty = false;
        this.blockID = -1;
    }


    public void setPos(int pos) {
        this.pos = pos;
    }


    public int getPos() {
        return pos;
    }


    public int getBlockID() {
        return blockID;
    }


    public void setBlockID(int id) {
        this.blockID = id;
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

}
