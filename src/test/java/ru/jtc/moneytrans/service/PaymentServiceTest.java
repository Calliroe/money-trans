package ru.jtc.moneytrans.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.jtc.moneytrans.model.*;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.PaymentRepository;
import ru.jtc.moneytrans.rest.dto.FilteringDto;
import ru.jtc.moneytrans.rest.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {PaymentServiceTest.Initializer.class})
public class PaymentServiceTest {

    private static PostgreSQLContainer sqlContainer;

    static {
        sqlContainer = new PostgreSQLContainer("postgres:10.7")
                .withDatabaseName("integration-tests-db")
                .withUsername("username")
                .withPassword("password");
        sqlContainer.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + sqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + sqlContainer.getUsername(),
                    "spring.datasource.password=" + sqlContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    AccountRepository accountRepository;

    @Before
    public void before() {
        Account account1 = createAccount("accountNumber1");
        Account account2 = createAccount("accountNumber2");
        Account account3 = createAccount("accountNumber3");
        Account account4 = createAccount("accountNumber4");

        createPayment(account1, account2);
        createPayment(account1, account3);
        createPayment(account4, account1);
        createPayment(account2, account3);
    }

    @After
    public void after() {
        paymentRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    public void getAllByAccountId_accountIdIsExistAndFilterByReceiverAccount_shouldReturnAllFiltered() {
        FilteringDto dto = new FilteringDto();
        Account account = accountRepository.findByAccountNumber("accountNumber1");
        dto.setPayerAccountId(account.getId());

        List<Payment> payments = paymentService.getAllByAccountId(account.getId(), dto);

        assertThat(payments.size()).isEqualTo(2);
    }

    @Test
    public void getAllByAccountId_accountIdIsExistAndWithoutFiltering_shouldReturnAllByAccountId() {
        Account account = accountRepository.findByAccountNumber("accountNumber1");
        List<Payment> payments = paymentService.getAllByAccountId(account.getId(), null);

        assertThat(payments.size()).isEqualTo(3);
    }

    @Test
    public void getAllByAccountId_accountIdIsNotExist_shouldReturnEmptyList() {
        FilteringDto dto = new FilteringDto();
        dto.setReceiverAccountId(3L);

        List<Payment> payments = paymentService.getAllByAccountId(100L, null);
        List<Payment> payments2 = paymentService.getAllByAccountId(100L, dto);

        assertThat(payments.size()).isEqualTo(0);
        assertThat(payments2.size()).isEqualTo(0);
    }

    @Test
    public void gelAll_filterByNotExistPayerAccount_shouldReturnEmptyList() {
        FilteringDto dto = new FilteringDto();
        dto.setPayerAccountId(100L);

        List<Payment> payments = paymentService.getAll(dto);

        assertThat(payments.size()).isEqualTo(0);
    }

    @Test
    public void gelAll_filterByPayerAccount_shouldReturnAllFiltered() {
        FilteringDto dto = new FilteringDto();
        Account account = accountRepository.findByAccountNumber("accountNumber1");
        dto.setPayerAccountId(account.getId());

        List<Payment> payments = paymentService.getAll(dto);

        assertThat(payments.size()).isEqualTo(2);
    }

    @Test
    public void gelAll_withoutFiltering_shouldReturnAllPayments() {
        List<Payment> payments = paymentService.getAll(null);

        assertThat(payments.size()).isEqualTo(4);
    }

    @Test
    public void transferMoney_validData_shouldTransferMoney() {
        createAccount("accountNumber5");
        createAccount("accountNumber6");
        PaymentDto dto = new PaymentDto();
        dto.setPayerAccountNumber("accountNumber5");
        dto.setReceiverAccountNumber("accountNumber6");
        dto.setAmount(new BigDecimal("150.0"));

        paymentService.transferMoney(dto);

        assertThat(accountRepository.findByAccountNumber("accountNumber5").getBalance()).isEqualTo(new BigDecimal("850.0"));
        assertThat(accountRepository.findByAccountNumber("accountNumber6").getBalance()).isEqualTo(new BigDecimal("1150.0"));
        assertThat(paymentService.getAll(null).size()).isEqualTo(5);
    }

    public Payment createPayment(Account payerAccount, Account receiverAccount) {
        Payment payment = new Payment();
        payment.setCreateDate(new Date(1L));
        payment.setModifyDate(new Date(1L));
        payment.setPayerAccount(payerAccount);
        payment.setReceiverAccount(receiverAccount);
        payment.setAmount(new BigDecimal("500.0"));
        paymentRepository.save(payment);
        return payment;
    }

    public Account createAccount(String accountNumber) {
        AccountType accountType = new AccountType();
        accountType.setId(1L);
        accountType.setType("Рассчетный счет");
        Account account = new Account();
        account.setAccountType(accountType);
        account.setBic(123L);
        account.setBalance(new BigDecimal("1000.0"));
        account.setUserId(6L);
        account.setAccountNumber(accountNumber);
        accountRepository.save(account);
        return account;
    }

}
