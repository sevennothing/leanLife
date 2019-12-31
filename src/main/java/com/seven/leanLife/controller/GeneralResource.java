package com.seven.leanLife.controller;

import com.seven.leanLife.service.DirectoryService;
import com.seven.leanLife.service.ThreadService;
import com.seven.leanLife.utils.Current;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.Objects;
import static org.springframework.web.bind.annotation.RequestMethod.*;
/**
 * Created by usta on 02.09.2015.
 */
@Controller
public class GeneralResource {
    private final FileService fileService;
    private final DirectoryService directoryService;
    private final Current current;
    private final ThreadService threadService;
//    private final TabService tabService;
    private final CommonResource commonResource;
    private Logger logger = LoggerFactory.getLogger(GeneralResource.class);
    @Autowired
    public GeneralResource(FileService fileService, DirectoryService directoryService, Current current, ThreadService threadService,
                           //TabService tabService,
                           CommonResource commonResource) {
        this.fileService = fileService;
        this.directoryService = directoryService;
        this.current = current;
        this.threadService = threadService;
        //this.tabService = tabService;
        this.commonResource = commonResource;
    }
    @RequestMapping(value = {"/lean-life/resource", "/lean-life/resource/**", "/lean-life/resource/*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void onrequest(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value = "p", required = false) String p) {
        Payload payload = new Payload(request, response);
        payload.setPattern("/lean-life/resource/");
        String finalURI = payload.getFinalURI();
        if (Objects.nonNull(p)) {
            Path path = directoryService.findPathInPublic(p);
            fileService.processFile(request, response, path);
        } else if (finalURI.matches(".*\\.(asc|asciidoc|ad|adoc|md|markdown)$")) {
            current.currentPath().ifPresent(path -> {
                Path ascFile = path.getRoot().resolve(finalURI);
                threadService.runActionLater(() -> {
                    //TODO: addTab
                    System.out.println("TODO: add Tab");
                    //tabService.addTab(ascFile);
                });
            });
            payload.setStatus(HttpStatus.NO_CONTENT);
        } else {
            commonResource.processPayload(payload);
        }
    }
}