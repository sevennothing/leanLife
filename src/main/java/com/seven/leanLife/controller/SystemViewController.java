/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;
import com.seven.leanLife.utils.moreUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
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
    @FXML
    private Label nameLabel;
    
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
    private void initialize() {
        currentMenuLabel  = noteManLabel;
        noteTabPane.visibleProperty().set(false);
        diaryTabPane.visibleProperty().set(false);
        toolsTabPane.visibleProperty().set(false);
        handleNoteManLabelClickedAction();
    }
    /**
     * 响应点击笔记管理
     */
    @FXML
    private void handleNoteManLabelClickedAction(){
        process_pre_menu_view(noteManLabel, noteTabPane);
    }

    /**
     * 响应点击日记管理
     */
    @FXML
    private void handleDiaryManLabelClickedAction(){
        process_pre_menu_view(diaryManLabel, diaryTabPane);
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
		//System.out.println("复制颜色值");	
               String  colorStr = colorPicker.getValue().toString();
               //System.out.println("颜色值:"+ colorStr  );
               
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
        mainApp.showLoginView();
        mainApp.setUser(null);
    }
    /**
     *	得到主控制器的引用
     */
    public void setMainApp(LeanLifeApp mainApp) {
        this.mainApp = mainApp;
        Stage stage = mainApp.getStage();
        //stage.setMaximized(true);
        //stage.setFullScreen(true);
        //nameLabel.setText(mainApp.getUser().getUserName());
    }
    
}
