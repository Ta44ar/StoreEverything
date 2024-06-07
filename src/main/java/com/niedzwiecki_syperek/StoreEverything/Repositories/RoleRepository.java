package com.niedzwiecki_syperek.StoreEverything.Repositories;

import com.niedzwiecki_syperek.StoreEverything.db.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
