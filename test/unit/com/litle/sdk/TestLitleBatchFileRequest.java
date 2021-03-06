package com.litle.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.Properties;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.litle.sdk.generate.CardType;
import com.litle.sdk.generate.MethodOfPaymentTypeEnum;
import com.litle.sdk.generate.OrderSourceType;
import com.litle.sdk.generate.Sale;

public class TestLitleBatchFileRequest {

    private LitleBatchFileRequest litleBatchFileRequest;

    @Before
    public void before() throws Exception {
        Properties property = new Properties();
        property.setProperty("username", "PHXMLTEST");
        property.setProperty("password", "password");
        property.setProperty("version", "8.18");
        property.setProperty("maxAllowedTransactionsPerFile", "1000");
        property.setProperty("maxTransactionsPerBatch", "500");
        property.setProperty("batchHost", "localhost");
        property.setProperty("batchPort", "2104");
        property.setProperty("batchTcpTimeout", "10000");
        property.setProperty("batchUseSSL", "false");
        property.setProperty("merchantId", "101");
        property.setProperty("proxyHost", "");
        property.setProperty("proxyPort", "");
        property.setProperty("reportGroup", "test");
        property.setProperty("batchRequestFolder", "test/unit/");
        property.setProperty("batchResponseFolder", "test/unit/");
        litleBatchFileRequest = new LitleBatchFileRequest("testFile.xml", property);
    }

    @Test
    public void testInitializeMembers() throws Exception {
        Properties configToPass = new Properties();

        configToPass.setProperty("username", "usr1");
        configToPass.setProperty("password", "pass");

        litleBatchFileRequest.intializeMembers("testFile.xml", configToPass);

        assertEquals(litleBatchFileRequest.getConfig().getProperty("username"), "usr1");
        assertEquals(litleBatchFileRequest.getConfig().getProperty("password"), "pass");
    }

    @Test
    public void testCreateBatchAndGetNumberOfBatches() throws FileNotFoundException, JAXBException {
        assertEquals(litleBatchFileRequest.getNumberOfBatches(), 0);

        LitleBatchRequest testBatch = litleBatchFileRequest.createBatch("101");
        assertNotNull(testBatch);

        assertEquals(litleBatchFileRequest.getNumberOfBatches(), 1);
    }

    @Test
    public void testGetNumberOfTransactionInFile() throws FileNotFoundException, JAXBException {
        assertEquals(litleBatchFileRequest.getNumberOfTransactionInFile(), 0);

        LitleBatchRequest testBatch = litleBatchFileRequest.createBatch("101");
        Marshaller mockMarshaller = Mockito.mock(Marshaller.class);
        testBatch.setMarshaller(mockMarshaller);
        testBatch.setNumOfTxn(1);
        testBatch.addTransaction(createTestSale(101L,"101"));
        testBatch.addTransaction(createTestSale(102L,"102"));
        testBatch.addTransaction(createTestSale(103L,"103"));

        LitleBatchRequest testBatch2 = litleBatchFileRequest.createBatch("101");
        testBatch2.setMarshaller(mockMarshaller);
        testBatch2.setNumOfTxn(1);


        testBatch2.addTransaction(createTestSale(104L,"104"));
        testBatch2.addTransaction(createTestSale(105L,"105"));
        testBatch2.addTransaction(createTestSale(106L,"106"));

        assertEquals(litleBatchFileRequest.getNumberOfTransactionInFile(), 8);
    }

    @Test
    public void testIsEmpty() throws FileNotFoundException, JAXBException{
        // right off from scratch, batchFileRequest should have no records.
        assertTrue(litleBatchFileRequest.isEmpty());

        LitleBatchRequest testBatch = litleBatchFileRequest.createBatch("101");
        testBatch.setNumOfTxn(1);
        Marshaller mockMarshaller = Mockito.mock(Marshaller.class);
        testBatch.setMarshaller(mockMarshaller);

        testBatch.addTransaction(createTestSale(101L,"101"));

        assertTrue(!litleBatchFileRequest.isEmpty());
    }

    @Test
    public void testIsFull() throws FileNotFoundException, JAXBException{
        // right off from scratch, batchFileRequest should have no records and hence not full.
        Properties property = new Properties();
        property.setProperty("username", "PHXMLTEST");
        property.setProperty("password", "password");
        property.setProperty("version", "8.18");
        property.setProperty("maxAllowedTransactionsPerFile", "4");
        property.setProperty("maxTransactionsPerBatch", "4");
        property.setProperty("batchHost", "localhost");
        property.setProperty("batchPort", "2104");
        property.setProperty("batchTcpTimeout", "10000");
        property.setProperty("batchUseSSL", "false");
        property.setProperty("proxyHost", "");
        property.setProperty("proxyPort", "");
        property.setProperty("merchantId", "101");
        property.setProperty("batchRequestFolder", "test/unit/requestFolder/");
        property.setProperty("batchResponseFolder", "test/unit/responseFolder/");

        litleBatchFileRequest = new LitleBatchFileRequest("testFile.xml", property);

        assertTrue(!litleBatchFileRequest.isFull());

        LitleBatchRequest testBatch = litleBatchFileRequest.createBatch("101");
        testBatch.setNumOfTxn(1);
        Marshaller mockMarshaller = Mockito.mock(Marshaller.class);
        testBatch.setMarshaller(mockMarshaller);


        testBatch.addTransaction(createTestSale(101L,"101"));
        testBatch.addTransaction(createTestSale(102L,"102"));
        testBatch.addTransaction(createTestSale(103L,"103"));

        assertTrue(litleBatchFileRequest.isFull());
    }

    public Sale createTestSale(Long amount, String orderId){
        Sale sale = new Sale();
        sale.setAmount(amount);
        sale.setOrderId(orderId);
        sale.setOrderSource(OrderSourceType.ECOMMERCE);
        CardType card = new CardType();
        card.setType(MethodOfPaymentTypeEnum.VI);
        card.setNumber("4100000000000002");
        card.setExpDate("1210");
        sale.setCard(card);
        sale.setReportGroup("test");
        return sale;
    }

}