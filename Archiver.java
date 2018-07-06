package com.shpp.p2p.cs.vicshymko.assignment15;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;
import com.shpp.p2p.cs.vicshymko.assignment17.PriorityQueueV2;

/**
 * This class takes paths to source and output files and
 * archive or unarchive source file to output file.
 */
public class Archiver implements Constants {
    /**
     * This list contain bytes arrays for data exchange
     * between different threads.
     */
    private List<byte[]> dataForWriting;
    /**
     * Number of available cores on current computer.
     */
    private int cores;

    /**
     * Initializing of required data.
     */
    Archiver() {
        dataForWriting = Collections.synchronizedList(new ArrayList<>());
        cores = Runtime.getRuntime().availableProcessors();
    }

    /**
     * Handles IO operations with files.
     *
     * @param inputFilePath  path to source file
     * @param outputFileName path to output file
     * @throws IOException raised if file don't exist
     */
    void encode(String inputFilePath, String outputFileName) throws IOException, InterruptedException {
        RandomAccessFile source = new RandomAccessFile(inputFilePath, "r");
        RandomAccessFile output = new RandomAccessFile(outputFileName, "rw");
        long blocks = getBlocksNumber(source.length());
        fillArrayListByNulls(dataForWriting, blocks);

        /* holds number of already read and wrote blocks */
        int readingBlockPos = 0;
        int writingBlockPos = 0;

        while (source.getFilePointer() < source.length() || writingBlockPos != readingBlockPos) {
            /* checking if there is data for writing */
            if (dataForWriting.get(writingBlockPos) != null) {
                output.write(dataForWriting.get(writingBlockPos));
                dataForWriting.set(writingBlockPos, null);
                writingBlockPos++;
            }
            /* checking how many threads are active now */
            if (readingBlockPos - writingBlockPos > cores) {
                Thread.sleep(SLEEP_TIME);
            }
            /* if cpu resources are available -> run reading new portion of data
            and start new thread for its compressing */
            else if (readingBlockPos < blocks) {
                byte[] dataBlock;
                if (source.length() - source.getFilePointer() < Constants.BLOCK_SIZE)
                    dataBlock = new byte[(int) (source.length() - source.getFilePointer())];
                else
                    dataBlock = new byte[Constants.BLOCK_SIZE];
                source.read(dataBlock);
                int finalReadingBlockPos = readingBlockPos;
                new Thread(() -> archive(dataBlock, dataBlock.length < Constants.BLOCK_SIZE,
                        finalReadingBlockPos)).start();
                readingBlockPos++;
            }
        }
        printSizesAndCompression(source.length(), output.length());
        source.close();
        output.close();
    }

    /**
     * Calculating number of blocks for given data size.
     *
     * @param fileLength of original data
     * @return number of blocks
     */
    private long getBlocksNumber(long fileLength) {
        return (fileLength % BLOCK_SIZE != 0) ?
                fileLength / BLOCK_SIZE + 1 :
                fileLength / BLOCK_SIZE;
    }

    /**
     * Fills array list by nulls for escaping NullPointerException.
     *
     * @param lst      data for filling
     * @param elements number of nulls
     */
    private void fillArrayListByNulls(List<byte[]> lst, long elements) {
        for (int i = 0; i < elements; i++) {
            lst.add(null);
        }
    }

    /**
     * Prints sizes of files and level of compression.
     *
     * @param inputSize  source file size in bytes
     * @param outputSize output file size in bytes
     */
    private void printSizesAndCompression(long inputSize, long outputSize) {
        System.out.println("Size of input file is " + bytesToHumanReadable(inputSize));
        System.out.println("Size of output file is " + bytesToHumanReadable(outputSize));
        double ratio = outputSize / (double) inputSize;
        if (ratio < 1) {
            System.out.println("File was compressed by " + round2(100 - (ratio * 100.0)) + "%");
        } else {
            System.out.println("File was uncompressed by " + round2((ratio * 100) - 100) + "%");
        }
    }

    /**
     * Converts bytes to human readable string.
     *
     * @param inputSize in bytes
     * @return nice looking data
     */
    private String bytesToHumanReadable(long inputSize) {
        if (inputSize > Constants.BYTES_IN_GIGABYTE)
            return round2(inputSize / Constants.BYTES_IN_GIGABYTE) + " GB";
        else if (inputSize > Constants.BYTES_IN_MEGABYTE)
            return round2(inputSize / Constants.BYTES_IN_MEGABYTE) + " MB";
        else if (inputSize > Constants.BYTES_IN_KILOBYTE)
            return round2(inputSize / Constants.BYTES_IN_KILOBYTE) + " KB";
        else
            return inputSize + " bytes.";
    }

    /**
     * Rounds number to two decimal after point
     *
     * @param number typical double
     * @return number in form 0.00
     */
    private double round2(double number) {
        return Math.round(number * 100.0) / 100.0;
    }

    /**
     * Does compression of given bytes array.
     *
     * @param dataBlock original bytes array
     * @param isLast    true if this data block is last in file
     */
    private void archive(byte[] dataBlock, boolean isLast, int blockPosition) {
        int[] bytesFrequencies = getFrequencies(dataBlock);
        HuffmanTree tree = new HuffmanTree(bytesFrequencies);
        HashMap<Byte, String> dictionary = tree.getDictionary();
        PriorityQueue<Node> sortedNodes = tree.getSortedNodes();
        dataForWriting.set(blockPosition, encodeData(dataBlock, dictionary, sortedNodes, isLast));
    }

    /**
     * Generate list of bytes sorted by corresponding key size
     *
     * @param sizedDictionary dictionary with
     * @return list of bytes
     */
    private List<Byte> getOrderedBytes(HashMap<Byte, Integer> sizedDictionary) {
        return sizedDictionary.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Writes all required info to bytes array.
     *
     * @param dataBlock   source data
     * @param dictionary  keyByte - valueCodeBits
     * @param frequencies sorted bytes from source data
     * @param isLast      true if block is last
     * @return coded bytes array
     */
    private byte[] encodeData(byte[] dataBlock, HashMap<Byte, String> dictionary,
                              PriorityQueue<Node> frequencies, boolean isLast) {
        HashMap<Byte, Integer> sizesDict = getSizesDictionary(dictionary);
        HashMap<Byte, Integer> keysDict = getValuesDictionary(dictionary);
        List<Byte> orderedBytes = getOrderedBytes(sizesDict);
        long size = countBitsSize(sizesDict, frequencies, orderedBytes);
        BitsWriter encodedData = new BitsWriter(bitsToBytes(size));

        /* writing headers */
        encodedData.write((isLast) ? 0b1 : 0b0, 1); //write 1 if last block
        encodedData.write((int) size, Constants.BITS_LENGTH_SIZE); // block size
        encodedData.write(dataBlock.length, Constants.BYTES_BLOCK_SIZE);
        encodedData.write(dictionary.size(), Constants.DICTIONARY_BITS_SIZE); // dict size

        /* writing dictionary */
        int keySize = 1;
        for (Byte value : orderedBytes) {
            int shift = sizesDict.get(value) - keySize;
            keySize += shift;
            encodedData.write(Constants.SHIFTS[shift], shift + 1); //shift for key-value pair
            encodedData.write(keysDict.get(value), sizesDict.get(value)); // key
            encodedData.write(value, BITS_IN_BYTE); //value
        }
        /* writing encoded data */
        for (Byte b : dataBlock) {
            encodedData.write(keysDict.get(b), sizesDict.get(b));
        }

        return encodedData.getBytes();
    }

    /**
     * @param dictionary byte from original data - bits for coding as string
     * @return same byte - bits parsed to integer
     */
    private HashMap<Byte, Integer> getValuesDictionary(HashMap<Byte, String> dictionary) {
        HashMap<Byte, Integer> sizesDict = new HashMap<>();
        for (Map.Entry<Byte, String> entry : dictionary.entrySet()) {
            sizesDict.put(entry.getKey(), Integer.parseInt(entry.getValue(), 2));
        }
        return sizesDict;
    }

    /**
     * Converts dictionary key: byte from original data, value: string with bits
     * to same key's but value: integer length of value in given dictionary
     *
     * @param dictionary byte - bits in string
     * @return byte - length of bits
     */
    private HashMap<Byte, Integer> getSizesDictionary(HashMap<Byte, String> dictionary) {
        HashMap<Byte, Integer> sizesDict = new HashMap<>();
        for (Map.Entry<Byte, String> entry : dictionary.entrySet()) {
            sizesDict.put(entry.getKey(), entry.getValue().length());
        }
        return sizesDict;
    }

    /**
     * Calculates size of coded data.
     *
     * @param dictionary  contain pairs byteValue - coded bits size
     * @param frequencies sorted list of frequencies
     * @return size of coded data
     */
    private int countBitsSize(HashMap<Byte, Integer> dictionary,
                              PriorityQueue<Node> frequencies, List<Byte> orderedBytes) {
        int sizeInBits = 1 + Constants.BITS_LENGTH_SIZE + Constants.BYTES_BLOCK_SIZE + Constants.DICTIONARY_BITS_SIZE;

        /* create frequencies map */
        HashMap<Byte, Integer> frequenciesMap = new HashMap<>();
        for (Node n : frequencies) {
            frequenciesMap.put(n.getByteValue(), n.getByteFrequency());
        }

        /* calculating bits for every byte by multiplying it's frequency by corresponding code bits length */
        int dictKeySize = 1;
        for (Byte b : orderedBytes) {
            int keySize = dictionary.get(b);
            int shift = keySize - dictKeySize + 1;
            dictKeySize = keySize;
            int sizeInDictionary = shift + keySize + BITS_IN_BYTE;// 8 -> value size
            long sizeInData = keySize * frequenciesMap.get(b);
            sizeInBits += sizeInDictionary + sizeInData;
        }
        return sizeInBits;
    }

    /**
     * Counts the number of occurrence of every byte in given array.
     *
     * @param dataBlock original data
     * @return list of frequencies of every byte
     */
    private int[] getFrequencies(byte[] dataBlock) {
        int[] frequencies = new int[256];
        for (byte b : dataBlock) {
            frequencies[b + BYTE_INT_DIFFERENCE]++;
        }
        return frequencies;
    }

    /**
     * Calculate how many bytes is required to hold given number of bits.
     *
     * @param bits number of
     * @return number of bytes
     */
    private int bitsToBytes(long bits) {
        if (bits % BITS_IN_BYTE == 0)
            return (int) bits / BITS_IN_BYTE;
        else
            return (int) bits / BITS_IN_BYTE + 1; //extra byte
    }

    /**
     * Decode the data from file with given file name to other file.
     *
     * @param inputFilePath  source file path
     * @param outputFileName target file path
     * @throws IOException problem with opening and reading/writing file
     */
    void decode(String inputFilePath, String outputFileName) throws IOException, InterruptedException {
        RandomAccessFile source = new RandomAccessFile(inputFilePath, "r");
        RandomAccessFile output = new RandomAccessFile(outputFileName, "rw");

        int readingBlockPos = 0;
        int writingBlockPos = 0;
        boolean isLastBlock = false;
        do {
            /* writing output if available */
            if (!dataForWriting.isEmpty() && dataForWriting.get(writingBlockPos) != null) {
                output.write(dataForWriting.get(writingBlockPos));
                dataForWriting.set(writingBlockPos, null);
                writingBlockPos++;
            }/* waiting for cpu resources */
            if (readingBlockPos - writingBlockPos > cores) {
                Thread.sleep(SLEEP_TIME);
            }
            /* reading new block for unarchiving */
            else if (!isLastBlock) {
                dataForWriting.add(null);
                byte[] starting5bytes = new byte[5];
                source.read(starting5bytes); // reading first 5 bytes to get whole data block size

                /* getting info about this and next block */
                BitsReader br = new BitsReader(starting5bytes);
                isLastBlock = br.getNextBit() == 1;
                int bytesInBlock = bitsToBytes(br.getBits(Constants.BITS_LENGTH_SIZE));

                /* preparing array for block data */
                byte[] compressedBlock = new byte[bytesInBlock];
                System.arraycopy(starting5bytes, 0, compressedBlock, 0, starting5bytes.length);

                /* reading all block and unpacking it */
                source.read(compressedBlock, starting5bytes.length, bytesInBlock - starting5bytes.length);
                int finalReadingBlockPos = readingBlockPos;
                new Thread(() -> unarchive(compressedBlock, finalReadingBlockPos)).start();

                /* writing unpacking data to target file */
                readingBlockPos++;
            }
        } while (readingBlockPos != writingBlockPos);

        printSizesAndCompression(source.length(), output.length());
        source.close();
        output.close();
    }

    /**
     * Reads dictionary and unpack data.
     * !Method needs to be optimized (poor performance).
     *
     * @param encodedBlock data array
     */
    private void unarchive(byte[] encodedBlock, int blockPos) {
        BitsReader data = new BitsReader(encodedBlock);
        data.getBits(1);
        int bitsNumber = data.getBits(Constants.BITS_LENGTH_SIZE);
        int blockSize = data.getBits(Constants.BYTES_BLOCK_SIZE);
        int dictSize = data.getBits(Constants.DICTIONARY_BITS_SIZE);
        HashMap<Integer, Byte> dictionary = new HashMap<>();
        SimpleBitsWriter output = new SimpleBitsWriter(blockSize);
        ArrayList<Integer> sizesLst = new ArrayList<>();

        /* reading dictionary */
        int keySize = 1;
        for (int i = 0; i < dictSize; i++) {
            int shift = data.getNextBit();

            for (int s = 0; s < Constants.SHIFTS.length; s++) {
                if (shift == Constants.SHIFTS[s]) {
                    keySize += s;
                    break;
                }
                shift = (shift << 1) | data.getNextBit();
            }

            sizesLst.add(keySize);
            int sizeByteKey = (keySize << SIZE_KEY_SHIFT) | data.getBits(keySize);
            dictionary.put(sizeByteKey, (byte) data.getBits(8));
        }

        /* preparing helper data */
        int[] sizesParts = getSizesParts(sizesLst);
        int[] sizesShifts = prepareSizes(sizesLst); // contain array of number of bits required for getting next key

        /* unpacking data */
        int currKey = 0b0;
        while (data.getBitsPosition() < bitsNumber) {
            int currSize = 0;

            for (int size : sizesShifts) {
                currSize += size;
                currKey = (currKey << size) | data.getBits(size);//getting required number of bits
                Byte posByte;

                /* checking if key present in dictionary */
                if ((posByte = dictionary.get(sizesParts[currSize] | currKey)) != null) {
                    output.write(posByte);
                    currKey = 0b0;
                    break;
                }
            }
        }
        dataForWriting.set(blockPos, output.getData());
    }

    /**
     * Performance increasing calculations.
     * Generate sizes mask for "|" them to value masks.
     *
     * @param sizes of keys in compressed data in bits
     * @return ready for using "size masks"
     */
    private int[] getSizesParts(ArrayList<Integer> sizes) {
        int[] sizesParts = new int[Collections.max(sizes) + 1];
        for (int size : sizes) {
            if (sizesParts[size] == 0) {
                sizesParts[size] = size << SIZE_KEY_SHIFT;
            }
        }
        return sizesParts;
    }

    /**
     * Generate table of shifts for quicker data processing
     * and reducing number of accessing to dictionary.
     *
     * @param sizesLst list of key sizes in bits
     * @return array of shifts
     */
    private int[] prepareSizes(ArrayList<Integer> sizesLst) {
        ArrayList<Integer> sizes = new ArrayList<>();
        sizes.add(sizesLst.get(0));
        /* generating table of shifts*/
        for (int i = 1; i < sizesLst.size(); i++) {
            int difference = sizesLst.get(i) - sizesLst.get(i - 1);
            if (difference != 0) {
                sizes.add(difference);
            }
        }
        /* converting it to array */
        int[] sizesArr = new int[sizes.size()];
        for (int i = 0; i < sizes.size(); i++) {
            sizesArr[i] = sizes.get(i);
        }
        return sizesArr;
    }
}
