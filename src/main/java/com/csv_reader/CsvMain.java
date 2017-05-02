package com.csv_reader;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *It is the start point of program execution.
 * @author Ranjeet
 */
public class CsvMain {

    private final static Logger LOGGER = Logger
            .getLogger(CSVReaderFactory.class.getName());

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Please Pass Directory  in parameter  path ...!!!");
            return;
        }


        CSVReaderFactory.ReadListener readListener = new CSVReaderFactory.ReadListener() {

            @Override
            public void onNewFile(File file) {
                System.out.println(file);
                System.out
                        .println("----------------------------------------------");
            }

            @Override
            public void onReadLine(String[] line) {
                System.out.println();
                for (String item : line) {
                    System.out.print(item);
                }
                System.out.println();
            }

            @Override
            public void onError(File file, CSVReaderFactory.Error error) {
                switch (error) {
                    case EMPTY_DIR_ERR:

                        LOGGER.log(Level.WARNING, file.toString()
                                + " No csv files found in given directory");
                        break;
                    case NO_CONTENT_ERR:
                        LOGGER.log(Level.WARNING, file.toString()
                                + " There is no content in file");
                        break;
                    case READ_FILE_ERR:
                        LOGGER.log(Level.WARNING, file.toString()
                                + " unable to read  file content");
                        break;

                    default:
                        break;
                }

            }
        };

        CSVReaderFactory.getInstance().readDirectory(new File(args[0]), readListener);

        CSVReaderFactory.getInstance().readFileContents(readListener);
    }
}
