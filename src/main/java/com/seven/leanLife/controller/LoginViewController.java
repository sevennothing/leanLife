/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;
import com.seven.leanLife.model.User;
import com.seven.leanLife.utils.CheckValidTool;
import com.seven.leanLife.utils.JDBCTool;
import com.seven.leanLife.utils.DialogTool;
import com.seven.leanLife.config.DebugConfig;

import java.net.URL;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;

/**
 * FXML Controller class
 *
 * @author Admin
 */
@Controller
public class LoginViewController{
    private ApplicationController parentController;
    @FXML
    private Label systemName;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button registButton;
    @FXML
    private ImageView leftImageView;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label errorInfoLabel;

    /**
     * 获取主控制器的引用
     */
    public void setMainController(ApplicationController controller) {
        this.parentController = controller;
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        //System.out.println("Login View initialize");
        URL url = getClass().getClassLoader().getResource("img/guide/user.png");
        userNameLabel.setGraphic(new ImageView(new Image(url.toExternalForm())));
        url = getClass().getClassLoader().getResource("img/guide/password.png");
        passwordLabel.setGraphic(new ImageView(new Image(url.toExternalForm())));
        url = getClass().getClassLoader().getResource("img/leftimage.bmp");
        leftImageView.setImage(new Image(url.toExternalForm()));

        leftImageView.setPreserveRatio(true);
        leftImageView.setSmooth(true);
    }

    /**
     *  刷新显示语言
     */
    public void langFlush(){
        /* 语言调整 */
        String value;
        value = parentController.languageConf.getFeild("systemName");
        systemName.setText(value);
        value = parentController.languageConf.getFeild("login");
        loginButton.setText(value);
        value = parentController.languageConf.getFeild("regist");
        registButton.setText(value);

        value = parentController.languageConf.getFeild("username.prompt");
        userNameField.setPromptText(value);
        value = parentController.languageConf.getFeild("password.prompt");
        passwordField.setPromptText(value);
    }

    /**
     * 处理注册按钮事件
     */
    @FXML
    private void handleRegistButtonAction() {
        parentController.showRegistView();
    }

    /**
     * 处理登录按钮事件
     */
    @FXML
    private void handleLoginButtonAction() {
        String userName = userNameField.getText();
        String password = passwordField.getText();

        if(DebugConfig.IGNORE_LOGIN_AUTH){
            /* Debug Only*/
            parentController.showSystemView();
            return;
        }
        if(CheckValidTool.isValidUserName(userName) && CheckValidTool.isValidPassword(password)) {
            String sql = "select * from user where name=? and password=?";
            User user= JDBCTool.getUser(parentController.dbConf, sql, userName, password);
            if(user != null) {
                //设置当前用户
                parentController.setUser(user);
                DialogTool.informationDialog("登录成功", "即将进入主界面");
                //显示主界面
                parentController.showSystemView();
            }else {
                errorInfoLabel.setText("用户名或密码不正确");
                userNameField.clear();
                passwordField.clear();
                //登录失败做出渐变渐显的效果
                FadeTransition ft = new FadeTransition();
                ft.setDuration(Duration.seconds(0.1));
                ft.setNode(parentController.getScene().getRoot());
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            }
        }else {
            errorInfoLabel.setText("用户名或密码不合法");
            userNameField.clear();
            passwordField.clear();
            FadeTransition ft = new FadeTransition();
            ft.setDuration(Duration.seconds(0.1));
            ft.setNode(parentController.getScene().getRoot());
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

}
