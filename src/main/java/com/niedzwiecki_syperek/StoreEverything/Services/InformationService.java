package com.niedzwiecki_syperek.StoreEverything.Services;

import com.niedzwiecki_syperek.StoreEverything.Repositories.InformationRepository;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Information;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InformationService {
    @Autowired
    private InformationRepository infoRepo;

    public void save(Information info) {
        infoRepo.save(info);
    }

    public Information findById(Long id) {
        return infoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid information Id:" + id));
    }

    public void update(Information info) {
        Information existingInfo = infoRepo.findById(info.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid information Id:" + info.getId()));
        existingInfo.setTitle(info.getTitle());
        existingInfo.setContent(info.getContent());
        existingInfo.setLink(info.getLink());
        existingInfo.setCategory(info.getCategory());
        infoRepo.save(existingInfo);
    }

    public void delete(Long id) {
        Information info = infoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid information Id:" + id));
        infoRepo.delete(info);
    }
}
