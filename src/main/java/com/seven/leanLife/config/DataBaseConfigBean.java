package com.seven.leanLife.config;

import com.seven.leanLife.engine.MyDataBase;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

@Component
@PropertySource("classpath:config/db.properties")
@ConfigurationProperties(prefix = "local.jdbc")
@Validated
@Data
public class DataBaseConfigBean {
    private MyDataBase myDataBase;

    /* 从配置文件中获取数据库连接信息 */
    private String driverClass;
    private String connectionURL;
    private String userId;
    private String password;

    public DataBaseConfigBean(){
    }

    @PostConstruct
    private void init(){
        myDataBase = new MyDataBase(driverClass, connectionURL, userId, password);
    }


    protected boolean canEqual(final Object other) {
        return other instanceof DataBaseConfigBean;
    }

}
