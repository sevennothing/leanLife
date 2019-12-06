package com.seven.leanLife;

import com.seven.leanLife.config.ConfigurationService;
import com.seven.leanLife.config.SpellcheckConfigBean;
import com.seven.leanLife.controller.ApplicationController;
import com.seven.leanLife.service.ThreadService;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.*;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketConfiguration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.script.ScriptEngine;
import java.util.Base64;

@Configuration
@EnableWebSocket
// 组件扫描启动太慢
//@ComponentScan(basePackages = "com.seven.leanLife.**")
//@EnableAutoConfiguration
@Import({
        //DelegatingWebSocketConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        ServletWebServerFactoryAutoConfiguration.class,
        //ErrorMvcAutoConfiguration.class,
        //WebMvcAutoConfiguration.class,
        //JacksonAutoConfiguration.class,
        //JmxAutoConfiguration.class,
        //MultipartAutoConfiguration.class,
        //PropertyPlaceholderAutoConfiguration.class,
        //RestTemplateAutoConfiguration.class,
        //SpringApplicationAdminJmxAutoConfiguration.class,
        //EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
        //WebSocketServletAutoConfiguration.class,
        //HttpEncodingAutoConfiguration.class,
        //HttpMessageConvertersAutoConfiguration.class,

        ApplicationController.class,
        ConfigurationService.class,
})
public class SpringAppConfig extends SpringBootServletInitializer implements WebSocketConfigurer {
    @Autowired
    private ApplicationController applicationController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        //TODO: 这里注册websocket
        registry.addHandler(applicationController, "/ws", "/ws**", "/ws/**").withSockJS();
    }


    @Bean
    @Lazy
    public SpellcheckConfigBean spellcheckConfigBean(ApplicationController controller, ThreadService threadService){
        SpellcheckConfigBean spellcheckConfigBean = new SpellcheckConfigBean(controller, threadService);
        return spellcheckConfigBean;
    }

    @Bean
    @Lazy
    public ThreadService threadService(){
        ThreadService threadService = new ThreadService();
        return threadService;
    }

    @Bean
    @Lazy
    public NashornScriptEngineFactory nashornScriptEngineFactory() {
        NashornScriptEngineFactory nashornScriptEngineFactory = new NashornScriptEngineFactory();
        return nashornScriptEngineFactory;
    }

    @Bean
    @Scope("prototype")
    @Lazy
    public ScriptEngine scriptEngine() {
        ScriptEngine scriptEngine = nashornScriptEngineFactory()
                .getScriptEngine();
//                .getScriptEngine(new String[]{"--persistent-code-cache", "--class-cache-size=50"});
        return scriptEngine;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application.sources(SpringAppConfig.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Base64.Encoder base64Encoder() {
        return Base64.getEncoder();
    }

}
