package ru.jtc.moneytrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jtc.moneytrans.model.AccountType;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
    AccountType findByType(String type);
}
