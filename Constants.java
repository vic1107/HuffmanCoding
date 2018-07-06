package com.shpp.p2p.cs.vicshymko.assignment15;

public interface Constants {

    /**
     * Default path if not given in command line.
     */
    String DEFAULT_SOURCE = "src/test.txt";
    /**
     * Size of number of bits in block in bits
     */
    int BITS_LENGTH_SIZE = 32;
    /**
     * Size of number of original data block in bytes
     */
    int BYTES_BLOCK_SIZE = 30;
    /**
     * Size of number of "words" in dictionary
     */
    int DICTIONARY_BITS_SIZE = 9;
    /**
     * Used for writing/reading dictionary with key with different size in bite
     */
    int[] SHIFTS = {0b0, 0b10, 0b110, 0b1110, 0b11110, 0b111110,
            0b1111110, 0b11111110, 0b111111110};
    /**
     * Large files for encoding are split to blocks with this size
     * Value was found by experiments.
     */
    int BLOCK_SIZE = 1024 * 1024 * 8;
    /**
     * Number of bytes in GB.
     */
    double BYTES_IN_GIGABYTE = 1073741824.0;
    /**
     * Number of bytes in MB.
     */
    double BYTES_IN_MEGABYTE = 1048576.0;
    /**
     * Number of bytes in KB.
     */
    double BYTES_IN_KILOBYTE = 1024.0;
    /**
     * Hello, Captain.
     */
    int BITS_IN_BYTE = 8;
    /**
     * The mask for extracting 8 bits from integer
     */
    int BYTE_MASK = 0b00000000000000000000000011111111;
    /**
     * The int value of key size will be shifted left by this number.
     */
    int SIZE_KEY_SHIFT = 26;
    /**
     * Unarchive flag shortcut used in command line
     */
    String UNARCHIVE_FLAG = "-u";
    /**
     * Archive flag shortcut used in command line
     */
    String ARCHIVE_FLAG = "-a";
    /**
     * Default archive extension.
     */
    String ARCHIVE_EXTENSION = ".par";
    /**
     * Default unarchive extension for files without original extension
     */
    String UNARCHIVE_EXTENSION = ".uar";
    /**
     * The masks for filtering given number of bits from byte
     * Used in following manure: number & MASKS[number of bits that should be left]
     */
    int[] MASKS = {0, 1, 3, 7, 15, 31, 63, 127, 255};
    /**
     * Sleep time for thread reading and writing files when any operations doesn't required
     */
    int SLEEP_TIME = 100;
    /**
     * Milliseconds in one second.
     */
    double MILLISECONDS = 1000.0;
    /**
     * The difference between 0 and Byte.MIN_VALUE
     */
    int BYTE_INT_DIFFERENCE = 128;
}
