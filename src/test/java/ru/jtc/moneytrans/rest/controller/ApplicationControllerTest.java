package ru.jtc.moneytrans.rest.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.jtc.moneytrans.model.Role;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.rest.validation.PaymentValidator;
import ru.jtc.moneytrans.service.AccountService;
import ru.jtc.moneytrans.service.PaymentService;
import ru.jtc.moneytrans.service.UserService;

import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;
    @MockBean
    PaymentService paymentService;
    @MockBean
    AccountService accountService;
    @MockBean
    PaymentValidator paymentValidator;

    @Before
    public void setUp() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        Role role = new Role();
        role.setRoleSignature("USER_ROLE");
        role.setRoleName("Пользователь");
        user.setRoles(Set.of(role));
        user.setAccounts(null);
        Mockito.when(userService.findByUsername("username")).thenReturn(user);
        Mockito.when(userService.loadUserByUsername("username")).thenReturn(user);
    }

    @Test
    public void createUser_userExist_returnOkWithFailureStatus() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/money-trans/registration")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"username\":\"username\",\n\"password\":\"123456\"\n}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createUser_userNotExist_returnOkWithSuccessfulStatus() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/money-trans/registration")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"username\":\"name\",\n\"password\":\"123456\"\n}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createUser_incorrectData_returnError() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/money-trans/registration")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"username\":\"\",\n\"password\":\"\"\n}"))
                .andExpect(status().is(400))
                .andDo(print());
    }

    @Test
    public void createAccount_userIsNotAuthenticated_returnError() throws Exception {
        boolean result = Objects.equals(mvc.perform(MockMvcRequestBuilders
                        .post("/money-trans/create-account")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"accountNumber\":\"111111\",\n\"bic\":111111,\n\"balance\":1000.0}"))
                .andExpect(status().is(302))
                .andReturn()
                .getResponse()
                .getRedirectedUrl(), "http://localhost/money-trans/login");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void transferMoney_userIsNotAuthenticated_returnError() throws Exception {
        boolean result = Objects.equals(mvc.perform(MockMvcRequestBuilders
                        .post("/money-trans/transfer-money")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"payerAccountNumber\":\"111111\",\n\"receiverAccountNumber\":\"222222\",\n\"amount\":1000.0}"))
                .andExpect(status().is(302))
                .andReturn()
                .getResponse()
                .getRedirectedUrl(), "http://localhost/money-trans/login");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void getPayments_userIsNotAuthenticated_returnError() throws Exception {
        boolean result = Objects.equals(mvc.perform(MockMvcRequestBuilders
                        .get("/money-trans/transfer-money"))
                .andExpect(status().is(302))
                .andReturn()
                .getResponse()
                .getRedirectedUrl(), "http://localhost/money-trans/login");
        assertThat(result).isEqualTo(true);
    }

}
