package ru.jtc.moneytrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jtc.moneytrans.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPayerAccountId(long payerAccountId);
    List<Payment> findByReceiverAccountId(long receiverAccountId);
}
