/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;
import com.seven.leanLife.model.Monitor;
import com.seven.leanLife.utils.EventProcess;
import com.seven.leanLife.utils.moreUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

/**
 * FXML Controller class
 *
 * @author Admin
 */
@Controller
public class SystemViewController {
    private LeanLifeApp mainApp;
    private Label currentMenuLabel;
    private MonitorWin sysMw = null;
    public static Monitor monitor = new Monitor();
    @FXML
    private Label nameLabel;

    /* 主布局 */
    @FXML private GridPane mainGridPane;
    /* 左侧主菜单面板 */
    @FXML private AnchorPane mainMenuPane;
    /* 中间主视图面板 */
    @FXML private AnchorPane mainViewPane;

    /* 笔记功能 */
    @FXML
    private Label noteManLabel;
    @FXML
    private TabPane noteTabPane;

    /* 日记功能 */
    @FXML
    private Label diaryManLabel;
    @FXML
    private TabPane diaryTabPane;

    /* 便捷工具 */
    @FXML
    private Label toolsManLabel;
    @FXML
    private TabPane toolsTabPane;
    @FXML
    private ImageView colorToolImg;
    @FXML
    private ImageView clockToolImg;
    @FXML
    private Tab clockTab;
    @FXML
    private TimeToolViewController timeToolController;

    public SystemViewController(LeanLifeApp mainApp){
        setMainApp(mainApp);
    }

    private Boolean hideMainMenu;

    @FXML
    private void initialize() {
        hideMainMenu = false;
        currentMenuLabel  = noteManLabel;
        noteTabPane.visibleProperty().set(false);
        diaryTabPane.visibleProperty().set(false);
        toolsTabPane.visibleProperty().set(false);
        handleNoteManLabelClickedAction();


    }

    @FXML private MenuItem hideMainMenuItem;
    @FXML private void handleHideMainMenu(){
        String value;
        if(hideMainMenu){
            // 重新开启
            hideMainMenuPane(false);
            value = mainApp.languageConf.getFeild("hideMainMenuPane");
        }else{
            hideMainMenuPane(true);
            value = mainApp.languageConf.getFeild("showMainMenuPane");
        }
        hideMainMenuItem.setText(value);
    }

    private void hideMainMenuPane(Boolean value){
        hideMainMenu = value;
        if(value){
            mainMenuPane.setVisible(false);
            mainMenuPane.setManaged(false);
            mainGridPane.getChildren().remove(mainViewPane);
            mainGridPane.add(mainViewPane,0,0,2,1);
        }else{
            mainMenuPane.setVisible(true);
            mainMenuPane.setManaged(true);
            mainGridPane.getChildren().remove(mainViewPane);
            mainGridPane.add(mainViewPane,1,0,1,1);
        }
    }
    /**
     * 响应点击笔记管理
     */
    @FXML
    private void handleNoteManLabelClickedAction(){
        process_pre_menu_view(noteManLabel, noteTabPane);
        paintDefaultToolbar();
    }

    /**
     * 响应点击日记管理
     */
    @FXML
    private void handleDiaryManLabelClickedAction(){
        process_pre_menu_view(diaryManLabel, diaryTabPane);
        paintDefaultToolbar();
    }

    /**
     * 响应点击工具管理
     */
    @FXML
    private void handleToolsManLabelClickedAction(){
        URL url = getClass().getClassLoader().getResource("img/tools/colors.jpg");
        process_pre_menu_view(toolsManLabel, toolsTabPane);
        colorToolImg.setImage(new Image(url.toExternalForm()));
        url = getClass().getClassLoader().getResource("img/tools/clock.jpg");
        clockToolImg.setImage(new Image(url.toExternalForm()));
    }

    @FXML
    /* 取色器功能 */
    private void handleColorToolClickedAction(){
        System.out.println("准备启动颜色工具");

        Tab colorTab = new Tab();
        colorTab.setText("取色器");
        colorTab.setClosable(true);
        toolsTabPane.getTabs().add(colorTab);

        Label colorLabel = new Label();
        colorLabel.setText("取色器");

        ColorPicker colorPicker  = new ColorPicker();
        Button cpyBt = new Button();
        cpyBt.setText("复制");


        cpyBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                monitor.setMsg("复制颜色值");
                //System.out.println("复制颜色值");
                String  colorStr = colorPicker.getValue().toString();
                //System.out.println("颜色值:"+ colorStr  );
                //monitor.setMsg("颜色值:"+ colorStr  );
                sysMw.publishMsg("颜色值:"+ colorStr  );

                Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(colorStr);
                clipboard.setContent(content);

            }

        });

        HBox hbox = new HBox();
        //hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(30);
        hbox.getChildren().add(colorLabel);
        hbox.getChildren().add(colorPicker);
        hbox.getChildren().add(cpyBt);

        VBox vbox = new VBox();
        //vbox.setAlignment(Pos.CENTER);

        vbox.getChildren().add(hbox);
        colorTab.setContent(vbox);

        SingleSelectionModel selectionModel = toolsTabPane.getSelectionModel();
        selectionModel.select(colorTab);
    }

    @FXML
    /* 时间戳工具功能 */
    private void handleClockToolClickedAction(){
        SingleSelectionModel selectionModel = toolsTabPane.getSelectionModel();
        selectionModel.select(clockTab);
        paintTimetoolToolbar();
    }

    private void process_pre_menu_view(Label menuLabel, TabPane menuTab){
        currentMenuLabel.setStyle("-fx-background-color:cornsilk ;");
        if(currentMenuLabel == noteManLabel){
            noteTabPane.visibleProperty().set(false);
        }else if(currentMenuLabel == diaryManLabel){
            diaryTabPane.visibleProperty().set(false);
        }else if(currentMenuLabel == toolsManLabel){
            toolsTabPane.visibleProperty().set(false);
        }
        menuTab.visibleProperty().set(true);
        menuLabel.setStyle("-fx-background-color:#99CCFF ;");
        currentMenuLabel = menuLabel;
    }

    private void handleSaveAction(){
        System.out.println("保存响应");
    }
    /**
     *	退出登录
     */
    @FXML
    private void handleLogoutLabelClickedAction() {
        mainApp.showLoginView();
        mainApp.setUser(null);
    }

    @FXML
    private void handleOpenMonitor(){
        if(sysMw != null){
            return;
        }
        sysMw = new MonitorWin();
        //sysMw.publishMsg("您打开了一个监控窗口");
        //sysMw.publishMsg("祝您使用愉快");
        monitor.msgProperty().addListener(((observable, oldValue, newValue) -> sysMw.publishMsg(newValue)));
        sysMw.isClosedProperty().addListener((observable, oldValue, newValue) -> monitorWindowClosed());
        timeToolController.setMw(sysMw);
    }
    private void monitorWindowClosed(){
        System.out.println("监控串口被关闭");
        sysMw = null;
    }

    @FXML
    private void handleFullScreen(){
        Stage stage = mainApp.getStage();
        stage.setFullScreen(true);
    }


    /**
     * tool-bar handle
     */
    /* 工具栏 */
    @FXML
    private AnchorPane toolbarPane;
    private void paintDefaultToolbar(){
        toolbarPane.getChildren().clear();

        ToolBar toolbar = new ToolBar();

        ToolBarItem saveI = new ToolBarItem();

        String value = mainApp.languageConf.getFeild("save");
        saveI.setText(value);

        URL url = getClass().getClassLoader().getResource("img/tools/save.png");
        saveI.setImage(new Image(url.toExternalForm()));

        saveI.setOnAction((ActionEvent e)->{
            System.out.println("clicked");
            handleSaveAction();
        });
        saveI.bindProcess(new EventProcess(){
            @Override
            public int process(){
                handleSaveAction();
                return 0;
            }
        });

        toolbar.addItem(saveI);

        ToolBarItem cancelI = new ToolBarItem();
        url = getClass().getClassLoader().getResource("img/tools/cancel.png");
        cancelI.setImage(new Image(url.toExternalForm()));
        value = mainApp.languageConf.getFeild("cancel");
        cancelI.setText(value);
        toolbar.addItem(cancelI);

        toolbarPane.getChildren().addAll(toolbar);
    }

    private void paintTimetoolToolbar(){
        String value;
        toolbarPane.getChildren().clear();
        ToolBar toolbar = new ToolBar();
        ToolBarItem flushI = new ToolBarItem();
        value = mainApp.languageConf.getFeild("flush");
        flushI.setText(value);
        ToolBarItem consoleWinI = new ToolBarItem();
        value = mainApp.languageConf.getFeild("monitor.terminal");
        consoleWinI.setText(value);
        toolbar.addItem(flushI);
        toolbar.addItem(consoleWinI);

        toolbarPane.getChildren().add(toolbar);
    }

    @FXML
    private void handleSave(){
        System.out.println("响应保存");
    }
    /**
     *	得到主控制器的引用
     */
    public void setMainApp(LeanLifeApp mainApp) {
        this.mainApp = mainApp;
        //Stage stage = mainApp.getStage();
        //stage.setMaximized(true);
        //stage.setFullScreen(true);
        //nameLabel.setText(mainApp.getUser().getUserName());
    }

    /**
     * 得到监控终端的引用
     * @return 监控窗口节点
     */
    public MonitorWin getSysMw(){
        return sysMw;
    }

}
