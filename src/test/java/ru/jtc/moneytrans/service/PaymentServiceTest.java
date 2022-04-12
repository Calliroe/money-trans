package ru.jtc.moneytrans.service;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.jtc.moneytrans.date.DateProviderImpl;
import ru.jtc.moneytrans.model.*;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.AccountTypeRepository;
import ru.jtc.moneytrans.repository.PaymentRepository;
import ru.jtc.moneytrans.rest.dto.FilteringDto;
import ru.jtc.moneytrans.rest.dto.PaymentInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {PaymentServiceTest.Initializer.class})
public class PaymentServiceTest extends AbstractServiceTest {

    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountTypeRepository accountTypeRepository;
    @Autowired
    DateProviderImpl dateProvider;

    @After
    public void after() {
        paymentRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    public void getAllByAccountId_accountIdIsExistAndFilterByReceiverAccount_shouldReturnAllFiltered() {
        Account account1 = createAccount("accountNumber1");
        Account account2 = createAccount("accountNumber2");
        Account account3 = createAccount("accountNumber3");
        Payment payment1 = createPayment(account1, account2);
        Payment payment2 = createPayment(account1, account3);
        Payment payment3 = createPayment(account2, account3);
        Account userAccount = accountRepository.findByAccountNumberAndBic("accountNumber1", 123L);
        FilteringDto dto = new FilteringDto();
        dto.setPayerAccountId(userAccount.getId());


        List<Payment> payments = paymentService.getAllByAccountId(userAccount.getId(), dto);
        System.out.println(payment1.getCreateDate());

        assertThat(payments).isEqualTo(List.of(payment1, payment2));
    }

    @Test
    public void getAllByAccountId_accountIdIsExistAndWithoutFiltering_shouldReturnAllByAccountId() {
        Account account1 = createAccount("accountNumber1");
        Account account2 = createAccount("accountNumber2");
        Account account3 = createAccount("accountNumber3");
        Payment payment1 = createPayment(account1, account2);
        Payment payment2 = createPayment(account3, account1);
        Payment payment3 = createPayment(account2, account3);
        Account account = accountRepository.findByAccountNumberAndBic("accountNumber1", 123L);

        List<Payment> payments = paymentService.getAllByAccountId(account.getId(), null);

        assertThat(payments).isEqualTo(List.of(payment1, payment2));
    }

    @Test
    public void getAllByAccountId_accountIdIsNotExist_shouldReturnEmptyList() {
        Account account1 = createAccount("accountNumber1");
        Account account2 = createAccount("accountNumber2");
        Payment payment1 = createPayment(account1, account2);
        FilteringDto dto = new FilteringDto();
        dto.setReceiverAccountId(2L);

        List<Payment> payments = paymentService.getAllByAccountId(100L, null);
        List<Payment> payments2 = paymentService.getAllByAccountId(100L, dto);

        assertThat(payments).isEmpty();
        assertThat(payments2).isEmpty();
    }

    @Test
    public void gelAll_filterByNotExistPayerAccount_shouldReturnEmptyList() {
        Account account1 = createAccount("accountNumber1");
        Account account2 = createAccount("accountNumber2");
        Payment payment1 = createPayment(account1, account2);
        FilteringDto dto = new FilteringDto();
        dto.setPayerAccountId(100L);

        List<Payment> payments = paymentService.getAll(dto);

        assertThat(payments).isEmpty();
    }

    @Test
    public void gelAll_filterByPayerAccount_shouldReturnAllFiltered() {
        Account account1 = createAccount("accountNumber1");
        Account account2 = createAccount("accountNumber2");
        Payment payment1 = createPayment(account1, account2);
        Payment payment2 = createPayment(account2, account1);
        Account userAccount = accountRepository.findByAccountNumberAndBic("accountNumber1", 123L);
        FilteringDto dto = new FilteringDto();
        dto.setPayerAccountId(userAccount.getId());

        List<Payment> payments = paymentService.getAll(dto);

        assertThat(payments).isEqualTo(List.of(payment1));
    }

    @Test
    public void gelAll_withoutFiltering_shouldReturnAllPayments() {
        Account account1 = createAccount("accountNumber1");
        Account account2 = createAccount("accountNumber2");
        Payment payment1 = createPayment(account1, account2);
        Payment payment2 = createPayment(account2, account1);

        List<Payment> payments = paymentService.getAll(null);

        assertThat(payments).isEqualTo(List.of(payment1, payment2));
    }

    @Test
    public void transferMoney_validData_shouldTransferMoney() {
        Account account1 = createAccount("accountNumber1");
        Account account2 = createAccount("accountNumber2");
        PaymentInfo dto = new PaymentInfo();
        dto.setPayerAccountNumber("accountNumber1");
        dto.setPayerBic(123L);
        dto.setReceiverAccountNumber("accountNumber2");
        dto.setReceiverBic(123L);
        dto.setAmount(new BigDecimal("150.0"));

        paymentService.transferMoney(dto);

        assertThat(accountRepository.findByAccountNumberAndBic("accountNumber1", 123L).getBalance()).isEqualTo(new BigDecimal("850.0"));
        assertThat(accountRepository.findByAccountNumberAndBic("accountNumber2", 123L).getBalance()).isEqualTo(new BigDecimal("1150.0"));
        assertThat(paymentService.getAll(null).size()).isEqualTo(1);
    }

    public Account createAccount(String accountNumber) {
        AccountType accountType = accountTypeRepository.findByType("Рассчетный счет");
        Account account = new Account();
        account.setAccountType(accountType);
        account.setBic(123L);
        account.setBalance(new BigDecimal("1000.0"));
        account.setUserId(6L);
        account.setAccountNumber(accountNumber);
        accountRepository.save(account);
        return account;
    }

    public Payment createPayment(Account payerAccount, Account receiverAccount) {
        Payment payment = new Payment();
        Date date = dateProvider.currentDate();
        payment.setCreateDate(date);
        payment.setModifyDate(date);
        payment.setPayerAccount(payerAccount);
        payment.setReceiverAccount(receiverAccount);
        payment.setAmount(new BigDecimal("500.0"));
        paymentRepository.save(payment);
        return payment;
    }

}
