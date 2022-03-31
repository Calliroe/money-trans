package ru.jtc.moneytrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jtc.moneytrans.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleSignature(String roleSignature);

}
