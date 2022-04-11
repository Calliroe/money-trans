package ru.jtc.moneytrans.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.jtc.moneytrans.model.Role;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.RoleRepository;
import ru.jtc.moneytrans.repository.UserRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {UserServiceTest.Initializer.class})
public class UserServiceTest extends AbstractServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
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

        User result = userService.findByUsername(user.getUsername());

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
        userService.createUser("keke", "kekeIsYou");

        User result = userRepository.findByUsername("keke");
        assertThat(result.getUsername()).isEqualTo("keke");
        assertThat(bCryptPasswordEncoder.matches("kekeIsYou", result.getPassword())).isTrue();
    }

    @Test
    public void loadUser_userIsExist_shouldReturnUserDetails() {
        User user = createUser();
        userRepository.save(user);

        UserDetails userDetails = userService.loadUserByUsername("keke");

        assertThat(userDetails.getUsername()).isEqualTo("keke");
        assertThat(bCryptPasswordEncoder.matches("kekeIsYou", userDetails.getPassword())).isTrue();
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUser_userIsNotExist_shouldThrowsAnException() {
        userService.loadUserByUsername("keke");
    }

    public User createUser() {
        User user = new User();
        user.setUsername("keke");
        user.setPassword("kekeIsYou");
        Role role = new Role();
        role.setId(1L);
        role.setRoleSignature("ROLE_USER");
        role.setRoleName("Пользователь");
        user.setRoles(Set.of(role));
        return user;
    }

}