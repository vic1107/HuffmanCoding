package com.shpp.p2p.cs.vicshymko.assignment15;


import acm.util.RandomGenerator;

import java.io.IOException;
import java.io.RandomAccessFile;

public class TestRunner {
    public static final int FILE_BLOCK = 1024 * 1024;

    public static void main(String[] args) {
//        Assignment15.main(new String[]{"big.txt"});
//        Assignment15.main(new String[]{"big.par", "bigD.txt"});
        SimpleBitsWriter bitsWriter = new SimpleBitsWriter(FILE_BLOCK);
        RandomGenerator randomGenerator = new RandomGenerator();
        for (int i = 0; i < FILE_BLOCK; i++) {
            bitsWriter.write((byte) 48);
        }
//        for (int i = 0; i < FILE_BLOCK; i++) {
//            bitsWriter.write((byte) randomGenerator.nextInt(-128, 127));
//        }
        try {
            new RandomAccessFile("block1.txt", "rw").write(bitsWriter.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
