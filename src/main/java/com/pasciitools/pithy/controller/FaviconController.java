package com.pasciitools.pithy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {

    private final Logger log = LoggerFactory.getLogger(FaviconController.class);

    @GetMapping("favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
        log.info("Tried to look for the favicon");
    }
}
