package ru.jtc.moneytrans.service;

import ru.jtc.moneytrans.rest.dto.TransferMoneyDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.PaymentRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void transferMoney(TransferMoneyDto dto) throws Exception { //Прописать свои эксепшены
        if (dto.getPayerAccountNumber() == dto.getReceiverAccountNumber()) {
            throw new Exception("Номер счёта отправителя совпадает с номером счёта получателя");
        }
        Account payerAccount = accountRepository.findByAccountNumber(dto.getPayerAccountNumber());
        double payerBalance = payerAccount.getBalance();
        double amount = dto.getAmount();
        if (payerBalance < amount) {
            throw new Exception("Недостаточно средств на счёте");
        }
        Account receiverAccount = accountRepository.findByAccountNumber(dto.getReceiverAccountNumber());
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
