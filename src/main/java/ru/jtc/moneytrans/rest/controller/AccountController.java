package ru.jtc.moneytrans.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.rest.dto.AccountInfo;
import ru.jtc.moneytrans.rest.validation.validator.AccountInfoValidator;
import ru.jtc.moneytrans.service.AccountService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/money-trans/account")
public class AccountController {

    private final AccountService accountService;
    private final AccountInfoValidator accountInfoValidator;

    @PostMapping("/create-account")
    public ResponseEntity<String> createAccount(@Valid @RequestBody AccountInfo accountInfo, @AuthenticationPrincipal User user) {
        accountInfoValidator.validate(accountInfo);
        accountService.createAccount(accountInfo, user);
        return ResponseEntity.ok("Аккаунт успешно сохранён");
    }

}
