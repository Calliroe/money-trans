package ru.jtc.moneytrans.rest.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.jtc.moneytrans.rest.dto.AccountInfo;
import ru.jtc.moneytrans.rest.dto.RegistrationDto;
import ru.jtc.moneytrans.rest.dto.BaseResponse;
import ru.jtc.moneytrans.rest.dto.PaymentDto;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.model.Role;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.rest.exception.PaymentException;
import ru.jtc.moneytrans.service.AccountService;
import ru.jtc.moneytrans.service.AccountTypeService;
import ru.jtc.moneytrans.service.PaymentService;
import ru.jtc.moneytrans.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController

@RequestMapping("/money-trans")
@AllArgsConstructor
public class ApplicationController {

    private final UserService userService;
    private final PaymentService paymentService;
    private final AccountTypeService accountTypeService;
    private final AccountService accountService;
    private final String SUCCESS_STATUS = "SUCCESS";
    private final String FAILURE_STATUS = "FAILURE";

    @PostMapping("/hello")
    public BaseResponse hello() {
        return new BaseResponse(SUCCESS_STATUS, "Hello!");
    }

    @PostMapping("/registration")
    public BaseResponse addUser(@Valid @RequestBody RegistrationDto registrationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new BaseResponse(FAILURE_STATUS, "Введены некорректные данные");
        }
        if (Objects.nonNull(userService.findByUsername(registrationDto.getUsername()))) {
            return new BaseResponse(FAILURE_STATUS, "Пользователь с таким именем уже существует");
        }
        userService.save(registrationDto.getUsername(), registrationDto.getPassword());
        return new BaseResponse(SUCCESS_STATUS, "Пользователь успешно зарегистрирован");
    }

    @PostMapping("/create-account")
    public BaseResponse createAccount(@Valid @RequestBody AccountInfo accountInfo, @AuthenticationPrincipal User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new BaseResponse(FAILURE_STATUS, "Введены некорректные данные");
        }
        accountService.addAccount(accountInfo, user);
        return new BaseResponse(SUCCESS_STATUS, "Аккаунт успешно сохранён");
    }

    @PostMapping("/transfer-money")
    public BaseResponse transferMoney(@Valid @RequestBody PaymentDto paymentDto, @AuthenticationPrincipal User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new BaseResponse(FAILURE_STATUS, "Введены некорректные данные");
        }
        Account account = accountService.findAccountByNumber(paymentDto.getPayerAccountNumber());
        if (!account.getUserId().equals(user.getId())) {
            return new BaseResponse(FAILURE_STATUS, "Вы не имеете прав на перевод");
        }
        try {
            paymentService.transferMoney(paymentDto);
            return new BaseResponse(SUCCESS_STATUS, "Операция выполнена");
        } catch (PaymentException e) {
            return new BaseResponse(FAILURE_STATUS, e.getMessage());
        }
    }

    @GetMapping
    public List<Payment> getPayments(@AuthenticationPrincipal User user) { //Сделать фильтрацию
        for (Role role : user.getRoles()) {
            if (role.getRoleSignature().equals("ROLE_ADMIN")) {
                return paymentService.getAll();
            }
        }
        List<Payment> payments = new ArrayList<>();
        List<Account> accounts = accountService.findByUserId(user.getId());
        for (Account account : accounts) {
            payments.addAll(paymentService.getAllByAccountId(account.getId()));
        }
        return payments;
    }
}
