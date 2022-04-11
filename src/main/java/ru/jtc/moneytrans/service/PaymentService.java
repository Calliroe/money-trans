package ru.jtc.moneytrans.service;

import ru.jtc.moneytrans.rest.dto.FilteringDto;
import ru.jtc.moneytrans.rest.dto.PaymentInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.PaymentRepository;
import ru.jtc.moneytrans.service.specification.PaymentSpecification;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void transferMoney(PaymentInfo dto) {
        Account payerAccount = accountRepository.findByAccountNumberAndBic(dto.getPayerAccountNumber(), dto.getPayerBic());
        Account receiverAccount = accountRepository.findByAccountNumberAndBic(dto.getReceiverAccountNumber(), dto.getReceiverBic());
        BigDecimal payerBalance = payerAccount.getBalance();
        BigDecimal receiverBalance = receiverAccount.getBalance();
        BigDecimal amount = dto.getAmount();
        payerAccount.setBalance(payerBalance.subtract(amount));
        receiverAccount.setBalance(receiverBalance.add(amount));
        accountRepository.saveAll(List.of(payerAccount, receiverAccount));
        createPayment(payerAccount, receiverAccount, amount, dto.getComment());
    }

    public List<Payment> getAllByAccountId(long accountId, FilteringDto dto) {
        return paymentRepository.findAll((PaymentSpecification.incomingPayments(accountId)
                .or(PaymentSpecification.outgoingPayments(accountId)))
                .and(PaymentSpecification.makeSpecification(dto)));
    }

    public List<Payment> getAll(FilteringDto dto) {
        return paymentRepository.findAll(PaymentSpecification.makeSpecification(dto));
    }

    private void createPayment(Account payerAccount, Account receiverAccount, BigDecimal amount, String comment) {
        Payment payment = new Payment();
        payment.setPayerAccount(payerAccount);
        payment.setReceiverAccount(receiverAccount);
        payment.setAmount(amount);
        Date date = new Date(); // Переделать
        payment.setCreateDate(date);
        payment.setModifyDate(date);
        payment.setComment(comment);
        paymentRepository.save(payment);
    }

}
