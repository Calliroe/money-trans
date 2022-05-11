package ru.jtc.moneytrans.rest.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.jtc.moneytrans.AbstractIntegrationTest;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.UserRepository;
import ru.jtc.moneytrans.rest.dto.RegistrationDto;
import ru.jtc.moneytrans.rest.validation.response.ErrorResponse;
import ru.jtc.moneytrans.rest.validation.violation.ApplicationViolation;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void createUser_usernameDoesNotExist_shouldReturnOk() {
        RegistrationDto dto = new RegistrationDto("username", "password");

        ResponseEntity<String> registrationResponse = restTemplate.postForEntity(
                "/money-trans/user/registration",
                new HttpEntity<>(dto),
                String.class);

        assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registrationResponse.getBody()).isEqualTo("Пользователь успешно зарегистрирован");
    }

    @Test
    public void createUser_usernameExists_shouldReturnBadRequest() {
        User user = new User();
        user.setUsername("keke");
        user.setPassword("isYou");
        userRepository.save(user);
        RegistrationDto dto = new RegistrationDto("keke", "isNotYou");

        ResponseEntity<String> registrationResponse = restTemplate.postForEntity(
                "/money-trans/user/registration",
                new HttpEntity<>(dto),
                String.class);

        ErrorResponse error = new ErrorResponse();
        error.getViolations().add(new ApplicationViolation("Пользователь с таким именем уже существует"));
        assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(registrationResponse.getBody()).isEqualTo("{\"violations\":[{\"message\":\"Пользователь с таким именем уже существует\"}]}");
    }
}
