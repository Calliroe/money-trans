package ru.jtc.moneytrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jtc.moneytrans.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
