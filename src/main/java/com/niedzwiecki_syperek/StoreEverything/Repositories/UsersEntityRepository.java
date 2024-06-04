package com.niedzwiecki_syperek.StoreEverything.Repositories;

import com.niedzwiecki_syperek.StoreEverything.db.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersEntityRepository extends JpaRepository<User, Long> {

}
