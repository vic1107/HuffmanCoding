package com.shpp.p2p.cs.vicshymko.assignment15;


import java.io.IOException;

/**
 * Created by Shymko Victor.
 *
 * This program is simple command line archiver.
 * Works only with one file at one time.
 */
public class Assignment15 implements Constants {

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            ArgumentsProcessor argsChecker = new ArgumentsProcessor(args);
            Archiver archiver = new Archiver();

            if (argsChecker.readyToStart()) {
                if (argsChecker.isArchived())
                    archiver.decode(argsChecker.getInputName(), argsChecker.getOutputName());
                else
                    archiver.encode(argsChecker.getInputName(), argsChecker.getOutputName());

                System.out.println("File was " + ((argsChecker.isArchived()) ? "unarchived": "archived")
                        + " to " + argsChecker.getOutputName());
                double seconds = (System.currentTimeMillis() - start) / MILLISECONDS;
                System.out.println("File processing took " + seconds + " seconds");

            } else
                System.out.println("Try again please!");

        } catch (IOException e) {
            System.out.println("Source file was not found or is corrupted!");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("Source file was harmed!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
