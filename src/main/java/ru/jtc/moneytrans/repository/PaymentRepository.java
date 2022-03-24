package ru.jtc.moneytrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jtc.moneytrans.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPayerAccountId(long payerAccountId);
    List<Payment> findByReceiverAccountId(long receiverAccountId);
}
