package ru.jtc.moneytrans.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jtc.moneytrans.model.AccountType;
import ru.jtc.moneytrans.repository.AccountTypeRepository;

@Service
@AllArgsConstructor
public class AccountTypeService {
    private final AccountTypeRepository accountTypeRepository;

    public AccountType findByType(String accountType) {
        return accountTypeRepository.findByType(accountType);
    }
}
