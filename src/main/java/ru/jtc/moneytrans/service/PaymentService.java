package ru.jtc.moneytrans.service;

import lombok.RequiredArgsConstructor;
import ru.jtc.moneytrans.date.DateProviderImpl;
import ru.jtc.moneytrans.rest.dto.PaymentFilter;
import ru.jtc.moneytrans.rest.dto.PaymentInfo;
import org.springframework.stereotype.Service;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.PaymentRepository;
import ru.jtc.moneytrans.service.specification.PaymentSpecificationCreator;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final DateProviderImpl dateProvider;
    private final PaymentSpecificationCreator specification;

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

    public List<Payment> getAllByAccountId(long accountId, PaymentFilter filter) {
        return paymentRepository.findAll(PaymentSpecificationCreator.incomingPayments(accountId)
                .or(PaymentSpecificationCreator.outgoingPayments(accountId))
                .and(specification.create(filter)));
    }

    public List<Payment> getAll(PaymentFilter filter) {
        return paymentRepository.findAll(specification.create(filter));
    }

    private void createPayment(Account payerAccount, Account receiverAccount, BigDecimal amount, String comment) {
        Payment payment = new Payment();
        payment.setPayerAccount(payerAccount);
        payment.setReceiverAccount(receiverAccount);
        payment.setAmount(amount);
        Date date = dateProvider.currentDate();
        payment.setCreateDate(date);
        payment.setModifyDate(date);
        payment.setComment(comment);
        paymentRepository.save(payment);
    }

}
