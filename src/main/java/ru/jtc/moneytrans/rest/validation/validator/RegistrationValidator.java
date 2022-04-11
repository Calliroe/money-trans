package ru.jtc.moneytrans.rest.validation.validator;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.jtc.moneytrans.repository.UserRepository;
import ru.jtc.moneytrans.rest.dto.RegistrationDto;
import ru.jtc.moneytrans.rest.validation.exception.RegistrationException;

import java.util.Objects;

@Data
@Component
public class RegistrationValidator {

    private final UserRepository userRepository;

    public void validate(RegistrationDto dto) throws RegistrationException {

        if (Objects.nonNull(userRepository.findByUsername(dto.getUsername()))) {
            throw new RegistrationException("Пользователь с таким именем уже существует");
        }
    }
}
