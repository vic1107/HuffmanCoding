package com.shpp.p2p.cs.vicshymko.assignment15;

/**
 * Accept array of String with parameters and
 * extracts source and output file path, direction of converting
 */
public class ArgumentsProcessor implements Constants {
    /* true - isArchived, false - unarchive */
    private boolean isArchived;
    private String inputName;
    private String outputName;

    ArgumentsProcessor(String[] args) {
        inputName = null;
        outputName = null;
        isArchived = args.length != 0 && (args[0].equals(UNARCHIVE_FLAG) || args[0].endsWith(ARCHIVE_EXTENSION));
        setSourceAndOutput(args);
    }

    /**
     * Reads source file path and create output file path (if not given).
     *
     * @param args from command line
     */
    private void setSourceAndOutput(String[] args) {
        if (args.length == 0){
            inputName = Constants.DEFAULT_SOURCE;
            outputName = getArchiveName(inputName);
            System.out.println("Default file path will be processed!");
        }
        else if (args[0].equals(ARCHIVE_FLAG) || args[0].equals(UNARCHIVE_FLAG)) //if flag present
            if (args.length > 2) {
                inputName = args[1];
                outputName = args[2];
            }
            else
                System.out.println("You should specify input file name and name of destination file!");
        else
            if (args.length == 1) {
                inputName = args[0];
                if (!args[0].endsWith(ARCHIVE_EXTENSION)) // find direction
                    outputName = getArchiveName(inputName);
                else
                    outputName = getSourceName(inputName);
            }
            else {
                inputName = args[0];
                outputName = args[1];
        }
    }

    /**
     * Creates source name from archive name.
     *
     * @param archiveName compressed data path
     * @return uncompressed data path
     */
    private String getSourceName(String archiveName) {
        int dotPos = archiveName.lastIndexOf(".");
        /* if file have two extension */
        if (dotPos != -1 && archiveName.indexOf(".") != dotPos) {
            return archiveName.substring(0, dotPos);
        } else {
            return archiveName.substring(0, dotPos) + UNARCHIVE_EXTENSION;
        }
    }

    /**
     * Creates name of archive.
     *
     * @param source path to original file
     * @return path to archived file
     */
    private String getArchiveName(String source) {
        int dotPos = source.lastIndexOf(".");
        /* if file has extension */
        if (dotPos == -1)
            return source + ARCHIVE_EXTENSION;
        else
            return source.substring(0, dotPos) + ARCHIVE_EXTENSION;
    }


    public boolean isArchived() {
        return isArchived;
    }

    public String getInputName() {
        return inputName;
    }

    public String getOutputName() {
        return outputName;
    }

    /**
     * @return true if input and output is known
     */
    public boolean readyToStart() {
        return inputName != null && outputName != null;
    }
}
