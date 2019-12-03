package com.seven.leanLife.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LangConfig {
    private Properties properties;
    private String lang = "english";

    public LangConfig(){
        properties = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("/config/application.properties");
            properties.load(in);
            String lang = properties.getProperty("lang");
            System.out.println("配置语言：" + lang);
            in.close();

            if(lang.equals("chinese")){
                System.out.println("-中文-");
                in = this.getClass().getResourceAsStream("/config/lang.chinese.properties");
            }else{
                System.out.println("-english-");
                in = this.getClass().getResourceAsStream("/config/lang.en.properties");
            }
            properties.load(in);
            in.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getFeild(String name) {
        if(properties == null){
            System.out.println("无效配置");
            return "";
        }
        String str = properties.getProperty(name,"");
        return str;
    }
}
