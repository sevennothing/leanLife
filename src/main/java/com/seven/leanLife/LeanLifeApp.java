package com.seven.leanLife;

import com.seven.leanLife.controller.LoginViewController;
import com.seven.leanLife.controller.MonitorWin;
import com.seven.leanLife.controller.RegistViewController;
import com.seven.leanLife.controller.SystemViewController;
import com.seven.leanLife.model.User;
import com.seven.leanLife.utils.LangConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

@SpringBootApplication
public class LeanLifeApp extends Application{
    private ConfigurableApplicationContext springContext;
    public LangConfig languageConf;
    private Parent rootNode;
    private FXMLLoader fxmlLoader;
    private Stage stage;
    private Scene scene;
    public MonitorWin sysMw;

    //标识当前用户
    private User user;
    public Properties dbConf;

    public static void main(String[] args) {
        //SpringApplication.run(LeanLifeApp.class, args);
        launch(args);
    }

    @Override
    public void init() throws Exception {
        sysMw = new MonitorWin(); // 不真的打开监控串口
        springContext = SpringApplication.run(LeanLifeApp.class);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(springContext::getBean);
        //System.out.println("加载语言配置");
        languageConf = new LangConfig();
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
    @Override
    public void start(Stage primaryStage) throws Exception{
        stage  = primaryStage;
        showLoginView();
        primaryStage.show();
        /*
        fxmlLoader.setLocation(getClass().getResource("/fxml/LoginView.fxml"));
        rootNode = fxmlLoader.load();
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(rootNode, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        */
    }

    @Override
    public void stop() {
        springContext.stop();
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
            lgController.setMainApp(this);
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

            RegistViewController regController = (RegistViewController)loader.getController();
            System.out.println(regController);
            regController.setMainApp(this);
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
        stage.setResizable(false);
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

    /**
     *	获取scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     *	获取Stage
     */
    public Stage getStage() {
        return stage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /*
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                System.out.println("Hello World");
            }
        };
    }
    */
}