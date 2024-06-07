package com.niedzwiecki_syperek.StoreEverything.Repositories;

import com.niedzwiecki_syperek.StoreEverything.db.entities.Information;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InformationRepository extends JpaRepository<Information, Long> {
    List<Information> findByUserEntityId(Long userId);

    Optional<Information> findByShareableLink(String shareableLink);

}
