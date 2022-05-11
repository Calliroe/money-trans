package ru.jtc.moneytrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.jtc.moneytrans.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Query("select u from User u join fetch u.roles where username = :username")
    User findByUsernameWithRoles(String username);

}
