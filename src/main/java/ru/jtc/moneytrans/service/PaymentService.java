package ru.jtc.moneytrans.service;

import ru.jtc.moneytrans.rest.dto.PaymentDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.PaymentRepository;
import ru.jtc.moneytrans.rest.exception.PaymentException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void transferMoney(PaymentDto dto) throws PaymentException {
        if (dto.getPayerAccountNumber().equals(dto.getReceiverAccountNumber())) {
            throw new PaymentException("Номер счёта отправителя совпадает с номером счёта получателя");
        }
        Account payerAccount = accountRepository.findByAccountNumber(dto.getPayerAccountNumber());
        if (Objects.isNull(payerAccount)) {
            throw new PaymentException("Аккаунта отправителя с данным номером не существует");
        }
        double payerBalance = payerAccount.getBalance();
        double amount = dto.getAmount();
        if (payerBalance < amount) {
            throw new PaymentException("Недостаточно средств на счёте");
        }
        Account receiverAccount = accountRepository.findByAccountNumber(dto.getReceiverAccountNumber());
        if (Objects.isNull(receiverAccount)) {
            throw new PaymentException("Аккаунта получателя с данным номером не существует");
        }
        double receiverBalance = receiverAccount.getBalance();
        payerAccount.setBalance(payerBalance - amount);
        receiverAccount.setBalance(receiverBalance + amount);
        Payment payment = new Payment();
        payment.setPayerAccount(payerAccount);
        payment.setReceiverAccount(receiverAccount);
        payment.setAmount(dto.getAmount());
        Date date = new Date();
        payment.setCreateDate(date);
        payment.setModifyDate(date);
        accountRepository.save(payerAccount);
        accountRepository.save(receiverAccount);
        paymentRepository.save(payment);
    }

    public List<Payment> getAllByAccountId(long accountId) {
        List<Payment> payments = paymentRepository.findByPayerAccountId(accountId);
        payments.addAll(paymentRepository.findByReceiverAccountId(accountId));
        return payments;
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }
}
