package com.pasciitools.pithy.controller;

import com.pasciitools.pithy.data.AppConfigRepo;
import com.pasciitools.pithy.data.FixedPageRepository;
import com.pasciitools.pithy.data.PostRepository;
import com.pasciitools.pithy.exception.PostNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Controller
@AllArgsConstructor
public class PageController implements Serializable, ErrorController, PithyController {

    private static final Logger log = LoggerFactory.getLogger(PageController.class);
    @Serial
    private static final long serialVersionUID = -6763999116784172273L;

    private final PostRepository postRepository;
    private final FixedPageRepository fixedPageRepository;

    private final AppConfigRepo appConfigRepo;
    private static final String ERROR = "error";


    @SuppressWarnings("SameReturnValue")
    @GetMapping("/")
    public String getHome (ModelMap model) {
        var blogTitleAppConfig = appConfigRepo.findByConfigKey("blog.title");
        var blogDescriptionAppConfig = appConfigRepo.findByConfigKey("blog.description");
        try {
            var listOfFiles = postRepository.getListOfFiles();
            model.put("blogTitle", blogTitleAppConfig.isPresent() ? blogTitleAppConfig.get().getConfigValue() : "Title TBD");
            model.put("blogDesc", blogDescriptionAppConfig.isPresent() ? blogDescriptionAppConfig.get().getConfigValue() : "Description TBD");
            model.put("fileList", listOfFiles);
            model.putAll(loadHeaderLinks(fixedPageRepository));
        } catch (IOException e) {
            log.error("Could not load '/' due to {}", e.getMessage());
            return ERROR;
        }

        return "index";
    }

    @GetMapping(value = "/post/{postName:^(?!favicon\\.ico$).*$}")
    public String getPost (@PathVariable String postName, ModelMap model) {

        try {
            var p = postRepository.getPost(postName);
            if (p == null) {
                throw new PostNotFoundException("Post Not Found");
            }
            model.put("blogPost", p);
            model.putAll(loadHeaderLinks(fixedPageRepository));
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
            if (page == null) {
                throw new PostNotFoundException("Fixed Page Not Found");
            }
            model.putAll(loadHeaderLinks(fixedPageRepository));
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
            request.getAttributeNames();
            model.put("errorException", request.getAttribute(RequestDispatcher.ERROR_EXCEPTION));
            logError(request);
        }
        return ERROR;
    }

    private void logError (HttpServletRequest request) {
        var headerNames = request.getHeaderNames();
        List<String> headerNamesList = new ArrayList<>();
        var headerNamesIter = headerNames.asIterator();
        while (headerNamesIter.hasNext()) {
            headerNamesList.add(headerNamesIter.next());
        }
        log.error("Error loading page: URL={}, Error Code={}, Client IP={}, Method={}, Context Path={}, Header Names={}, User Agent={}",
                request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI),
                request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE),
                request.getRemoteAddr(),
                request.getMethod(),
                request.getContextPath(),
                headerNamesList,
                request.getHeader("user-agent")
        );

    }
}
