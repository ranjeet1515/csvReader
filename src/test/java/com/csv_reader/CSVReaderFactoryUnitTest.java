package com.csv_reader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.csv_reader.CSVReaderFactory;

import java.io.File;

/**
 * test case code for CSVReaderFactory class
 */
public class CSVReaderFactoryUnitTest {

    private int taskStatus = -1;
    private CSVReaderFactory.Error error = null;
    private CSVReaderFactory.ReadListener readListener;

    @Before
    public void setUp() throws Exception {
        readListener = new CSVReaderFactory.ReadListener() {
            @Override
            public void onNewFile(File file) {
                taskStatus = 1;
            }

            @Override
            public void onReadLine(String[] line) {
                taskStatus = 2;
            }

            @Override
            public void onError(File file, CSVReaderFactory.Error code) {
                taskStatus = 0;
                error = code;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        readListener = null;
        taskStatus = -1;
        error = null;
    }

    /**
     * this  testCase is use for check instance　of test class.
     */
    @Test
    public void test_getInstance_not_null() {
        Assert.assertTrue(CSVReaderFactory.getInstance() != null);
    }

    /**
     * this test case is use for to test weather two object is possible or not ,
     * as it is Singleton class
     */
    @Test
    public void test_getInstance_singleton() {

        CSVReaderFactory csvReaderFactory1 = CSVReaderFactory.getInstance();
        CSVReaderFactory csvReaderFactory2 = CSVReaderFactory.getInstance();
        Assert.assertSame(csvReaderFactory1, csvReaderFactory2);
    }

    /**
     * test case for if directory is  returning null
     */
    @Test
    public void test_readDirectory_null() {
        CSVReaderFactory.getInstance().readDirectory(null, readListener);
        while (taskStatus == -1) ;
        Assert.assertTrue(taskStatus == 0);
        Assert.assertSame(CSVReaderFactory.Error.EMPTY_DIR_ERR, error);
    }

    /**
     * Test case for if directory is empty (ex- there is no csv file in directory)
     */
    @Test
    public void test_readDirectory_empty() {
        File resourcesDirectory = new File("src/test/resources/empty");
        CSVReaderFactory.getInstance().readDirectory(resourcesDirectory, readListener);
        while (taskStatus == -1);
        Assert.assertTrue(taskStatus == 0);
        Assert.assertSame(CSVReaderFactory.Error.EMPTY_DIR_ERR, error);
    }



}
