package ru.jtc.moneytrans.service;

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

import javax.transaction.Transactional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {UserServiceTest.Initializer.class})
@Transactional
public class UserServiceTest extends AbstractServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void findByUsername_userIsExist_shouldReturnUser() {
        User user = createUser();
        userRepository.save(user);

        User result = userService.findByUsername(user.getUsername());

        assertThat(result).isEqualTo(user);
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
        assertThat(result.getRoles()).isEqualTo(Set.of(getUserRole()));
    }

    @Test
    public void loadUser_userIsExist_shouldReturnUserDetails() {
        User user = createUser();
        userRepository.save(user);

        UserDetails userDetails = userService.loadUserByUsername("keke");

        assertThat(userDetails.getUsername()).isEqualTo("keke");
        assertThat(userDetails.getPassword()).isEqualTo("kekeIsYou");
        assertThat(userDetails.getAuthorities()).isEqualTo(Set.of(getUserRole()));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUser_userIsNotExist_shouldThrowsAnException() {
        userService.loadUserByUsername("keke");
    }

    public User createUser() {
        User user = new User();
        user.setUsername("keke");
        user.setPassword("kekeIsYou");
        user.setRoles(Set.of(getUserRole()));
        return user;
    }

    public Role getUserRole() {
        return roleRepository.findByRoleSignature("ROLE_USER");
    }

}