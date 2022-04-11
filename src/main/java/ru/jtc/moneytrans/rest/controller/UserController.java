package ru.jtc.moneytrans.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jtc.moneytrans.rest.dto.RegistrationDto;
import ru.jtc.moneytrans.rest.validation.validator.RegistrationValidator;
import ru.jtc.moneytrans.service.UserService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/money-trans/user")
public class UserController {

    private final UserService userService;
    private final RegistrationValidator registrationValidator;

    @PostMapping(value = "/registration")
    public ResponseEntity<String> createUser(@Valid @RequestBody RegistrationDto registrationDto) {
        registrationValidator.validate(registrationDto);
        userService.createUser(registrationDto.getUsername(), registrationDto.getPassword());
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

}
