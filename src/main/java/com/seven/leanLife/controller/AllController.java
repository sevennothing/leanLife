package com.seven.leanLife.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Consumer;
import static org.springframework.web.bind.annotation.RequestMethod.*;
/**
 * Created by usta on 02.09.2015.
 */
@Controller
public class AllController {
    private final CommonResource commonResource;
    private Logger logger = LoggerFactory.getLogger(AllController.class);
    @Autowired
    public AllController(CommonResource commonResource) {
        this.commonResource = commonResource;
    }
    @RequestMapping(value = {"/**/*.*", "*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void all(HttpServletRequest request, HttpServletResponse response) {
        Payload payload = new Payload(request, response);
        commonResource.processPayload(payload);
    }
    class Router {
        private final Payload payload;
        public Router(Payload payload) {
            this.payload = payload;
        }
        public Router executeIf(String pattern, Consumer<Payload> consumer) {
            if (payload.getRequestURI().contains(pattern)) {
                payload.setPattern(pattern);
                try {
                    consumer.accept(payload);
                } catch (Exception e) {
                    logger.debug(e.getMessage(), e);
                }
            }
            return this;
        }
    }
}