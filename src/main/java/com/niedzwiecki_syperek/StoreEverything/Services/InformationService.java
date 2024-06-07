package com.niedzwiecki_syperek.StoreEverything.Services;

import com.niedzwiecki_syperek.StoreEverything.Repositories.InformationRepository;
import com.niedzwiecki_syperek.StoreEverything.Repositories.UserRepository;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Information;
import com.niedzwiecki_syperek.StoreEverything.db.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

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

    public List<Information> findSharedWithMeInfos(Long userId) {
        List<Information> allInformations = infoRepo.findAll();
        List<Information> forMeInformations = new ArrayList<>();

        for (Information information : allInformations) {
            List<UserEntity> sharedWithUsers = information.getSharedWithUsers();
            if (sharedWithUsers != null && sharedWithUsers.stream().anyMatch(user -> user.getId().equals(userId))) {
                forMeInformations.add(information);
            }
        }

        return forMeInformations;
    }
}
