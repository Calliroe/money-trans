package ru.jtc.moneytrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jtc.moneytrans.model.Account;

import java.util.Set;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByAccountNumber(String accountNumber);

    Set<Account> findAllByUserId(Long userId);

}
