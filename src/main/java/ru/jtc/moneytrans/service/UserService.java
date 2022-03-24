package ru.jtc.moneytrans.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.jtc.moneytrans.model.Role;
import ru.jtc.moneytrans.model.User;
import ru.jtc.moneytrans.repository.RoleRepository;
import ru.jtc.moneytrans.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public boolean isAlreadyExist(String username) {
        return Objects.nonNull(userRepository.findByUsername(username));
    }

    @Transactional
    public void save(String username, String password) {
        boolean isExist = isAlreadyExist(username);
        if (!isExist) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            Role role = roleRepository.findByRoleSignature("ROLE_USER");
            user.setRoles(Set.of(role));
            userRepository.save(user);
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.error("in loadByUsername");
        User user = userRepository.findByUsername(username);

        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }
}
