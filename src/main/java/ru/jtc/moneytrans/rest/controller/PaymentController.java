package ru.jtc.moneytrans.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.rest.transformer.PaymentTransformer;
import ru.jtc.moneytrans.rest.dto.PaymentFilter;
import ru.jtc.moneytrans.rest.dto.PaymentInfo;
import ru.jtc.moneytrans.rest.validation.validator.PaymentValidator;
import ru.jtc.moneytrans.service.AccountService;
import ru.jtc.moneytrans.service.PaymentService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/money-trans/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final AccountService accountService;
    private final PaymentValidator paymentValidator;
    private final PaymentTransformer paymentTransformer;

    @PostMapping("/transfer-money")
    public ResponseEntity<String> transferMoney(@Valid @RequestBody PaymentInfo paymentDto, @AuthenticationPrincipal User user) {
        paymentValidator.validate(paymentDto, user);
        paymentService.transferMoney(paymentDto);
        return ResponseEntity.ok("Операция выполнена");
    }

    @GetMapping("/get-payments/user")
    public List<PaymentInfo> getPaymentsForUser(PaymentFilter paymentFilter, @AuthenticationPrincipal User user) {
        user.setAccounts(accountService.findAllByUserId(user.getId()));
        List<Payment> payments = new ArrayList<>();
        for (Account account : user.getAccounts()) {
            payments.addAll(paymentService.getAllByAccountId(account.getId(), paymentFilter));
        }
        return paymentTransformer.apply(payments.stream().distinct().collect(Collectors.toList()));
    }

    @GetMapping("/get-payments/admin")
    public ResponseEntity<List<PaymentInfo>> getPaymentsForAdmin(PaymentFilter paymentFilter) {
        List<Payment> payments = paymentService.getAll(paymentFilter);
        return ResponseEntity.ok(paymentTransformer.apply(payments));
    }

}
