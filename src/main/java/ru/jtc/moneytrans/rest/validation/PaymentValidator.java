package ru.jtc.moneytrans.rest.validation;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.rest.dto.PaymentDto;

import java.util.Objects;

@Data
@Component
public class PaymentValidator {

    private final AccountRepository accountRepository;

    public void validate(PaymentDto dto, User user) throws PaymentException {
        Account payerAccount = accountRepository.findByAccountNumber(dto.getPayerAccountNumber());
        if (Objects.isNull(payerAccount)) {
            throw new PaymentException("Аккаунта отправителя с данным номером не существует");
        }
        if (!payerAccount.getUserId().equals(user.getId())) {
            throw new PaymentException("Нет прав на перевод средств с данного счёта");
        }
        if (dto.getPayerAccountNumber().equals(dto.getReceiverAccountNumber())) {
            throw new PaymentException("Номер счёта отправителя не может совпадать с номером счёта получателя");
        }
        double payerBalance = payerAccount.getBalance();
        double amount = dto.getAmount();
        if (payerBalance < amount) {
            throw new PaymentException("Недостаточно средств на счёте");
        }
        Account receiverAccount = accountRepository.findByAccountNumber(dto.getReceiverAccountNumber());
        if (Objects.isNull(receiverAccount)) {
            throw new PaymentException("Аккаунта получателя с данным номером не существует");
        }
    }

}
