/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Admin
 */
public class ColorPickerViewController {
    private LeanLifeApp mainApp;
    
    @FXML
    protected TextField sysTextField;

    @FXML
    protected TextField rgbTextField;

    @FXML
    protected TextField argbTextField;
    
    @FXML
    protected TextField rgbaTextField;
    
    @FXML
    protected TextField hslTextField;
    
    @FXML
    protected TextField hsvTextField;
    
    @FXML
    protected Button sysCopyButton;
    
    @FXML
    protected Button rgbCopyButton;
    
    @FXML
    protected Button argbCopyButton;
    
    @FXML
    protected Button rgbaCopyButton;
    
    @FXML
    protected Button hslCopyButton;
    
    @FXML
    protected Button hsvCopyButton;
    
    @FXML
    protected ColorPicker colorSelectColorPicker;
    
    @FXML
    private void initialize() {
      
    }
    
    /**
     *	得到主控制器的引用
     */
    public void setMainApp(LeanLifeApp mainApp) {
        this.mainApp = mainApp;
    }
    
}
