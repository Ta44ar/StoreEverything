package com.niedzwiecki_syperek.StoreEverything.Services;

import com.niedzwiecki_syperek.StoreEverything.Repositories.InformationRepository;
import com.niedzwiecki_syperek.StoreEverything.Repositories.UserRepository;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Information;
import com.niedzwiecki_syperek.StoreEverything.db.entities.UserEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InformationService {
    @Autowired
    private InformationRepository infoRepo;
    @Autowired
    private UserRepository userRepo;

    public void save(Information info) {
        infoRepo.save(info);
    }

    public Information findById(Long id) {
        return infoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid information Id:" + id));
    }

    public List<Information> findByUserId(Long userId) {
        return infoRepo.findByUserEntityId(userId);
    }

    public void update(Information info) {
        Information existingInfo = infoRepo.findById(info.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid information Id:" + info.getId()));
        existingInfo.setTitle(info.getTitle());
        existingInfo.setContent(info.getContent());
        existingInfo.setCategory(info.getCategory());
        infoRepo.save(existingInfo);
    }

    public void delete(Long id) {
        Information info = infoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid information Id:" + id));
        infoRepo.delete(info);
    }

    public String generateShareableLink(Long id) {
        Information info = infoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid information Id:" + id));
        String shareableLink = UUID.randomUUID().toString();
        info.setShareableLink(shareableLink);
        infoRepo.save(info);
        return shareableLink;
    }

    public Information findByShareableLink(String shareableLink) {
        return infoRepo.findByShareableLink(shareableLink).orElseThrow(() -> new IllegalArgumentException("Invalid shareable link"));
    }

    public void shareInformationWithUser(Long informationId, Long userId, Long currentUserId) {
        Information information = infoRepo.findById(informationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid information Id:" + informationId));

        if (!information.getUserEntity().getId().equals(currentUserId)) {
            throw new SecurityException("You can only share your own information");
        }

        UserEntity userToShare = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));

        information.getSharedWithUsers().add(userToShare);
        infoRepo.save(information);
    }


    public List<Information> findSharedWithMeInfos(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {
        Specification<Information> spec = Specification.where(isSharedWithUser(userId));

        if (categoryId != null) {
            spec = spec.and(hasCategoryId(categoryId));
        }
        if (startDate != null) {
            spec = spec.and(hasDateAddedAfter(startDate));
        }
        if (endDate != null) {
            spec = spec.and(hasDateAddedBefore(endDate));
        }

        return infoRepo.findAll(spec);
    }

    public List<Information> findByUserIdWithFilters(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {
        Specification<Information> spec = Specification.where(hasUserId(userId));

        if (categoryId != null) {
            spec = spec.and(hasCategoryId(categoryId));
        }
        if (startDate != null) {
            spec = spec.and(hasDateAddedAfter(startDate));
        }
        if (endDate != null) {
            spec = spec.and(hasDateAddedBefore(endDate));
        }

        return infoRepo.findAll(spec);
    }

    private Specification<Information> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userEntity").get("id"), userId);
    }

    private Specification<Information> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<Information> hasDateAddedAfter(LocalDate startDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("dateAdded"), startDate);
    }

    private Specification<Information> hasDateAddedBefore(LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("dateAdded"), endDate);
    }

    private Specification<Information> isSharedWithUser(Long userId) {
        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> sharedWithUsers = root.join("sharedWithUsers", JoinType.LEFT);
            return criteriaBuilder.equal(sharedWithUsers.get("id"), userId);
        };
    }
}
