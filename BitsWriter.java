package com.shpp.p2p.cs.vicshymko.assignment15;

/**
 * This class gives ability write simple bit
 * or group of bits to byte array.
 */
public class BitsWriter implements Constants {

    /* bytes array */
    private byte[] bytes;

    /* current byte */
    private int oneByte;

    /* bits left to fill current bit */
    private int freeBits;

    /* position for writing current byte */
    private int pointer;


    /**
     * @param size of resulting byte array
     */
    BitsWriter(int size) {
        bytes = new byte[size];
        freeBits = Constants.BITS_IN_BYTE;
        pointer = 0;
        oneByte = 0b0;
    }

    /* wrapper for writing byte */
    public void write(byte bits, int size) {
        int bitsInt = bits;
        bitsInt = bitsInt & Constants.BYTE_MASK;
        write(bitsInt, size);
    }


    /**
     * @param bits for writing
     * @param size how many bits needed to write
     */
    public void write(int bits, int size){
        /* writing will be continued while "size" bits wasn't write */
        while (size != 0) {
            if (size < freeBits) {
                /* shifting bits left to corresponding place */
                oneByte = oneByte | (bits << (freeBits - size));
                freeBits -= size;
                size = 0;
            } else {
                if (size == freeBits) {
                    /* fill all remained free bits */
                    oneByte = oneByte | bits;
                    size = 0;
                } else {
                    /* fill all remained free bits with first bits from "bits" */
                    oneByte = oneByte | (bits >> (size - freeBits));
                    size -= freeBits;
                }
                writeByte((byte) oneByte);
                freeBits = Constants.BITS_IN_BYTE;
                oneByte = 0b0;
            }

        }
    }

    private void writeByte(byte b){
        bytes[pointer] = b;
        pointer++;
    }


    public byte[] getBytes() {
        if (freeBits != BITS_IN_BYTE)
            writeByte((byte) oneByte); //adding last byte
        return bytes;
    }
}
