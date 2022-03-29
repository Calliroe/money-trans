package ru.jtc.moneytrans.service;

import liquibase.pro.packaged.A;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.rest.dto.AccountInfo;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountTypeService accountTypeService;

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    public Account findAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public List<Account> findByUserId(Long userId) {
        return accountRepository.findAllByUserId(userId);
    }

    @Transactional
    public void addAccount(AccountInfo accountInfo, User user) {
        Account account = new Account();
        account.setAccountNumber(accountInfo.getAccountNumber());
        account.setUserId(user.getId());
        account.setAccountType(accountTypeService.findByType("Рассчетный счет"));
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
