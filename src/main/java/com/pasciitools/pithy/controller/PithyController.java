package com.pasciitools.pithy.controller;

import com.pasciitools.pithy.data.FixedPageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;

import java.io.IOException;

public interface PithyController {

    Logger log = LoggerFactory.getLogger(PithyController.class);
    default ModelMap loadHeaderLinks(FixedPageRepository fixedPageRepository) {
        ModelMap m = new ModelMap();
        try {
            var listOfHeaderLinks = fixedPageRepository.getListOfFixedPages();
            m.put("headerLinks", listOfHeaderLinks);
        } catch (IOException e) {
            log.warn("Could not load links destined for the header. Cause: {}\nSite will continue to load but may appear incorrect.", e.getMessage());
        }
        return m;
    }
}
