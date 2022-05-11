package ru.jtc.moneytrans.rest.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import ru.jtc.moneytrans.AbstractIntegrationTest;
import ru.jtc.moneytrans.rest.dto.AccountInfo;
import ru.jtc.moneytrans.service.UserService;

import java.math.BigDecimal;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountControllerTest extends AbstractIntegrationTest {
    @Autowired
    UserService userService;

    @Test
    public void createAccount_correctData_shouldReturnOk() {
        userService.createUser("keke", "isYou");
        String cookie = getCookieForUser("/money-trans/login?username=keke&password=isYou");
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", cookie);
        AccountInfo accountInfo = createAccountInfo(111L);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/money-trans/account/create-account",
                HttpMethod.POST,
                new HttpEntity<>(accountInfo, header),
                String.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("Аккаунт успешно сохранён");
    }

    @Test
    public void createAccount_incorrectData_shouldReturnBadRequest() {
        userService.createUser("me", "isYou");
        String cookie = getCookieForUser("/money-trans/login?username=me&password=isYou");
        HttpHeaders header = new HttpHeaders();
        header.add("Cookie", cookie);
        AccountInfo accountInfo = createAccountInfo(null);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/money-trans/account/create-account",
                HttpMethod.POST,
                new HttpEntity<>(accountInfo, header),
                String.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("{\"violations\":[{\"fieldName\":\"bic\",\"message\":\"Введите БИК\"}]}");
    }

    @Test
    public void createAccount_userNotAuthenticated_shouldRedirectToLoginPage() {
        AccountInfo accountInfo = createAccountInfo(222L);

        HttpHeaders response = restTemplate.exchange(
                "/money-trans/account/create-account",
                HttpMethod.POST,
                new HttpEntity<>(accountInfo),
                String.class
        ).getHeaders();

        assertThat(Objects.requireNonNull(response.getLocation()).toString().endsWith("/money-trans/login")).isTrue();
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
        accountInfo.setBalance(new BigDecimal("200.0"));
        accountInfo.setBic(bic);
        return accountInfo;
    }

}
