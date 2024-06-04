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
}
