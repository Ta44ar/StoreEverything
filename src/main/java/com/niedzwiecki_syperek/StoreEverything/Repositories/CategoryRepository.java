package com.niedzwiecki_syperek.StoreEverything.Repositories;

import com.niedzwiecki_syperek.StoreEverything.db.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
