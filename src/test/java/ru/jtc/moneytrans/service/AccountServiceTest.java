package ru.jtc.moneytrans.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.AccountType;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.AccountTypeRepository;
import ru.jtc.moneytrans.rest.dto.AccountInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {AccountServiceTest.Initializer.class})
public class AccountServiceTest extends AbstractServiceTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountTypeRepository accountTypeRepository;

    @Before
    public void before() {
        accountRepository.deleteAll();
    }

    @Test
    public void save_validData_shouldSaveAccount() {
        Account account = createAccount(1L, "num1");

        accountService.saveAccount(account);

        assertThat(accountRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll()).containsOnly(account);
    }

    @Test
    public void findAllByUserId_existUserId_shouldReturnAll() {
        Account account1 = createAccount(1L, "num1");
        Account account2 = createAccount(1L, "num2");
        Account account3 = createAccount(2L, "num3");
        accountRepository.saveAll(List.of(account1, account2, account3));

        Set<Account> accountSet = accountService.findAllByUserId(1L);

        assertThat(accountSet.size()).isEqualTo(2);
        assertThat(accountSet).containsOnly(account1, account2);
    }

    @Test
    public void findAllByUserId_notExistUserId_shouldReturnEmptySet() {
        Account account1 = createAccount(1L, "num1");
        Account account2 = createAccount(1L, "num2");
        Account account3 = createAccount(2L, "num3");
        accountRepository.saveAll(List.of(account1, account2, account3));

        Set<Account> accountSet = accountService.findAllByUserId(4L);

        assertThat(accountSet).isEmpty();
    }

    @Test
    public void createAccount_validData_shouldCreateAccount() {
        AccountInfo accountInfo = createAccountInfo();
        User user = new User();
        user.setId(1L);

        accountService.createAccount(accountInfo, user);

        Account result = accountRepository.findByAccountNumberAndBic(accountInfo.getAccountNumber(), accountInfo.getBic());
        Account expect = createAccount(user.getId(), accountInfo.getAccountNumber());
        expect.setVersion(0L);
        expect.setId(result.getId());
        assertThat(accountRepository.findAll().size()).isEqualTo(1);
        assertThat(result).isEqualTo(expect);
    }

    public Account createAccount(Long userId, String accountNumber) {
        AccountType accountType = accountTypeRepository.findByType("Рассчетный счет");
        Account account = new Account();
        account.setAccountType(accountType);
        account.setBic(123L);
        account.setBalance(new BigDecimal("1000.0"));
        account.setUserId(userId);
        account.setAccountNumber(accountNumber);
        return account;
    }

    public AccountInfo createAccountInfo() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountNumber("number");
        accountInfo.setBic(123L);
        accountInfo.setBalance(new BigDecimal("1000.0"));
        accountInfo.setAccountType("Рассчетный счет");
        return accountInfo;
    }

}
