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
import com.seven.leanLife.utils.DebugConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;

/**
 * FXML Controller class
 *
 * @author Admin
 */
@Controller
public class LoginViewController{
    private LeanLifeApp mainApp;
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
    public void setMainApp(LeanLifeApp mainApp) {
        this.mainApp = mainApp;
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
        value = mainApp.languageConf.getFeild("systemName");
        systemName.setText(value);
        value = mainApp.languageConf.getFeild("login");
        loginButton.setText(value);
        value = mainApp.languageConf.getFeild("regist");
        registButton.setText(value);

        value = mainApp.languageConf.getFeild("username.prompt");
        userNameField.setPromptText(value);
        value = mainApp.languageConf.getFeild("password.prompt");
        passwordField.setPromptText(value);
    }

    /**
     * 处理注册按钮事件
     */
    @FXML
    private void handleRegistButtonAction() {
        mainApp.showRegistView();
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
            mainApp.showSystemView();
            return;
        }
        if(CheckValidTool.isValidUserName(userName) && CheckValidTool.isValidPassword(password)) {
            String sql = "select * from user where name=? and password=?";
            User user= JDBCTool.getUser(sql, userName, password);
            if(user != null) {
                //设置当前用户
                mainApp.setUser(user);
                DialogTool.informationDialog("登录成功", "即将进入主界面");
                //显示主界面
                mainApp.showSystemView();
            }else {
                errorInfoLabel.setText("用户名或密码不正确");
                userNameField.clear();
                passwordField.clear();
                //登录失败做出渐变渐显的效果
                FadeTransition ft = new FadeTransition();
                ft.setDuration(Duration.seconds(0.1));
                ft.setNode(mainApp.getScene().getRoot());
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
            ft.setNode(mainApp.getScene().getRoot());
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

}
