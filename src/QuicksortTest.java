import student.TestCase;

/**
 * @author {Your Name Here}
 * @version {Put Something Here}
 */
public class QuicksortTest extends TestCase {
    private CheckFile fileChecker;

    /**
     * Sets up the tests that follow. In general, used for initialization.
     */
    public void setUp() {
        fileChecker = new CheckFile();
    }


    /**
     * This method is a demonstration of the file generator and file checker
     * functionality. It calles generateFile to create a small "ascii" file.
     * It then calls the file checker to see if it is sorted (presumably not
     * since we don't call a sort method in this test, so we assertFalse).
     *
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testFileGenerator() throws Exception {
        String[] args = new String[3];
        args[0] = "input.txt";
        args[1] = "1";
        args[2] = "statFile.txt";
        Quicksort.generateFile("input.txt", "1", 'a');
        // In a real test we would call the sort
        // Quicksort.main(args);
        // In a real test, the following would be assertTrue()
        assertFalse(fileChecker.checkFile("input.txt"));
    }


    /**
     * Get code coverage of the class declaration.
     * 
     * @throws Exception
     */
    public void testQInit() throws Exception {
        Quicksort tree = new Quicksort();
        assertNotNull(tree);
        Quicksort.main(null);
    }


    /**
     * Ascii values
     * 1 Block
     * 1 Buffers
     * 
     * @throws Exception
     */
    public void test2Ascii() throws Exception {
        String[] args = new String[3];
        args[0] = "input.txt";
        args[1] = "1";
        args[2] = "statFile.txt";
        Quicksort.generateFile("input.txt", "1", 'a');
        Quicksort.main(args);
        assertTrue(fileChecker.checkFile("input.txt"));

    }


    /**
     * Binary Values
     * 1 Block
     * 1 Buffers
     * 
     * @throws Exception
     */
    public void test2Binary() throws Exception {
        String[] args = new String[3];
        args[0] = "input.txt";
        args[1] = "1";
        args[2] = "statFile.txt";
        Quicksort.generateFile("input.txt", "1", 'b');
        Quicksort.main(args);
        assertTrue(fileChecker.checkFile("input.txt"));
    }


    /**
     * Ascii
     * 10 Blocks
     * 10 Buffers
     * 
     * @throws Exception
     */
    public void test3Ascii() throws Exception {
        String[] args = new String[3];
        args[0] = "input.txt";
        args[1] = "10";
        args[2] = "statFile.txt";
        Quicksort.generateFile("input.txt", "10", 'a');
        Quicksort.main(args);
        assertTrue(fileChecker.checkFile("input.txt"));

    }


    /**
     * Binary
     * 10 Blocks
     * 10 Buffers
     * 
     * @throws Exception
     */
    public void test3Binary() throws Exception {
        String[] args = new String[3];
        args[0] = "input.txt";
        args[1] = "10";
        args[2] = "statFile.txt";
        Quicksort.generateFile("input.txt", "10", 'b');
        Quicksort.main(args);
        assertTrue(fileChecker.checkFile("input.txt"));

    }


    /**
     * Ascii
     * 10 Blocks
     * 4 Buffers
     * 
     * @throws Exception
     */
    public void test5Ascii() throws Exception {
        String[] args = new String[3];
        args[0] = "input.txt";
        args[1] = "4";
        args[2] = "statFile.txt";
        Quicksort.generateFile("input.txt", "10", 'a');
        Quicksort.main(args);
        assertTrue(fileChecker.checkFile("input.txt"));
    }


    /**
     * Binary
     * 10 Blocks
     * 4 Buffers
     * 
     * @throws Exception
     */
    public void test5Binary() throws Exception {
        String[] args = new String[3];
        args[0] = "input.txt";
        args[1] = "4";
        args[2] = "statFile.txt";
        Quicksort.generateFile("input.txt", "10", 'b');
        Quicksort.main(args);
        assertTrue(fileChecker.checkFile("input.txt"));
    }


    public void testRealBufferPool1() throws Exception {
        String[] args = new String[3];
        args[0] = "input.txt";
        args[1] = "3";
        args[2] = "statFile.txt";
        Quicksort.generateFile("input.txt", "2", 'a');
        Quicksort.main(args);
        assertTrue(fileChecker.checkFile("input.txt"));
    }
}
