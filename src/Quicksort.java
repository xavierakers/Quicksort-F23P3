
/**
 * {Project Description Here}
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The class containing the main method.
 *
 * @author Xavier Akers
 * @version Last Updated 2023-10-13
 */

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

public class Quicksort {

    /**
     * This method is used to generate a file of a certain size, containing a
     * specified number of records.
     *
     * @param filename
     *            the name of the file to create/write to
     * @param blockSize
     *            the size of the file to generate
     * @param format
     *            the format of file to create
     * @throws IOException
     *             throw if the file is not open and proper
     */
    public static void generateFile(
        String filename,
        String blockSize,
        char format)
        throws IOException {
        FileGenerator generator = new FileGenerator();
        String[] inputs = new String[3];
        inputs[0] = "-" + format;
        inputs[1] = filename;
        inputs[2] = blockSize;
        generator.generateFile(inputs);
    }


    /**
     * @param args
     *            Command line parameters.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int[] cacheHits = new int[] { 0 };
        int[] diskReads = new int[] { 0 };
        int[] diskWrites = new int[] { 0 };
        long startTime = System.currentTimeMillis();

        int numBuffers = Integer.parseInt(args[1]);
        BufferPool buffPool = new BufferPool(numBuffers, 4096, args[0],
            cacheHits, diskReads, diskWrites);

        Sort sort = new Sort();
        sort.quickSort(buffPool, 0, (buffPool.getSize()) - 1);
        buffPool.flushAll();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        reportMetrics(cacheHits, diskReads, diskWrites, executionTime, args[2]);
    }


    /**
     * Prints performance metrics
     * 
     * @param cacheHits
     *            number of cache hits
     * @param diskReads
     *            number of disk reads
     * @param diskWrites
     *            number of disk writes
     * @param executionTime
     *            total time elapsed
     * @param filePath
     *            file path to print metrics
     */
    public static void reportMetrics(
        int[] cacheHits,
        int[] diskReads,
        int[] diskWrites,
        long executionTime,
        String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Cache Hits: " + cacheHits[0]);
            writer.println("Disk Reads: " + diskReads[0]);
            writer.println("Disk Writes: " + diskWrites[0]);
            writer.println("Execution Time: " + executionTime
                + " milliseconds");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
