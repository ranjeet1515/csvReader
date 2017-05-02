package com.csv_reader;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * It is factory class to read the csv file and
 *
 * @author Ranjeet
 */
public class CSVReaderFactory {
	private final static BlockingQueue<File> csvFiles = new LinkedBlockingQueue<File>();

	private static CSVReaderFactory csvReaderFactory;
	private CSVReadTask contentReadTask;

	private CSVReaderFactory() {

	}

	/**
	 * @return CSVReaderFactory
	 */
	public static synchronized CSVReaderFactory getInstance() {
		if (csvReaderFactory == null) {
			csvReaderFactory = new CSVReaderFactory();
		}
		return csvReaderFactory;
	}

	/**
	 * @param directory
	 *            File
	 * @param readListener
	 *            ReadListener
	 */
	public synchronized void readDirectory(File directory,
			ReadListener readListener) {
		try {
			Thread thread = new Thread(new CSVReadTask(directory, readListener));
			thread.start();
			thread.join();
		} catch (Exception e) {
			if (readListener != null) {
				readListener.onError(directory, Error.EMPTY_DIR_ERR);
			}
		}
	}

	/**
	 * @param readListener
	 *            ReadListener
	 * @return true - Success, false - Already existed
	 */
	public synchronized boolean readFileContents(ReadListener readListener) {
		if (contentReadTask == null) {
			contentReadTask = new CSVReadTask(readListener);
			Thread thread = new Thread(contentReadTask);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException ignored) {
			}
			return true;
		}
		return false;
	}

	/**
     *
     */
	public void cancelProcess() {
		contentReadTask.cancelFlag = true;
		contentReadTask = null;
	}

	/**
	 * このenumはエラメッセージ表示するため使います。
	 *
	 * @author Ranjeet
	 */
	public enum Error {
		EMPTY_DIR_ERR, NO_CONTENT_ERR, READ_FILE_ERR, ABORT_BY_USER
	}

	/**
	 * @author Ranjeet
	 */
	interface ReadListener {

		/**
		 * @param file
		 *            File
		 */
		void onNewFile(File file);

		/**
		 * @param line
		 *            String
		 */
		void onReadLine(String[] line);

		/**
		 * @param file
		 *            File
		 * @param code
		 *            Error
		 */
		void onError(File file, Error code);
	}

	/**
	 * It is thread class and used for scan file from directory. This class use
	 * for read CSV file and display file data.
	 *
	 * @author Ranjeet
	 */
	private class CSVReadTask implements Runnable {

		private final File directory;
		private final ReadListener readListener;
		private boolean cancelFlag = false;

		public CSVReadTask(File directory, ReadListener readListener)
				throws Exception {
			if (directory == null) {
				throw new Exception();
			}
			this.directory = directory;
			this.readListener = readListener;
		}

		public CSVReadTask(ReadListener readListener) {
			this.directory = null;
			this.readListener = readListener;
		}

		@Override
		public void run() {
			if (directory != null) {
				this.readFiles();
			} else if (readListener != null) {
				this.readContents();
			}
		}

		/**
		 * To check weather CSV file exist or not in input directory. After
		 * checking extension of file add in to BlockingQueue
		 * 
		 */
		private void readFiles() {
			if (directory != null && directory.isFile()) {
				if (directory.getName().toLowerCase().endsWith(".csv")) {
					csvFiles.add(directory);
				} else {
					readListener.onError(directory, Error.EMPTY_DIR_ERR);
				}
			} else if (directory != null && directory.isDirectory()) {
				File[] files = directory.listFiles((dir, name) -> name
						.toLowerCase().endsWith(".csv"));
				if (files != null && files.length > 0) {
					Collections.addAll(csvFiles, files);
				} else {
					readListener.onError(directory, Error.EMPTY_DIR_ERR);
				}
			}
		}

		/**
		 * To read CSV File content
		 * 
		 */
		private void readContents() {

			while (!cancelFlag) {
				if (!csvFiles.isEmpty()) {
					File file = csvFiles.remove();
					if (readListener != null) {
						readListener.onNewFile(file);
					}
					try (CSVReader csvReader = new CSVReader(
							new InputStreamReader(new FileInputStream(file),
									"UTF-8"))) {
						String[] line;
						boolean readStatus = false;
						while ((line = csvReader.readNext()) != null) {
							readStatus = true;
							if (readListener != null) {
								readListener.onReadLine(line);
							}
						}
						if (!readStatus) {
							if (readListener != null) {
								readListener
										.onError(file, Error.NO_CONTENT_ERR);
							}
						}
					} catch (IOException e) {
						if (readListener != null) {
							readListener.onError(file, Error.READ_FILE_ERR);
						}
					}
				}
			}
			if (readListener != null) {
				readListener.onError(null, Error.ABORT_BY_USER);
			}
		}
	}

}
