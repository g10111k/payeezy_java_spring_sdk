package com.firstdata.payeezy.samples;

import com.firstdata.payeezy.JSONHelper;
import com.firstdata.payeezy.PayeezyClientApplication;
import com.firstdata.payeezy.PayeezyClientHelper;
import com.firstdata.payeezy.models.transaction.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Example to illustrate an authorization and reversal(void) using a credit card
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PayeezyClientApplication.class)
public class CreditCardSampleTest{

    @Autowired
    PayeezyClientHelper payeezyClientHelper;

    @Test
    public void testCreditCardAuthAndCapture() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("100"); // always set the amouunt in cents
        transactionRequest.setTransactionType(TransactionType.AUTHORIZE.name().toLowerCase());
        transactionRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD.getValue());
        transactionRequest.setReferenceNo(""+System.currentTimeMillis()); // this is your order number
        transactionRequest.setCurrency("USD");
        // set the credit card info
        Card card = new Card().setName("Not Provided").setType("visa").setCvv("123").setExpiryDt("1020").setNumber("4012000033330026");
        transactionRequest.setCard(card);

        try{
            PayeezyResponse payeezyResponse = payeezyClientHelper.doPrimaryTransaction(transactionRequest);
            System.out.println(payeezyResponse.getResponseBody());
            TransactionResponse transactionResponse = new JSONHelper().fromJson(payeezyResponse.getResponseBody(), TransactionResponse.class) ;
            // Capture the credit card authorization
            TransactionRequest captureRequest = new TransactionRequest();
            captureRequest.setTransactionTag(transactionResponse.getTransactionTag());
            captureRequest.setTransactionType(TransactionType.CAPTURE.name().toLowerCase());
            captureRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD.getValue());
            captureRequest.setAmount("100"); // should always match the request amount
            captureRequest.setCurrency("USD");
            PayeezyResponse captureResponse = payeezyClientHelper.doSecondaryTransaction(transactionResponse.getTransactionId(), captureRequest);
            System.out.println(captureResponse.getResponseBody());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
