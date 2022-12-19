package com.pasciitools.pithy.controller;

import com.pasciitools.pithy.config.BlogConfiguration;
import com.pasciitools.pithy.data.FixedPageRepository;
import com.pasciitools.pithy.data.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

@Controller
public class PageController implements Serializable, ErrorController {

    private static final Logger log = LoggerFactory.getLogger(PageController.class);
    @Serial
    private static final long serialVersionUID = -6763999116784172273L;

    private final PostRepository postRepository;
    private final FixedPageRepository fixedPageRepository;
    private final BlogConfiguration blogConfiguration;
    private static final String ERROR = "error";


    @SuppressWarnings("SameReturnValue")
    @GetMapping("/")
    public String getHome (ModelMap model) {

        try {
            var listOfFiles = postRepository.getListOfFiles();
            model.put("blogTitle", blogConfiguration.getTitle());
            model.put("blogDesc", blogConfiguration.getDescription());
            model.put("fileList", listOfFiles);
            model.putAll(loadHeaderLinks());
        } catch (IOException e) {
            log.error("Could not load '/' due to {}", e.getMessage());
            return ERROR;
        }

        return "index";
    }

    @GetMapping(value = "/{postName:^(?!favicon\\.ico$).*$}")
    public String getPost (@PathVariable String postName, ModelMap model) {

        try {
            var p = postRepository.getPost(postName);
            model.put("blogPost", p);
            model.putAll(loadHeaderLinks());
        } catch (IOException e) {
            log.error("Could not load '/{} due to {}", postName, e.getMessage());
            return ERROR;
        }
        return "blogpost";
    }

    @GetMapping("/fixed/{fixedPage}")
    public String getFixedPage (@PathVariable String fixedPage, ModelMap model) {
        try {
            var page = fixedPageRepository.getFixedPage(fixedPage);
            model.putAll(loadHeaderLinks());
            model.put("fixedPage", page);
        } catch (IOException e) {
            log.error("Could not load '/fixed/{} due to {}", fixedPage, e.getMessage());
            return ERROR;
        }

        return "fixed_page";
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping("/error")
    public String getError (HttpServletRequest request, ModelMap model){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.put("errorCode", statusCode);
            model.put("errorException", request.getAttribute(RequestDispatcher.ERROR_EXCEPTION));
        }
        return ERROR;
    }

    private ModelMap loadHeaderLinks () {
        ModelMap m = new ModelMap();
        try {
            var listOfHeaderLinks = fixedPageRepository.getListOfFixedPages();
            m.put("headerLinks", listOfHeaderLinks);
        } catch (IOException e) {
            log.warn("Could not load links destined for the header. Cause: {}\nSite will continue to load but may appear incorrect.", e.getMessage());
        }
        return m;
    }

    public PageController (PostRepository postRepository, FixedPageRepository fixedPageRepository, BlogConfiguration blogConfiguration) {
        this.postRepository = postRepository;
        this.fixedPageRepository = fixedPageRepository;
        this.blogConfiguration = blogConfiguration;
    }
}
