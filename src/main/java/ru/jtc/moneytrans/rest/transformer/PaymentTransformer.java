package ru.jtc.moneytrans.rest.transformer;

import org.springframework.stereotype.Component;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.rest.dto.PaymentInfo;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentTransformer {

    public List<PaymentInfo> apply(List<Payment> payments) {
        List<PaymentInfo> paymentInfoList = new ArrayList<>();
        for (Payment payment : payments) {
            PaymentInfo dto = new PaymentInfo();
            dto.setPayerAccountNumber(payment.getPayerAccount().getAccountNumber());
            dto.setReceiverAccountNumber(payment.getReceiverAccount().getAccountNumber());
            dto.setPayerBic(payment.getPayerAccount().getBic());
            dto.setReceiverBic(payment.getReceiverAccount().getBic());
            dto.setAmount(payment.getAmount());
            dto.setComment(payment.getComment());
            paymentInfoList.add(dto);
        }
        return paymentInfoList;
    }
}
