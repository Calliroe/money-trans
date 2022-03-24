package ru.jtc.moneytrans.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.jtc.moneytrans.rest.dto.RegistrationDto;
import ru.jtc.moneytrans.rest.dto.TransferMoneyDto;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.model.Role;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.service.PaymentService;
import ru.jtc.moneytrans.service.UserService;

import java.util.*;

@RestController

@RequestMapping("/money-trans")
@AllArgsConstructor
public class ApplicationController {

    private final UserService userService;
    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @PostMapping("/hello")
    public String hello() {
        return "Hello!";
    }

    @PostMapping("/registration")
    public String addUser(@RequestBody RegistrationDto registrationDto) {
        if (Objects.nonNull(userService.findByUsername(registrationDto.getUsername()))) {
            return "Пользователь с таким именем уже существует";
        }
        userService.save(registrationDto.getUsername(), registrationDto.getPassword());
        return "Пользователь успешно зарегистрирован";
    }

    @PostMapping("/transfer-money")
    public String transferMoney(@RequestBody TransferMoneyDto transferMoneyDto) {
        try {
            paymentService.transferMoney(transferMoneyDto);
            return "Операция выполнена";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping
    public List<Payment> getPayments(@AuthenticationPrincipal User user) {
        for (Role role : user.getRoles()) {
            if (role.getRoleSignature().equals("ROLE_ADMIN")) {
                return paymentService.getAll();
            }
        }

        List<Payment> payments = new ArrayList<>();
        for (Account account : user.getAccounts()) {
            payments.addAll(paymentService.getAllByAccountId(account.getId()));
        }
        return payments;
    }
}
