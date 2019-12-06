/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;
import com.seven.leanLife.component.MonitorWin;
import com.seven.leanLife.component.ToolBar;
import com.seven.leanLife.component.ToolBarItem;
import com.seven.leanLife.config.ConfigurationService;
import com.seven.leanLife.config.SpellcheckConfigBean;
import com.seven.leanLife.model.Monitor;
import com.seven.leanLife.service.ThreadService;
import com.seven.leanLife.service.extension.AsciiTreeGenerator;
import com.seven.leanLife.utils.EventProcess;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import javafx.stage.Stage;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FXML Controller class
 *
 * @author Admin
 */
@Controller
public class SystemViewController {
    private ApplicationController parentController;
    private Label currentMenuLabel;
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
    private ImageView recorderToolImg;
    @FXML
    private ImageView editorToolImg;
    @FXML
    private Tab clockTab;
    @FXML
    private TimeToolViewController timeToolController;
    @FXML
    private RecorderViewController recorderController;
    private Boolean hideMainMenu;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private AsciiTreeGenerator asciiTreeGenerator;
    @Autowired
    private SpellcheckConfigBean spellcheckConfigBean;
    @Autowired
    private ConfigurationService configurationService;


    public SystemViewController(ApplicationController controller,
                                ThreadService threadService,
                                SpellcheckConfigBean spellcheckConfigBean,
                                AsciiTreeGenerator asciiTreeGenerator
                                ){
        this.parentController = controller;
        this.threadService = threadService;
        this.spellcheckConfigBean = spellcheckConfigBean;
        this.asciiTreeGenerator = asciiTreeGenerator;

        //Stage stage = parentController.getStage();
        //stage.setMaximized(true);
        //stage.setFullScreen(true);
        //nameLabel.setText(parentController.getUser().getUserName());
    }


    @FXML
    private void initialize() {
        parentController.sysMw = new MonitorWin();
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
            value = parentController.languageConf.getFeild("hideMainMenuPane");
        }else{
            hideMainMenuPane(true);
            value = parentController.languageConf.getFeild("showMainMenuPane");
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
     *  为ImageView节点添加图片
      * @param node
     * @param imgPath
     */
    private void setImageForImageView(ImageView node, String imgPath){
        URL url = getClass().getClassLoader().getResource(imgPath);
        if(url != null) {
            node.setImage(new Image(url.toExternalForm()));
        }
    }

    /**
     * 响应点击工具管理
     */
    @FXML
    private void handleToolsManLabelClickedAction(){
        setImageForImageView(colorToolImg, "img/tools/paint_palette.png");
        setImageForImageView(clockToolImg, "img/tools/clock.png");
        setImageForImageView(recorderToolImg, "img/tools/recorder.png");
        setImageForImageView(editorToolImg, "img/tools/editor.png");
        process_pre_menu_view(toolsManLabel,toolsTabPane);
    }

    private void openNewTabPane(TabPane tabPane, String fxmlName, Object controller, String tabName){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
        loader.setController(controller);
        AnchorPane ap = null;
        try {
            ap = (AnchorPane)loader.load();
        }catch(IOException e) {
            e.printStackTrace();
        }

        Tab newTab = new Tab();
        newTab.setText(tabName);
        newTab.setClosable(true);
        newTab.setContent(ap);

        tabPane.getTabs().add(newTab);

        SingleSelectionModel selectionModel = tabPane.getSelectionModel();
        selectionModel.select(newTab);

    }

    @FXML
    /* 取色器功能 */
    private void handleColorToolClickedAction(){
        parentController.sysMw.publishMsg("Start the color tool");
        ColorPickerViewController cpvController = new ColorPickerViewController(parentController);
        openNewTabPane(toolsTabPane, "/fxml/ColorPickerView.fxml",cpvController,"取色工具");
    }

    @FXML
    /* 时间戳工具功能 */
    private void handleClockToolClickedAction(){
        timeToolController.setApplicationController(parentController);
        SingleSelectionModel selectionModel = toolsTabPane.getSelectionModel();
        selectionModel.select(clockTab);
        paintTimetoolToolbar();
    }

    /***
     *  录音机功能入口
     */
    @FXML
    private void handleRecorderToolClickedAction(){
        parentController.sysMw.publishMsg("Start the Recorder tool");
        RecorderViewController controller = new RecorderViewController(parentController);
        openNewTabPane(toolsTabPane, "/fxml/RecorderView.fxml",controller,"录音机");
        controller.voiceRecorderStart();
    }

    /**
     * 编辑器工具
     */
    @FXML
    private void handleEditorToolClickedAction(){
        parentController.sysMw.publishMsg("Start the Editor tool");
        EditorViewController controller = new EditorViewController(parentController,threadService,asciiTreeGenerator,
                spellcheckConfigBean);

        openNewTabPane(toolsTabPane, "/fxml/EditorView.fxml",controller,"编辑器");

        controller.load();
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
        parentController.showLoginView();
        parentController.setUser(null);
    }

    @FXML
    private void handleOpenMonitor(){
        if(parentController.sysMw.isNone()){
            // 打开
            String value = parentController.languageConf.getFeild("monitor.terminal");
            parentController.sysMw.openWindow(value);
            //sysMw.publishMsg("您打开了一个监控窗口");
            //sysMw.publishMsg("祝您使用愉快");
        }
        //monitor.msgProperty().addListener(((observable, oldValue, newValue) -> sysMw.publishMsg(newValue)));
        //sysMw.isClosedProperty().addListener((observable, oldValue, newValue) -> monitorWindowClosed());
        //timeToolController.setMw(sysMw);
    }

    /*
    private void monitorWindowClosed(){
        System.out.println("监控串口被关闭");
        parentController.sysMw = null;
    }
    */

    @FXML
    private void handleFullScreen(){
        Stage stage = parentController.getStage();
        stage.setFullScreen(true);
    }


    /**
     * 工具栏
     * tool-bar handle
     */
    @FXML
    private AnchorPane toolbarPane;
    private void paintDefaultToolbar(){
        toolbarPane.getChildren().clear();

        com.seven.leanLife.component.ToolBar toolbar = new com.seven.leanLife.component.ToolBar();

        ToolBarItem saveI = new ToolBarItem();

        String value = parentController.languageConf.getFeild("save");
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
        value = parentController.languageConf.getFeild("cancel");
        cancelI.setText(value);
        toolbar.addItem(cancelI);

        toolbarPane.getChildren().addAll(toolbar);
    }

    private void paintTimetoolToolbar(){
        String value;
        toolbarPane.getChildren().clear();
        com.seven.leanLife.component.ToolBar toolbar = new ToolBar();
        ToolBarItem flushI = new ToolBarItem();
        value = parentController.languageConf.getFeild("flush");
        flushI.setText(value);
        ToolBarItem consoleWinI = new ToolBarItem();
        value = parentController.languageConf.getFeild("monitor.terminal");
        consoleWinI.setText(value);
        toolbar.addItem(flushI);
        toolbar.addItem(consoleWinI);

        toolbarPane.getChildren().add(toolbar);
    }

    @FXML
    private void handleSave(){
        System.out.println("响应保存");
    }


}
