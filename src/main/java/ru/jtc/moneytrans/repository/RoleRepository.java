package ru.jtc.moneytrans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jtc.moneytrans.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleSignature(String roleSignature);
}
