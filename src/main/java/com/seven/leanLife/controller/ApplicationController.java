package com.seven.leanLife.controller;


import com.seven.leanLife.LeanLifeApp;
import com.seven.leanLife.component.MonitorWin;
import com.seven.leanLife.config.LangConfigBean;
import com.seven.leanLife.model.User;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
public class ApplicationController extends TextWebSocketHandler implements Initializable {
    private Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    private Path configPath;
    private Path installationPath;
    private String logPath;
    @Value("${application.config.folder}")
    private String configDirName;

    // 全局的监控窗口
    public MonitorWin sysMw;
    public LangConfigBean languageConf;

    private Stage stage;
    private Scene scene;
    private List<WebSocketSession> sessionList = new ArrayList<>();

    private HostServices hostServices;

    //标识当前用户
    private User user;
    public Properties dbConf;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void initializeApp(){
        sysMw = new MonitorWin(); // 不真的打开监控串口
        //System.out.println("加载语言配置");
        languageConf = new LangConfigBean();
        // 加载数据库配置文件
        dbConf = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("/config/db.properties");
            dbConf.load(in);
            in.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private Stage[] getAllStages() {
        return new Stage[]{
                /*
                        stage,
                        detachStage,
                        asciidocTableStage,
                        markdownTableStage
                */
                };
    }

    public void bindConfigurations() {

    }

    public Path getInstallationPath() {
        if (isNull(installationPath)) {
            try {
                String homeProp = System.getProperty("leanlife.home");
                if (homeProp != null) {
                    installationPath = new File(homeProp).toPath();
                } else {
                    //guess installation path
                    CodeSource codeSource = LeanLifeApp.class.getProtectionDomain().getCodeSource();
                    File jarFile = new File(codeSource.getLocation().toURI().getPath());
                    installationPath = jarFile.toPath().getParent().getParent();
                }
            } catch (Exception e) {
                logger.error("Problem occured while resolving conf and log paths", e);
            }
        }
        return installationPath;
    }

    public Path getConfigPath(){
        if (isNull(configPath)) {
            configPath = getInstallationPath().resolve("conf");
        }
        return configPath;
    }

    public String getLogPath() {
        if (isNull(logPath)) {
            Optional<String> linuxHome = Optional.ofNullable(System.getenv("HOME"));
            Optional<String> windowsHome = Optional.ofNullable(System.getenv("USERPROFILE"));
            Stream.<Optional<String>>of(linuxHome, windowsHome)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Paths::get)
                    .findFirst()
                    .ifPresent(path -> logPath = path.resolve(configDirName).resolve("log").toString());
        }
        return logPath;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public Stage getStage() {
        return stage;
    }
    public Scene getScene() {
        return scene;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     *	显示登录界面
     */
    public void showLoginView() {
        try {
            URL url = getClass().getClassLoader().getResource("img/guide/login.png");
            stage.setTitle("Login");
            stage.getIcons().add(new Image(url.toExternalForm()));
            LoginViewController lgController = (LoginViewController)replaceSceneContent("/fxml/LoginView.fxml");

            stage.setResizable(false);

            lgController.setMainController(this);
            lgController.langFlush();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *	显示注册界面
     */
    public void showRegistView() {
        try {
            stage.setTitle("Regist");
            stage.getIcons().add(new Image("file:/img/guide/regist.png"));
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(LeanLifeApp.class.getResource("/fxml/RegistView.fxml"));
            BorderPane bp = (BorderPane)loader.load();

            scene = new Scene(bp);
            stage.setScene(scene);
            stage.setResizable(false);

            RegistViewController regController = loader.getController();
            System.out.println(regController);
            regController.setMainController(this);
            regController.langFlush();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *	显示系统主界面
     *  为了方便处理多语言，不在fxml中指定控制器；
     *  new 控制器时将mainApp传递进去
     */
    public void showSystemView() {
        try {
            stage.setTitle("LeanLifeSystem");
            stage.getIcons().add(new Image("file:/img/home.png"));
            setMaximized();
            //stage.setMaximized(true);
            //stage.setFullScreen(true);

            SystemViewController svController = new SystemViewController(this);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SystemView.fxml"));
            loader.setController(svController);
            replaceSceneContent(loader);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void replaceSceneContent(FXMLLoader loader){
        AnchorPane ap = null;
        try {
            ap = (AnchorPane)loader.load();
        }catch(IOException e) {
            e.printStackTrace();
        }

        scene = new Scene(ap);
        stage.setScene(scene);
        stage.setResizable(true);
    }

    /**
     *	显示指定的视图
     */
    private Object replaceSceneContent(String fxmlFile) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        replaceSceneContent(loader);

        return loader.getController();
    }

    private void setMaximized() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }


}

