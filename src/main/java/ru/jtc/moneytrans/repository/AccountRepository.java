package ru.jtc.moneytrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jtc.moneytrans.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(long accountNumber);
}
