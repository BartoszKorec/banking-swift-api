package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.entity.Headquarter;
import com.bartoszkorec.banking_swift_service.repository.HeadquarterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeadquarterServiceImpl implements HeadquarterService {

    private final HeadquarterRepository headquarterRepository;

    @Autowired
    public HeadquarterServiceImpl(HeadquarterRepository headquarterRepository) {
        this.headquarterRepository = headquarterRepository;
    }

    @Override
    public void processHeadquarter(Headquarter headquarter) {

        headquarterRepository.save(headquarter);
    }
}
