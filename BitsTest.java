package com.shpp.p2p.cs.vicshymko.assignment15;

public class BitsTest {
    public static void main(String[] args) {
//        int b = 0b0001100110011001100;
//        System.out.println(b);
//        BitsReader br = new BitsReader(new byte[]{0b1111001, -23, 0b111111});
//        for (int i = 0; i < 16; i++) {
//            System.out.println(br.getNextBit());
//        }

//        BitsWriter writer = new BitsWriter(160);
//        for (int i = 8; i <= 12; i++) {
//            writer.write(b, i);
//        }
//        writer.write(b, 7);
//        writer.write(b, 4);
//        writer.write(b, 5);
//        System.out.println();


        String[] tests = new String[]{
                "poem.txt",
                "poem.txt arvhied_poem.par",
                "poem.txt.par",
                "poem.txt.par unarvhived_poem.txt",
                "poem archived",
                "-u archived unarchived",
                "-a archive.par archived_twice.par"
        };

        ArgumentsProcessor argsProc;
        for (String test: tests) {
            argsProc = new ArgumentsProcessor(test.split(" "));
            System.out.print(argsProc.isArchived());
            System.out.print("\t" + argsProc.getInputName());
            System.out.println("\t" + argsProc.getOutputName());
        }
    }
}
