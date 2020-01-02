/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.controller;

import com.seven.leanLife.component.*;
import com.seven.leanLife.component.ToolBar;
import com.seven.leanLife.config.ConfigurationService;
import com.seven.leanLife.config.EditorConfigBean;
import com.seven.leanLife.config.SpellcheckConfigBean;
import com.seven.leanLife.model.Monitor;
import com.seven.leanLife.service.ThreadService;
import com.seven.leanLife.service.extension.AsciiTreeGenerator;
import com.seven.leanLife.service.shortcut.ShortcutProvider;
import com.seven.leanLife.service.ui.EditorService;
import com.seven.leanLife.service.ui.EditorTabService;
import com.seven.leanLife.utils.Current;
import com.seven.leanLife.utils.EventProcess;

import java.io.IOException;
import java.net.URL;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
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
    private FlowPane toolsMainTabFlowPane;
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
    private ImageView pictureToolImg;

    /** 时间戳工具只能同时存在一个被打开的视图 */
    TimeToolViewController timeToolViewController;
    BasicTab timeToolTab;

    /** 录音机工具只能同时存在一个被打开的视图 */
    RecorderViewController recorderViewController;
    BasicTab recorderTab;

    PicturesViewController picturesViewController;

    @FXML
    private RecorderViewController recorderController;
    private Boolean hideMainMenu;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private EditorService editorService;
    @Autowired
    private EditorTabService editorTabService;
    @Autowired
    private AsciiTreeGenerator asciiTreeGenerator;
    @Autowired
    private SpellcheckConfigBean spellcheckConfigBean;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private EditorConfigBean editorConfigBean;
    @Autowired
    private ShortcutProvider shortcutProvider;

    @Autowired
    private Current current;


    @Autowired
    public SystemViewController(ApplicationController controller,
                                Current current,
                                ThreadService threadService,
                                EditorService editorService,
                                EditorTabService editorTabService,
                                SpellcheckConfigBean spellcheckConfigBean,
                                EditorConfigBean editorConfigBean,
                                ShortcutProvider shortcutProvider,
                                AsciiTreeGenerator asciiTreeGenerator
                                ){
        this.parentController = controller;
        this.threadService = threadService;
        this.editorService = editorService;
        this.editorTabService = editorTabService;
        this.spellcheckConfigBean = spellcheckConfigBean;
        this.asciiTreeGenerator = asciiTreeGenerator;
        this.editorConfigBean = editorConfigBean;
        this.shortcutProvider = shortcutProvider;
        this.current = current;

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
        toolsTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if( newValue instanceof BasicTab) {
                    current.setChoiceTab((BasicTab) newValue);
                }else{
                    current.setChoiceTab(null);
                }
                paintToolbar();
            }
        });
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
        paintToolbar();
    }

    /**
     * 响应点击日记管理
     */
    @FXML
    private void handleDiaryManLabelClickedAction(){
        process_pre_menu_view(diaryManLabel, diaryTabPane);
        paintToolbar();
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

    private void freshToolsMainTab(){
        double minSize = 40.01;

        final ImageView colorPicker = ImageViewBuilt.image(
                getClass().getClassLoader().getResource("img/tools/paint_palette.png"),
                minSize
        ).clazz("tools-cell").tip("取色器").click(this::handleColorToolClickedAction).build();

        final ImageView clockTool = ImageViewBuilt.image(
                getClass().getClassLoader().getResource("img/tools/clock.png"),
                minSize
        ).clazz("tools-cell").tip("时间工具").click(this::handleClockToolClickedAction).build();

        final ImageView recorderTool = ImageViewBuilt.image(
                getClass().getClassLoader().getResource("img/tools/recorder.png"),
                minSize
        ).clazz("tools-cell").tip("录音机").click(this::handleRecorderToolClickedAction).build();

        final ImageView editorTool = ImageViewBuilt.image(
                getClass().getClassLoader().getResource("img/tools/editor.png"),
                minSize
        ).clazz("tools-cell").tip("编辑器").click(this::handleEditorToolClickedAction).build();


        final ImageView pictureMan = ImageViewBuilt.image(
                getClass().getClassLoader().getResource("img/tools/pictures.png"),
                minSize
        ).clazz("tools-cell").tip("图库管理工具").click(this::handlePictureToolClickedAction).build();

        toolsMainTabFlowPane.getChildren().clear();
        toolsMainTabFlowPane.getChildren().addAll(colorPicker,clockTool,recorderTool,editorTool,pictureMan);
        toolsMainTabFlowPane.setHgap(Double.valueOf(30));
        toolsMainTabFlowPane.setVgap(Double.valueOf(30));
        toolsMainTabFlowPane.setId("tools-list");

    }

    /**
     * 响应点击工具管理
     */
    @FXML
    private void handleToolsManLabelClickedAction(){
        freshToolsMainTab();
        process_pre_menu_view(toolsManLabel,toolsTabPane);
    }

    private BasicTab openNewTabPane(TabPane tabPane, String fxmlName, Object controller, String tabName){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
        loader.setController(controller);
        AnchorPane ap = null;
        try {
            ap = (AnchorPane)loader.load();
        }catch(IOException e) {
            e.printStackTrace();
        }

        BasicTab tab = new BasicTab(parentController);
        tab.setTabText(tabName);
        tab.setContent(ap);
        tabPane.getTabs().add(tab);
        tab.select();
        tab.enableMenu();
        return tab;
    }

    @FXML
    /* 取色器功能 */
    private void handleColorToolClickedAction(Event... events){
        ColorPickerViewController cpvController = new ColorPickerViewController(parentController);
        openNewTabPane(toolsTabPane, "/fxml/ColorPickerView.fxml",cpvController,"取色工具");
    }

    @FXML
    /* 时间戳工具功能 */
    private void handleClockToolClickedAction(Event... events){
        if(timeToolViewController == null) {
            timeToolViewController = new TimeToolViewController(parentController);
        }

        if(timeToolTab == null) {
            timeToolTab = openNewTabPane(toolsTabPane, "/fxml/timeToolView.fxml", timeToolViewController, "时间工具");
            timeToolTab.setOnClosed(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    timeToolTab = null;
                }
            });

            ToolBar toolbar = new ToolBar();
            double minSize = 14.01;
            final Label flushLabel = LabelBuilt.icon(FontAwesome.REFRESH, minSize)
                    .clazz("timeTools-label")
                    .tip("fresh").click(timeToolViewController::refresh).build();
            toolbar.addItem(flushLabel, "timeTools-label");
            timeToolTab.setToolBar(toolbar);
        }else {
            SingleSelectionModel selectionModel = toolsTabPane.getSelectionModel();
            selectionModel.select(timeToolTab);
        }
        current.setChoiceTab(timeToolTab);
        paintToolbar();
    }


    /***
     *  录音机功能入口
     */
    @FXML
    private void handleRecorderToolClickedAction(Event... events){
        if(recorderViewController == null) {
            recorderViewController = new RecorderViewController(parentController);
        }
        if(recorderTab == null) {
            recorderTab = openNewTabPane(toolsTabPane, "/fxml/RecorderView.fxml",recorderViewController,"录音机");
            recorderTab.setOnClosed(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    recorderTab = null;
                }
            });
        }else {
            SingleSelectionModel selectionModel = toolsTabPane.getSelectionModel();
            selectionModel.select(recorderTab);
        }
        recorderViewController.voiceRecorderStart();
    }

    /**
     * 编辑器工具
     */
    @FXML
    private void handleEditorToolClickedAction(Event... events){
        parentController.sysMw.publishMsg("Start the Editor tool");
        BasicTab tab = openNewTabPane(toolsTabPane, "/fxml/EditorView.fxml",parentController.editorController,"编辑器");
        // 在新建的tab上加载编辑器
        parentController.editorController.loadEditPane(tab);
        current.setChoiceTab(tab);
        paintToolbar();
    }
    /**
     * 图形库管理工具
     */
    @FXML
    private void handlePictureToolClickedAction(Event... events){
        if(picturesViewController == null) {
            picturesViewController = new PicturesViewController(parentController);
        }
        BasicTab tab = openNewTabPane(toolsTabPane, "/fxml/PicturesView.fxml",picturesViewController,"图库管理");
        picturesViewController.setBasicTab(tab);
        picturesViewController.initialized();

        current.setChoiceTab(tab);
        paintToolbar();
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
    private void paintToolbar(){
        Stage stage = parentController.getStage();
        double toolBarWidth = stage.getWidth() - 100;
        double toolBarHeight = 40.0;

        toolbarPane.getChildren().clear();
        toolbarPane.setPrefSize(toolBarWidth,toolBarHeight);

        //TODO: 获取当前的tab
        if(current.getChoiceTab() != null) {
            ToolBar toolBar = current.getChoiceTab().getToolBar();
            if(toolBar != null){
                toolBar.setPrefSize(toolBarWidth,toolBarHeight);
                toolbarPane.getChildren().add(toolBar);
                AnchorPane.setLeftAnchor(toolBar, Double.valueOf(0));
                AnchorPane.setRightAnchor(toolBar, Double.valueOf(0));
                AnchorPane.setBottomAnchor(toolBar,Double.valueOf(0));
                AnchorPane.setTopAnchor(toolBar,Double.valueOf(0));
            }
        }
    }

    @FXML
    private void handleSave(){
        System.out.println("响应保存");
    }


}
