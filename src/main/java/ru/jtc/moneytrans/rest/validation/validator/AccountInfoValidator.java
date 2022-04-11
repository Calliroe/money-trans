package ru.jtc.moneytrans.rest.validation.validator;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.AccountType;
import ru.jtc.moneytrans.repository.AccountRepository;
import ru.jtc.moneytrans.repository.AccountTypeRepository;
import ru.jtc.moneytrans.rest.dto.AccountInfo;
import ru.jtc.moneytrans.rest.validation.exception.AccountCreatingException;

import java.util.Objects;

@Data
@Component
public class AccountInfoValidator {

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;

    public void validate(AccountInfo accountInfo) throws AccountCreatingException {
        Account account = accountRepository.findByAccountNumberAndBic(accountInfo.getAccountNumber(), accountInfo.getBic());
        if (Objects.nonNull(account)) {
            throw new AccountCreatingException("Аккаунт c данным номером счёта и БИК уже существует");
        }
        AccountType accountType = accountTypeRepository.findByType(accountInfo.getAccountType());
        if (Objects.isNull(accountType)) {
            throw new AccountCreatingException("Данного типа аккаунта не существует");
        }
    }

}
