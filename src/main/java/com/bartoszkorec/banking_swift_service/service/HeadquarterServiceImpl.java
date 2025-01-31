package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.parser.HeadquarterDTO;
import com.bartoszkorec.banking_swift_service.entity.Headquarter;
import com.bartoszkorec.banking_swift_service.mapper.parser.HeadquarterMapper;
import com.bartoszkorec.banking_swift_service.repository.HeadquarterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeadquarterServiceImpl implements HeadquarterService {

    private final HeadquarterRepository headquarterRepository;
    private final HeadquarterMapper headquarterMapper;

    @Autowired
    public HeadquarterServiceImpl(HeadquarterRepository headquarterRepository, HeadquarterMapper headquarterMapper) {
        this.headquarterRepository = headquarterRepository;
        this.headquarterMapper = headquarterMapper;
    }

    @Override
    public void processHeadquarter(HeadquarterDTO headquarterDTO) {

        Headquarter headquarter = headquarterMapper.toEntity(headquarterDTO);
        headquarterRepository.save(headquarter);
    }
}
