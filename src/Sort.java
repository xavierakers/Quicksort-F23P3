import java.nio.ByteBuffer;

/**
 * @author Xavier Akers
 * 
 * @version Last Updated
 * 
 * @since 2023-10-17
 * 
 *        A modified QuickSort Algorithm
 *        Implementing BufferPool Message-Passing Communication
 * 
 */
public class Sort {
    private byte[] space = new byte[4];

    /**
     * Recursive QuickSort Method
     * 
     * @param bufferPool
     *            access to the byte data
     * @param left
     *            start of the array
     * @param right
     *            number of bytes stored in the buffer
     * @throws Exception
     */
    public void quickSort(BufferPool bufferPool, int left, int right)
        throws Exception {
        
        int pivotindex = findPivot(left, right);
        byte[] pivotVal = bufferPool.getPivot(pivotindex);

        short key = getKey(pivotVal);
        swap(bufferPool, pivotindex, right);

        int newPivot = partition(bufferPool, left, right - 4, key);
        swap(bufferPool, newPivot, right);

        if ((newPivot - left) > 1) {
            quickSort(bufferPool, left, newPivot - 4);
        }
        if ((right - newPivot) > 1) {
            quickSort(bufferPool, newPivot + 4, right);
        }
    }


    /**
     * Finds the pivot of the bufferedStorage
     * Adjusted for 4 bytes records
     * 
     * @param left
     *            left most index
     * @param right
     *            right most index
     * @return the Pivot index of the record
     */
    public int findPivot(int left, int right) {
        return (((int)(left / 4) + (int)(right / 4)) / 2) * 4;
    }


    /**
     * Sorts the subarray
     * 
     * @param bufferPool
     *            Provides access to the data
     * @param left
     *            left most index
     * @param right
     *            right most index
     * @param pivotVal
     *            value of the pivot
     * @return the new left index
     * @throws Exception 
     */
    public int partition(
        BufferPool bufferPool,
        int left,
        int right,
        short pivotVal)
        throws Exception {
        // Adjusting for 4 bytes records
        right = (int)(right / 4) * 4;

        while (left <= right) {

            bufferPool.getBytes(space, 4, left);
            while (getKey(space) < pivotVal) {
                left += 4;
                bufferPool.getBytes(space, 4, left);
            }

            bufferPool.getBytes(space, 4, right);
            while ((right >= left) && getKey(space) >= pivotVal) {
                right -= 4;
                if (right >= 0) {
                    bufferPool.getBytes(space, 4, right);
                }
            }

            if (right > left) {
                swap(bufferPool, left, right);
            }
        }

        return left;
    }


    /**
     * Extracts key from 4 byte record
     * 
     * @param record
     *            byte array containing the record
     * @return short key value
     */
    private short getKey(byte[] record) {
        ByteBuffer buffer = ByteBuffer.wrap(record);
        return buffer.getShort();

    }


    /**
     * Moves records around in a bufferPool
     * 
     * @param bufferPool
     *            the bufferPool
     * @param index1
     *            index to be swapped, pivot
     * @param index2
     *            index to be swapped, right
     * @throws Exception 
     */
    private void swap(BufferPool bufferPool, int index1, int index2)
        throws Exception {
        index2 = (int)(index2 / 4) * 4;
        index1 = (int)(index1 / 4) * 4;

        if (index1 >= 0 && index1 < bufferPool.getSize() && index2 >= 0
            && index2 < bufferPool.getSize()) {
            byte[] temp = new byte[4];
            bufferPool.getBytes(temp, 4, index1);
            bufferPool.getBytes(space, 4, index2);
            bufferPool.insert(space, 4, index1);
            bufferPool.insert(temp, 4, index2);
        }
    }

}
