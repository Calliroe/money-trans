package ru.jtc.moneytrans.service;

import ru.jtc.moneytrans.rest.dto.FilteringDto;
import ru.jtc.moneytrans.rest.dto.PaymentDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.PaymentRepository;
import ru.jtc.moneytrans.service.speciication.PaymentSpecification;

import javax.transaction.Transactional;
import java.util.*;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void transferMoney(PaymentDto dto) {
        Account payerAccount = accountRepository.findByAccountNumber(dto.getPayerAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(dto.getReceiverAccountNumber());
        double payerBalance = payerAccount.getBalance();
        double receiverBalance = receiverAccount.getBalance();
        double amount = dto.getAmount();
        payerAccount.setBalance(payerBalance - amount);
        receiverAccount.setBalance(receiverBalance + amount);
        accountRepository.saveAll(List.of(payerAccount, receiverAccount));
        accountRepository.save(receiverAccount);
        createPayment(payerAccount, receiverAccount, amount);
    }

    public List<Payment> getAllByAccountId(long accountId, FilteringDto dto) {
        if (Objects.isNull(dto)) {
            return paymentRepository.findAll(PaymentSpecification.incomingPayments(accountId)
                    .or(PaymentSpecification.outgoingPayments(accountId)));
        }
        return paymentRepository.findAll((PaymentSpecification.incomingPayments(accountId)
                .or(PaymentSpecification.outgoingPayments(accountId)))
                .and(PaymentSpecification.makeSpecification(dto)));
    }

    public List<Payment> getAll(FilteringDto dto) {
        if (Objects.isNull(dto)) {
            return paymentRepository.findAll();
        } else {
            return paymentRepository.findAll(PaymentSpecification.makeSpecification(dto));
        }
    }

    private void createPayment(Account payerAccount, Account receiverAccount, Double amount) {
        Payment payment = new Payment();
        payment.setPayerAccount(payerAccount);
        payment.setReceiverAccount(receiverAccount);
        payment.setAmount(amount);
        Date date = new Date();
        payment.setCreateDate(date);
        payment.setModifyDate(date);
        paymentRepository.save(payment);
    }
}
