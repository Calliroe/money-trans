package ru.jtc.moneytrans.rest.validation.validator;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.rest.dto.PaymentInfo;
import ru.jtc.moneytrans.rest.validation.exception.PaymentException;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Component
public class PaymentValidator {

    private final AccountRepository accountRepository;

    public void validate(PaymentInfo dto, User user) throws PaymentException {
        Account payerAccount = accountRepository.findByAccountNumberAndBic(dto.getPayerAccountNumber(), dto.getPayerBic());
        Account receiverAccount = accountRepository.findByAccountNumberAndBic(dto.getReceiverAccountNumber(), dto.getReceiverBic());
        if (Objects.isNull(payerAccount)) {
            throw new PaymentException("Счёта отправителя с данным номером и БИК не существует");
        }
        if (Objects.isNull(receiverAccount)) {
            throw new PaymentException("Счёта получателя с данным номером и БИК не существует");
        }
        if (!payerAccount.getUserId().equals(user.getId())) {
            throw new PaymentException("Нет прав на перевод средств с данного счёта");
        }
        if (payerAccount.equals(receiverAccount)) {
            throw new PaymentException("Счёт отправителя не может совпадать с счётом получателя");
        }
        BigDecimal payerBalance = payerAccount.getBalance();
        BigDecimal amount = dto.getAmount();
        if (payerBalance.compareTo(amount) < 0) {
            throw new PaymentException("Недостаточно средств на счёте");
        }
    }

}
