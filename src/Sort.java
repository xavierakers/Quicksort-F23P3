import java.nio.ByteBuffer;

/**
 * @author Xavier Akers
 * @author Zoe Hite
 *
 * @version Last Updated 11-1-2023
 *
 * @since 10-17-2023
 *
 *        A modified QuickSort Algorithm Implementing BufferPool Message-Passing
 *        Communication
 *
 */
public class Sort {
    // make three bytes for space that with length 4
    private byte[] space = new byte[4];
    private byte[] swap1 = new byte[4];
    private byte[] swap2 = new byte[4];

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
        // if there are less than 10 items than use insertion sort
        if (right - left < 40) {
            insort(bufferPool, left, right);
        }
        else {
            // instantiate a variable to be pivot index
            int pivotindex = findPivot(left, right);
            // get the pivot value
            byte[] pivotVal = bufferPool.getPivot(pivotindex);
            // store the key of pivot
            short key = getKey(pivotVal);
            bufferPool.getBytes(swap1, 4, (int)(right / 4) * 4);
            // swap the pivot into the end
            swap(bufferPool, pivotindex, right, pivotVal, swap1);
            // partition
            int newPivot = partition(bufferPool, left, right - 4, key);
            // new pivot
            bufferPool.getBytes(swap1, 4, newPivot);
            // swap out of place
            swap(bufferPool, newPivot, right, swap1, pivotVal);
            // sort left partition
            if ((newPivot - left) > 1) {
                quickSort(bufferPool, left, newPivot - 4);
            }
            // sort right partition
            if ((right - newPivot) > 1) {
                quickSort(bufferPool, newPivot + 4, right);
            }
        }
    }


    /**
     * Finds the pivot of the bufferedStorage Adjusted for 4 bytes records
     *
     * @param left
     *            left most index
     * @param right
     *            right most index
     * @return the Pivot index of the record
     */
    public int findPivot(int left, int right) {
        // find the middle
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
        right = (int)(right / 4) * 4;
        // when left is less than or equal to right
        while (left <= right) {
            bufferPool.getBytes(swap1, 4, left);
            // while the left is less than pivot
            while (getKey(swap1) < pivotVal) {
                left += 4;
                bufferPool.getBytes(swap1, 4, left);
            }
            bufferPool.getBytes(swap2, 4, right);
            // when right is greater than or equal to left
            while ((right >= left) && getKey(swap2) >= pivotVal) {
                right -= 4;
                if (right >= 0) {
                    bufferPool.getBytes(swap2, 4, right);
                }
            }
            // swap the values places
            if (right > left) {
                swap(bufferPool, left, right, swap1, swap2);
            }
        }
        return left;
    }


    /**
     *
     * Sorts by an insertion sort
     *
     * @param bufferPool
     *            The data pool
     * @param left
     *            The left bound index
     * @param right
     *            The right bound index
     * @throws Exception
     */
    public void insort(BufferPool bufferPool, int left, int right)
        throws Exception {
        // iterate through from left to the end
        for (int i = left + 4; i <= right; i += 4) {
            int j = i;
            bufferPool.getBytes(space, 4, j);
            short key = getKey(space);
            while (j > left) {
                bufferPool.getBytes(swap2, 4, j - 4);
                short prevKey = getKey(swap2);
                if (key < prevKey) {
                    // switch places if not ordered
                    swap(bufferPool, j, j - 4, space, swap2);
                    j -= 4;
                }
                else if (key == prevKey) {
                    break;
                }
                else {
                    break;
                }
            }
        }
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
    private void swap(
        BufferPool bufferPool,
        int index1,
        int index2,
        byte[] index1Val,
        byte[] index2Val)
        throws Exception {
        // make copies
        index2 = (int)(index2 / 4) * 4;
        index1 = (int)(index1 / 4) * 4;
        // when indices are in bounds
        if (index1 >= 0 && index1 < bufferPool.getSize() && index2 >= 0
            && index2 < bufferPool.getSize()) {
            // place copy in new spots
            bufferPool.insert(index1Val, 4, index2);
            bufferPool.insert(index2Val, 4, index1);
        }
    }
}
