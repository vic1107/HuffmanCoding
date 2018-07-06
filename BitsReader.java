package com.shpp.p2p.cs.vicshymko.assignment15;

/**
 * Allows to read single bit or several bits from bytes array.
 * !Class need to be optimized in performance.
 */
public class BitsReader implements Constants {

    private byte[] bytes;
    private int ind;
    private int bitsLeft;

    /**
     * Accepts byte[] for reading
     *
     * @param bytes data for reading
     */
    BitsReader(byte[] bytes) {
        this.bytes = bytes;
        ind = 0;
        bitsLeft = BITS_IN_BYTE;
    }

    /**
     * Every call moves bit's pointer by 1.
     *
     * @return single bit
     */
    public int getNextBit() {
        int bit = bytes[ind] >> (bitsLeft - 1);
        bitsLeft--;
        if (bitsLeft == 0) {
            bitsLeft = BITS_IN_BYTE;
            ind++;
        }
        return bit & MASKS[1];
    }


    /**
     * returns next portion of bits with given size
     *
     * @param bitsNumber how many bits required to read from byte array
     * @return bits
     */
    public int getBits(int bitsNumber) {
        if (bitsNumber == 1) // if only one bit required
            return getNextBit();

        int result = 0b0;
        while (bitsNumber != 0) {
            if (bitsNumber < bitsLeft) {// if all bits are at the beginning or at the middle of current bit
                result = result | ((bytes[ind] >> (bitsLeft - bitsNumber)) & MASKS[bitsNumber]);
                bitsLeft -= bitsNumber;
                bitsNumber = 0;
            }
            else if (bitsNumber == bitsLeft) { // if bits expands right to the last bit of current bit
                result = result | (bytes[ind] & MASKS[bitsNumber]);
                bitsLeft = BITS_IN_BYTE;
                bitsNumber = 0;
                ind++;
            }
            else { // if required bits are present in current and following bits
                int temp = bytes[ind] & MASKS[bitsLeft];
                bitsNumber -= bitsLeft;
                result = (result | temp) << ((bitsNumber > BITS_IN_BYTE)? BITS_IN_BYTE: bitsNumber);
                bitsLeft = BITS_IN_BYTE;
                ind++;
            }
        }
        return result;
    }

    /**
     * @return current pointer position
     */
    public int getBitsPosition() {
        return ind * BITS_IN_BYTE + (BITS_IN_BYTE - bitsLeft);
    }
}
