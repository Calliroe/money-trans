package ru.jtc.moneytrans.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.jtc.moneytrans.model.Role;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {UserServiceTest.Initializer.class})
public class UserServiceTest {

    private static PostgreSQLContainer sqlContainer;

    static {
        sqlContainer = new PostgreSQLContainer("postgres:10.7")
                .withDatabaseName("integration-tests-db")
                .withUsername("username")
                .withPassword("password");
        sqlContainer.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + sqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + sqlContainer.getUsername(),
                    "spring.datasource.password=" + sqlContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Before
    public void before() {
        userRepository.deleteAll();
    }

    @Test
    public void findByUsername_userIsExist_shouldReturnUser() {
        User user = createUser();
        userRepository.save(user);

        User result = userService.findByUsername("keke");

        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    public void findByUsername_userIsNotExist_shouldReturnNull() {
        User user = userService.findByUsername("keke");

        assertThat(user).isEqualTo(null);
    }

    @Test
    public void createUser_validData_shouldCreate() {
        User expect = createUser();

        userService.createUser("keke", "kekeIsYou");

        User result = userRepository.findByUsername("keke");
        assertThat(result.getUsername()).isEqualTo(expect.getUsername());
        assertThat(bCryptPasswordEncoder.matches(expect.getPassword(), result.getPassword())).isTrue();
    }

    @Test
    public void loadUser_userIsExist_shouldReturnUserDetails() {
        User user = createUser();
        userRepository.save(user);

        UserDetails userDetails = userService.loadUserByUsername("keke");

        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(user.getPassword()).isEqualTo(userDetails.getPassword());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUser_userIsNotExist_shouldThrowsAnException() {
        userService.loadUserByUsername("keke");
    }

    public User createUser() {
        User user = new User();
        user.setUsername("keke");
        user.setPassword("kekeIsYou");
        return user;
    }

    public Role createUserRole() {
        Role role = new Role();
        role.setId(2L);
        role.setRoleSignature("ROLE_USER");
        role.setRoleName("Пользователь");
        return role;
    }

}