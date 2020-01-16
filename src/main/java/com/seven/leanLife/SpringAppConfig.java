package com.seven.leanLife;

import com.seven.leanLife.controller.ApplicationController;
import com.seven.leanLife.controller.EditorController;
import com.zaxxer.hikari.HikariDataSource;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.sql.DataSource;
import java.util.Base64;

@Configuration
@EnableWebSocket
// 组件扫描启动太慢
@ComponentScan(basePackages = "com.seven.leanLife.**")
//@EnableAutoConfiguration
/*
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
        ShortcutProvider.class,
        EditorService.class,
        //EditorConfigBean.class,
})
*/
public class SpringAppConfig extends SpringBootServletInitializer implements WebSocketConfigurer {
    @Autowired
    private ApplicationController applicationController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        //System.out.println("registerWebSocketHandlers");
        // 这里注册websocket
        registry.addHandler(applicationController.editorController, "/ws", "/ws**", "/ws/**").withSockJS();
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
