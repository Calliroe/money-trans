package ru.jtc.moneytrans.service;

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

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {PaymentServiceTest.Initializer.class})
@Transactional
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

    @Test
    public void getAllByAccountId_accountIdIsExistAndFilterByReceiverAccount_shouldReturnAllFiltered() {
        Account payerAccount1 = createAccount("accountNumber1");
        Account receiverAccount1 = createAccount("accountNumber2");
        Payment payment1 = createPayment(payerAccount1, receiverAccount1);
        Account receiverAccount2 = createAccount("accountNumber3");
        Payment payment2 = createPayment(payerAccount1, receiverAccount2);
        Payment payment3 = createPayment(receiverAccount2, payerAccount1);
        Payment payment4 = createPayment(receiverAccount2, receiverAccount1);
        FilteringDto dto = new FilteringDto();
        dto.setReceiverAccountId(payerAccount1.getId());

        List<Payment> payments = paymentService.getAllByAccountId(payerAccount1.getId(), dto);

        assertThat(payments.size()).isEqualTo(1);
        assertThat(payments).containsOnly(payment3);
    }

    @Test
    public void getAllByAccountId_accountIdIsExistAndWithoutFiltering_shouldReturnAllByAccountId() {
        Account payerAccount1 = createAccount("accountNumber1");
        Account receiverAccount1 = createAccount("accountNumber2");
        Payment payment1 = createPayment(payerAccount1, receiverAccount1);
        Account receiverAccount2 = createAccount("accountNumber3");
        Payment payment2 = createPayment(payerAccount1, receiverAccount2);
        Payment payment3 = createPayment(receiverAccount2, payerAccount1);
        Payment payment4 = createPayment(receiverAccount2, receiverAccount1);

        List<Payment> payments = paymentService.getAllByAccountId(payerAccount1.getId(), null);

        assertThat(payments.size()).isEqualTo(3);
        assertThat(payments).containsOnly(payment1, payment2, payment3);
    }

    @Test
    public void getAllByAccountId_accountIdIsNotExist_shouldReturnEmptyList() {
        Account payerAccount1 = createAccount("accountNumber1");
        Account receiverAccount1 = createAccount("accountNumber2");
        createPayment(payerAccount1, receiverAccount1);
        Account receiverAccount2 = createAccount("accountNumber3");
        createPayment(payerAccount1, receiverAccount2);
        FilteringDto dto = new FilteringDto();
        dto.setReceiverAccountId(payerAccount1.getId());

        List<Payment> payments = paymentService.getAllByAccountId(100L, null);
        List<Payment> payments2 = paymentService.getAllByAccountId(100L, dto);

        assertThat(payments.size()).isEqualTo(0);
        assertThat(payments2.size()).isEqualTo(0);
    }

    @Test
    public void gelAll_filterByNotExistPayerAccount_shouldReturnEmptyList() {
        Account payerAccount1 = createAccount("accountNumber1");
        Account receiverAccount1 = createAccount("accountNumber2");
        createPayment(payerAccount1, receiverAccount1);
        Account payerAccount2 = createAccount("accountNumber3");
        Account receiverAccount2 = createAccount("accountNumber4");
        createPayment(payerAccount2, receiverAccount2);
        FilteringDto dto = new FilteringDto();
        dto.setPayerAccountId(100L);

        List<Payment> payments = paymentService.getAll(dto);

        assertThat(payments.size()).isEqualTo(0);
    }

    @Test
    public void gelAll_filterByPayerAccount_shouldReturnAllFiltered() {
        Account payerAccount1 = createAccount("accountNumber1");
        Account receiverAccount1 = createAccount("accountNumber2");
        Payment payment1 = createPayment(payerAccount1, receiverAccount1);
        Account payerAccount2 = createAccount("accountNumber3");
        Account receiverAccount2 = createAccount("accountNumber4");
        Payment payment2 = createPayment(payerAccount2, receiverAccount2);
        FilteringDto dto = new FilteringDto();
        dto.setPayerAccountId(payerAccount1.getId());
        List<Payment> payments = paymentService.getAll(dto);

        assertThat(payments.size()).isEqualTo(1);
        assertThat(payments).containsOnly(payment1);
    }

    @Test
    public void gelAll_withoutFiltering_shouldReturnAllPayments() {
        Account payerAccount1 = createAccount("accountNumber1");
        Account receiverAccount1 = createAccount("accountNumber2");
        Payment payment1 = createPayment(payerAccount1, receiverAccount1);
        Account payerAccount2 = createAccount("accountNumber3");
        Account receiverAccount2 = createAccount("accountNumber4");
        Payment payment2 = createPayment(payerAccount2, receiverAccount2);

        List<Payment> payments = paymentService.getAll(null);

        assertThat(payments.size()).isEqualTo(2);
        assertThat(payments).containsOnly(payment1, payment2);
    }

    @Test
    public void transferMoney_validData_shouldTransferMoney() {
        createAccount("accountNumber1");
        createAccount("accountNumber2");
        PaymentDto dto = new PaymentDto();
        dto.setPayerAccountNumber("accountNumber1");
        dto.setReceiverAccountNumber("accountNumber2");
        dto.setAmount(150.0);

        paymentService.transferMoney(dto);

        assertThat(accountRepository.findByAccountNumber("accountNumber1").getBalance()).isEqualTo(850.0);
        assertThat(accountRepository.findByAccountNumber("accountNumber2").getBalance()).isEqualTo(1150.0);
        assertThat(paymentService.getAll(null).size()).isEqualTo(1);
    }

    public Payment createPayment(Account payerAccount, Account receiverAccount) {
        Payment payment = new Payment();
        payment.setCreateDate(new Date(1L));
        payment.setModifyDate(new Date(1L));
        payment.setPayerAccount(payerAccount);
        payment.setReceiverAccount(receiverAccount);
        payment.setAmount(500.0);
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
        account.setBalance(1000.0);
        account.setUserId(6L);
        account.setAccountNumber(accountNumber);
        accountRepository.save(account);
        account.setId(accountRepository.findByAccountNumber(accountNumber).getId());
        return account;
    }
}
