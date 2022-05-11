package ru.jtc.moneytrans.rest.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.jtc.moneytrans.AbstractIntegrationTest;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.PaymentRepository;
import ru.jtc.moneytrans.rest.dto.AccountInfo;
import ru.jtc.moneytrans.rest.dto.PaymentInfo;
import ru.jtc.moneytrans.service.AccountService;
import ru.jtc.moneytrans.service.PaymentService;
import ru.jtc.moneytrans.service.UserService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.reflect.TypeUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentControllerTest extends AbstractIntegrationTest {

    @Autowired
    UserService userService;
    @Autowired
    AccountService accountService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository paymentRepository;

    @Before
    public void beforeTest() {
        paymentRepository.deleteAll();
    }

    @Test
    public void transferMoney_correctData_shouldReturnOk() {
        User user = createUser("keke", "isYou");
        accountService.createAccount(createAccountInfo(111L), user);
        accountService.createAccount(createAccountInfo(222L), user);
        String cookie = getCookieForUser("/money-trans/login?username=keke&password=isYou");
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", cookie);
        PaymentInfo paymentInfo = createPaymentInfo(111L, 222L);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/money-trans/payment/transfer-money",
                HttpMethod.POST,
                new HttpEntity<>(paymentInfo, header),
                String.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("Операция выполнена");
    }

    @Test
    public void transferMoney_incorrectData_shouldReturnBadRequest() {
        User user = createUser("me", "isYou");
        accountService.createAccount(createAccountInfo(333L), user);
        accountService.createAccount(createAccountInfo(444L), user);
        String cookie = getCookieForUser("/money-trans/login?username=me&password=isYou");
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", cookie);
        PaymentInfo paymentInfo = createPaymentInfo(33L, 444L);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/money-trans/payment/transfer-money",
                HttpMethod.POST,
                new HttpEntity<>(paymentInfo, header),
                String.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("{\"violations\":[{\"message\":\"Счёта отправителя с данным номером и БИК не существует\"}]}");
    }

    @Test
    public void transferMoney_userNotAuthenticated_shouldRedirectToLoginPage() {
        User user = createUser("empty", "isYou");
        accountService.createAccount(createAccountInfo(1L), user);
        accountService.createAccount(createAccountInfo(2L), user);
        PaymentInfo paymentInfo = createPaymentInfo(1L, 2L);

        HttpHeaders response = restTemplate.exchange(
                "/money-trans/payment/transfer-money",
                HttpMethod.POST,
                new HttpEntity<>(paymentInfo),
                String.class
        ).getHeaders();

        assertThat(Objects.requireNonNull(response.getLocation()).toString().endsWith("/money-trans/login")).isTrue();
    }

    @Test
    public void getPaymentsForUser_withoutFiltering_shouldReturnAllUserPayments() {
        User user1 = createUser("baba", "isYou");
        User user2 = createUser("skull", "isDefeat");
        User user3 = createUser("flag", "isWin");
        accountService.createAccount(createAccountInfo(1L), user1);
        accountService.createAccount(createAccountInfo(2L), user2);
        accountService.createAccount(createAccountInfo(3L), user3);
        paymentService.transferMoney(createPaymentInfo(1L, 2L));
        paymentService.transferMoney(createPaymentInfo(1L, 3L));
        paymentService.transferMoney(createPaymentInfo(2L, 3L));
        paymentService.transferMoney(createPaymentInfo(3L, 2L));
        paymentService.transferMoney(createPaymentInfo(2L, 1L));
        String cookie = getCookieForUser("/money-trans/login?username=baba&password=isYou");
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", cookie);

        ResponseEntity<List<PaymentInfo>> responseEntity = restTemplate.exchange(
                "/money-trans/payment/get-payments/user",
                HttpMethod.GET,
                new HttpEntity<>(header),
                ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, PaymentInfo.class))
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(List.of(
                createPaymentInfo(1L, 2L),
                createPaymentInfo(1L, 3L),
                createPaymentInfo(2L, 1L)
        ));
    }

    @Test
    public void getPaymentsForUser_withFiltering_shouldReturnFilteredUserPayments() {
        User user1 = createUser("koko", "isYou");
        User user2 = createUser("jojo", "isDefeat");
        User user3 = createUser("mojo", "isWin");
        accountService.createAccount(createAccountInfo(4L), user1);
        accountService.createAccount(createAccountInfo(5L), user2);
        accountService.createAccount(createAccountInfo(6L), user3);
        paymentService.transferMoney(createPaymentInfo(4L, 5L));
        paymentService.transferMoney(createPaymentInfo(4L, 6L));
        paymentService.transferMoney(createPaymentInfo(5L, 6L));
        paymentService.transferMoney(createPaymentInfo(6L, 5L));
        paymentService.transferMoney(createPaymentInfo(5L, 4L));
        String cookie = getCookieForUser("/money-trans/login?username=koko&password=isYou");
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", cookie);

        ResponseEntity<List<PaymentInfo>> responseEntity = restTemplate.exchange(
                "/money-trans/payment/get-payments/user?payerAccountNumber=123456&payerBic=4",
                HttpMethod.GET,
                new HttpEntity<>(header),
                ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, PaymentInfo.class))
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(List.of(
                createPaymentInfo(4L, 5L),
                createPaymentInfo(4L, 6L)
        ));
    }

    @Test
    public void getPaymentsForUser_withFilteringByDate_shouldReturnFilteredUserPayments() {
        User user1 = createUser("kokoya", "isYou");
        User user2 = createUser("jojoya", "isDefeat");
        User user3 = createUser("mojoya", "isWin");
        accountService.createAccount(createAccountInfo(34L), user1);
        accountService.createAccount(createAccountInfo(35L), user2);
        accountService.createAccount(createAccountInfo(36L), user3);
        paymentService.transferMoney(createPaymentInfo(34L, 35L));
        paymentService.transferMoney(createPaymentInfo(34L, 36L));
        paymentService.transferMoney(createPaymentInfo(35L, 36L));
        paymentService.transferMoney(createPaymentInfo(36L, 35L));
        paymentService.transferMoney(createPaymentInfo(35L, 34L));
        String cookie = getCookieForUser("/money-trans/login?username=kokoya&password=isYou");
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", cookie);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        ResponseEntity<List<PaymentInfo>> responseEntity = restTemplate.exchange(
                "/money-trans/payment/get-payments/user?createDate=" + formatter.format(new Date()),
                HttpMethod.GET,
                new HttpEntity<>(header),
                ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, PaymentInfo.class))
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(List.of(
                createPaymentInfo(34L, 35L),
                createPaymentInfo(34L, 36L),
                createPaymentInfo(35L, 34L)
        ));
    }

    @Test
    public void getPaymentsForAdmin_withoutFiltering_shouldReturnAllPayments() {
        User user1 = createUser("babe", "isYou");
        User user2 = createUser("skate", "isDefeat");
        User user3 = createUser("floor", "isWin");
        accountService.createAccount(createAccountInfo(99L), user1);
        accountService.createAccount(createAccountInfo(98L), user2);
        accountService.createAccount(createAccountInfo(97L), user3);
        paymentService.transferMoney(createPaymentInfo(99L, 98L));
        paymentService.transferMoney(createPaymentInfo(99L, 97L));
        paymentService.transferMoney(createPaymentInfo(98L, 97L));
        paymentService.transferMoney(createPaymentInfo(97L, 98L));
        paymentService.transferMoney(createPaymentInfo(98L, 99L));
        String cookie = getCookieForUser("/money-trans/login?username=admin&password=admin");
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", cookie);

        ResponseEntity<List<PaymentInfo>> responseEntity = restTemplate.exchange(
                "/money-trans/payment/get-payments/admin",
                HttpMethod.GET,
                new HttpEntity<>(header),
                ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, PaymentInfo.class))
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(List.of(
                createPaymentInfo(99L, 98L),
                createPaymentInfo(99L, 97L),
                createPaymentInfo(98L, 97L),
                createPaymentInfo(97L, 98L),
                createPaymentInfo(98L, 99L)
        ));
    }

    @Test
    public void getPaymentsForAdmin_withFiltering_shouldReturnAllFilteredPayments() {
        User user1 = createUser("kokos", "isYou");
        User user2 = createUser("jojos", "isDefeat");
        User user3 = createUser("mojos", "isWin");
        accountService.createAccount(createAccountInfo(66L), user1);
        accountService.createAccount(createAccountInfo(67L), user2);
        accountService.createAccount(createAccountInfo(68L), user3);
        paymentService.transferMoney(createPaymentInfo(66L, 67L));
        paymentService.transferMoney(createPaymentInfo(66L, 68L));
        paymentService.transferMoney(createPaymentInfo(67L, 68L));
        paymentService.transferMoney(createPaymentInfo(68L, 67L));
        paymentService.transferMoney(createPaymentInfo(67L, 66L));
        String cookie = getCookieForUser("/money-trans/login?username=admin&password=admin");
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", cookie);

        ResponseEntity<List<PaymentInfo>> responseEntity = restTemplate.exchange(
                "/money-trans/payment/get-payments/admin?payerAccountNumber=123456&payerBic=66",
                HttpMethod.GET,
                new HttpEntity<>(header),
                ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, PaymentInfo.class))
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(List.of(
                createPaymentInfo(66L, 67L),
                createPaymentInfo(66L, 68L)
        ));
    }

    private String getCookieForUser(String loginUrl) {
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                loginUrl,
                HttpEntity.EMPTY,
                String.class);
        return Objects.requireNonNull(loginResponse.getHeaders().get("Set-Cookie")).get(0);
    }

    private AccountInfo createAccountInfo(Long bic) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountNumber("123456");
        accountInfo.setAccountType("Рассчетный счет");
        accountInfo.setBalance(new BigDecimal("2000.0"));
        accountInfo.setBic(bic);
        return accountInfo;
    }

    private User createUser(String username, String password) {
        userService.createUser(username, password);
        return userService.findByUsername(username);
    }

    private PaymentInfo createPaymentInfo(Long payerBic, Long receiverBic) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPayerAccountNumber("123456");
        paymentInfo.setPayerBic(payerBic);
        paymentInfo.setReceiverAccountNumber("123456");
        paymentInfo.setReceiverBic(receiverBic);
        paymentInfo.setAmount(new BigDecimal("100.0"));
        return paymentInfo;
    }

}
