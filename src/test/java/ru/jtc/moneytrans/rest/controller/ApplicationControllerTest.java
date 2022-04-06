package ru.jtc.moneytrans.rest.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.jtc.moneytrans.model.Account;
import ru.jtc.moneytrans.model.Payment;
import ru.jtc.moneytrans.model.Role;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.rest.validation.PaymentValidator;
import ru.jtc.moneytrans.service.AccountService;
import ru.jtc.moneytrans.service.PaymentService;
import ru.jtc.moneytrans.service.UserService;

import java.security.Principal;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTest {


    /*    @Autowired
        private WebApplicationContext context;*/
    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;
    @MockBean
    PaymentService paymentService;
    @MockBean
    AccountService accountService;
    @MockBean
    PaymentValidator paymentValidator;

/*    @Before
    public void init() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }*/

    @Before
    public void setUp() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("$2a$12$86WC2SvpEIhd8ktoaPILYO65XdQSOj34cOQpzPy9BadXHiAvlNiK2");
        user.setId(3L);
        Role role = new Role();
        role.setRoleSignature("USER_ROLE");
        role.setRoleName("Пользователь");
        user.setRoles(Set.of(role));
        user.setAccounts(null);
        Mockito.when(userService.findByUsername("username")).thenReturn(user);
        Mockito.when(userService.loadUserByUsername("username")).thenReturn(user);
        Account account = new Account();
        account.setId(9L);
        Payment payment = new Payment();
        payment.setPayerAccount(account);
    }

    @Test
    public void createUser_userExist_returnOkWithFailureStatus() throws Exception {
        mvc.perform(post("/money-trans/registration")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"username\":\"username\",\n\"password\":\"123456\"\n}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createUser_userNotExist_returnOkWithSuccessfulStatus() throws Exception {
        mvc.perform(post("/money-trans/registration")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"username\":\"name\",\n\"password\":\"123456\"\n}"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createUser_incorrectData_returnError() throws Exception {
        mvc.perform(post("/money-trans/registration")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"username\":\"\",\n\"password\":\"\"\n}"))
                .andExpect(status().is(400));
    }

    @Test
    public void createAccount_userIsNotAuthenticated_returnError() throws Exception {
        mvc.perform(post("/money-trans/create-account")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"accountNumber\":\"111111\",\n\"bic\":111111,\n\"balance\":1000.0}"))
                .andExpect(redirectedUrl("http://localhost/money-trans/login"));
    }

    @Test
    public void transferMoney_userIsNotAuthenticated_returnError() throws Exception {
        mvc.perform(post("/money-trans/transfer-money")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .content("{\n\"payerAccountNumber\":\"111111\",\n\"receiverAccountNumber\":\"222222\",\n\"amount\":1000.0}"))
                .andExpect(redirectedUrl("http://localhost/money-trans/login"));
    }

    @Test
    public void getPayments_userIsNotAuthenticated_returnError() throws Exception {
        mvc.perform(get("/money-trans/transfer-money"))
                .andExpect(redirectedUrl("http://localhost/money-trans/login"));
    }

    @Test
    public void login_correctLoginAndPassword_performAuthenticationAndRedirectOnTheHomePage() throws Exception {
        mvc.perform(formLogin("/money-trans/login")
                        .user("username", "username")
                        .password("password", "password"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void login_incorrectLoginAndPassword_returnLoginError() throws Exception {
        mvc.perform(formLogin("/money-trans/login")
                        .user("username", "username")
                        .password("password", "pass"))
                .andExpect(redirectedUrl("/money-trans/login?error"));
    }

/*    @Test
    @WithMockUser(username = "username")
    public void getPayments_userIsAuthenticated_returnPayments() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/money-trans")
                        .principal(createPrincipal()))
                .andDo(print());
    }*/

    private Principal createPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return "username";
            }

            public String getPassword() {
                return "password";
            }

            public Set<Role> getRoles() {
                Role role = new Role();
                role.setRoleSignature("USER_ROLE");
                role.setRoleName("Пользователь");
                return Set.of(role);
            }

            public Long getId() {
                return 6L;
            }
        };
    }
}
