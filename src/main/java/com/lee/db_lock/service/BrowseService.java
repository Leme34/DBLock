package com.lee.db_lock.service;

import com.lee.db_lock.entity.Browse;
import com.lee.db_lock.repository.BrowseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BrowseService {

    @Autowired
    private BrowseRepository browseRepository;

    public void save(Browse browse){
        browseRepository.save(browse);
    }


}
