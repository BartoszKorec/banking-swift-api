package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeadquartersServiceImpl implements HeadquartersService {

    private final HeadquartersRepository headquartersRepository;

    @Autowired
    public HeadquartersServiceImpl(HeadquartersRepository headquartersRepository) {
        this.headquartersRepository = headquartersRepository;
    }

    @Override
    public void processHeadquarters(Headquarters headquarters) {

        headquartersRepository.save(headquarters);
    }
}
