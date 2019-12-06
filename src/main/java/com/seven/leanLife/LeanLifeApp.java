package com.seven.leanLife;

import com.seven.leanLife.config.ConfigurationService;
import com.seven.leanLife.controller.ApplicationController;
import com.seven.leanLife.service.ThreadService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;


/**
 *  尝试网上解决spring boot 启动缓慢的问题，
 *  发现使用@Configuration 和 @EnableAutoConfiguration
 *  代替@SpringBootApplication;
 *  -- 无效
 */
//@Configuration
//@EnableAutoConfiguration
@SpringBootApplication
public class LeanLifeApp extends Application{
    private static Logger logger = LoggerFactory.getLogger(LeanLifeApp.class);
    private static ApplicationController controller;
    private static ConfigurableApplicationContext context;
    private Stage stage;

    private ConfigurationService configurationService;
    private ThreadService threadService;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage(), e));
        loadRequiredFonts();

        // 启动线程
        new Thread(() -> {
            try {
                startApp(primaryStage);
            } catch (final Throwable e) {
                logger.error("Problem occured while starting LeanLife System", e);
            }
        }).start();

    }

    private void loadRequiredFonts() {
        //Note: 在这里加载字体
        /*
        Font.loadFont(LeanLifeApp.class.getResourceAsStream("/font/NotoSerif-Regular.ttf"), -1);
        Font.loadFont(LeanLifeApp.class.getResourceAsStream("/font/NotoSerif-Italic.ttf"), -1);
        Font.loadFont(LeanLifeApp.class.getResourceAsStream("/font/NotoSerif-Bold.ttf"), -1);
        Font.loadFont(LeanLifeApp.class.getResourceAsStream("/font/NotoSerif-BoldItalic.ttf"), -1);
        Font.loadFont(LeanLifeApp.class.getResourceAsStream("/font/DejaVuSansMono.ttf"), -1);
        Font.loadFont(LeanLifeApp.class.getResourceAsStream("/font/DejaVuSansMono-Bold.ttf"), -1);
        Font.loadFont(LeanLifeApp.class.getResourceAsStream("/font/DejaVuSansMono-Oblique.ttf"), -1);
        */
    }

    private void startApp(final Stage primaryStage) throws Throwable {
        this.stage  = primaryStage;
        context = SpringApplication.run(SpringAppConfig.class);
        System.out.println("start Service");

        controller = context.getBean(ApplicationController.class);
        threadService = context.getBean(ThreadService.class);

        // 加载配置服务
        configurationService = context.getBean(ConfigurationService.class);
        configurationService.loadConfigurations();


        final FXMLLoader parentLoader = new FXMLLoader();
        parentLoader.setControllerFactory(context::getBean);

        controller.initializeApp();

        // 提前设置，防止闪屏
        controller.setStage(stage);

        stage.setOnShowing(e -> {
            configurationService.loadConfigurations();
        });
        stage.setOnShown(e -> {
            controller.bindConfigurations();
        });

        threadService.runActionLater(() -> {
            //setMaximized();
            controller.showLoginView();
            stage.show();
        });

        final ThreadService threadService = context.getBean(ThreadService.class);
        threadService.start(() -> {
            try {
                logger.info("thread Service start");
            } catch (Exception e) {
                logger.error("Problem occured in startup listener", e);
            }
        });
        /*
        scene.getWindow().setOnCloseRequest(controller::closeAllTabs);
        stage.widthProperty().addListener(controller::stageWidthChanged);
        stage.heightProperty().addListener(controller::stageWidthChanged);
        */

    }

    /*
    private void setMaximized() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }
    */




    /**
     *	获取Stage
     */
    public Stage getStage() {
        return stage;
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

    @Override
    public void stop() {
        //springContext.stop();

        context.registerShutdownHook();
        Platform.exit();
        System.exit(0);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}