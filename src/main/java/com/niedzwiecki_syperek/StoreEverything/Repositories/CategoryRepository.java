package com.niedzwiecki_syperek.StoreEverything.Repositories;

import com.niedzwiecki_syperek.StoreEverything.db.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c AS infoCount FROM Category c LEFT JOIN Information i ON c.id = i.category.id GROUP BY c.id ORDER BY COUNT(i) DESC")
    List<Category> findAllSortedByPopularity();
}
