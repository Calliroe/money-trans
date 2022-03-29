package ru.jtc.moneytrans.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.AccountTypeRepository;
import ru.jtc.moneytrans.rest.dto.AccountInfo;

import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    public Set<Account> findAllByUserId(Long userId) {
        return accountRepository.findAllByUserId(userId);
    }

    public void createAccount(AccountInfo accountInfo, User user) {
        Account account = new Account();
        account.setAccountNumber(accountInfo.getAccountNumber());
        account.setUserId(user.getId());
        account.setAccountType(accountTypeRepository.findByType("Рассчетный счет"));
        Double balance = accountInfo.getBalance();
        if (Objects.isNull(balance)) {
            account.setBalance(0.0);
        } else {
            account.setBalance(balance);
        }
        account.setBic(accountInfo.getBic());
        saveAccount(account);
    }
}
