package ru.jtc.moneytrans.service;

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

import javax.transaction.Transactional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {UserServiceTest.Initializer.class})
@Transactional
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
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void findByUsername_userIsExist_shouldReturnUser() {
        User user = userService.findByUsername("admin");

        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(bCryptPasswordEncoder.matches("admin", user.getPassword())).isTrue();
    }

    @Test
    public void findByUsername_userIsNotExist_shouldReturnNull() {
        User user = userService.findByUsername("keke");

        assertThat(user).isEqualTo(null);
    }

    @Test
    public void createUser_validData_shouldCreate() {
        userService.createUser("keke", "kekeIsYou");

        User user = userService.findByUsername("keke");
        assertThat(user.getUsername()).isEqualTo("keke");
        assertThat(bCryptPasswordEncoder.matches("kekeIsYou", user.getPassword())).isTrue();
    }

    @Test
    public void loadUser_userIsExist_shouldReturnUserDetails() {
        userService.createUser("keke", "kekeIsYou");

        UserDetails userDetails = userService.loadUserByUsername("keke");

        assertThat(userDetails.getUsername()).isEqualTo("keke");
        assertThat(bCryptPasswordEncoder.matches("kekeIsYou", userDetails.getPassword())).isTrue();
        assertThat(userDetails.getAuthorities()).isEqualTo(Set.of(createUserRole()));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUser_userIsNotExist_shouldThrowsAnException() {
        userService.loadUserByUsername("keke");
    }

    public Role createUserRole() {
        Role role = new Role();
        role.setId(2L);
        role.setRoleSignature("ROLE_USER");
        role.setRoleName("Пользователь");
        return role;
    }
}