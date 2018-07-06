package com.shpp.p2p.cs.vicshymko.assignment15;

/**
 * Main reason for creating this class is avoiding
 * manual indexing handling while writing into bytes array.
 */
public class SimpleBitsWriter implements Constants {
    private byte[] data;
    private int ind;

    SimpleBitsWriter(int capacity) {
        data = new byte[capacity];
        ind = 0;
    }

    public void write(byte b) {
        data[ind] = b;
        ind++;
    }

    public byte[] getData() {
        return data;
    }
}
