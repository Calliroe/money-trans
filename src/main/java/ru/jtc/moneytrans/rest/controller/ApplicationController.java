package ru.jtc.moneytrans.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.jtc.moneytrans.rest.dto.*;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.model.Role;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.rest.validation.PaymentException;
import ru.jtc.moneytrans.rest.validation.PaymentValidator;
import ru.jtc.moneytrans.service.AccountService;
import ru.jtc.moneytrans.service.PaymentService;
import ru.jtc.moneytrans.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/money-trans")
public class ApplicationController {

    private final UserService userService;
    private final PaymentService paymentService;
    private final AccountService accountService;
    private final PaymentValidator paymentValidator;
    private final String SUCCESS_STATUS = "SUCCESS";
    private final String FAILURE_STATUS = "FAILURE";

    @PostMapping(value = "/registration")
    public ResponseEntity<BaseResponse> createUser(@Valid @RequestBody RegistrationDto registrationDto) { // Поменять
        if (Objects.nonNull(userService.findByUsername(registrationDto.getUsername()))) {
            return ResponseEntity.ok(new BaseResponse(FAILURE_STATUS, "Пользователь с таким именем уже существует"));
        }
        userService.createUser(registrationDto.getUsername(), registrationDto.getPassword());
        return ResponseEntity.ok(new BaseResponse(SUCCESS_STATUS, "Пользователь успешно зарегистрирован"));
    }

    @PostMapping("/create-account")
    public ResponseEntity<BaseResponse> createAccount(@Valid @RequestBody AccountInfo accountInfo, @AuthenticationPrincipal User user) {
        accountService.createAccount(accountInfo, user);
        return ResponseEntity.ok(new BaseResponse(SUCCESS_STATUS, "Аккаунт успешно сохранён"));
    }

    @PostMapping("/transfer-money")
    public ResponseEntity<BaseResponse> transferMoney(@Valid @RequestBody PaymentDto paymentDto, @AuthenticationPrincipal User user) {
        try {
            paymentValidator.validate(paymentDto, user);
        } catch (PaymentException e) {
            return ResponseEntity.ok(new BaseResponse(FAILURE_STATUS, e.getMessage()));
        }
        paymentService.transferMoney(paymentDto);
        return ResponseEntity.ok(new BaseResponse(SUCCESS_STATUS, "Операция выполнена"));
    }

    @GetMapping
    public List<Payment> getPayments(@RequestBody(required = false) FilteringDto filteringDto, @AuthenticationPrincipal User user) {
        for (Role role : user.getRoles()) { // Разделить на два эндпоинта
            if (role.getRoleSignature().equals("ROLE_ADMIN")) {
                return paymentService.getAll(filteringDto);
            }
        }
        user.setAccounts(accountService.findAllByUserId(user.getId()));
        List<Payment> payments = new ArrayList<>();
        for (Account account : user.getAccounts()) {
            payments.addAll(paymentService.getAllByAccountId(account.getId(), filteringDto));
        }
        return payments;
    }

}
