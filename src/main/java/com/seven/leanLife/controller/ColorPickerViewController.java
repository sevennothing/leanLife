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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Admin
 */
public class ColorPickerViewController {
    private LeanLifeApp mainApp;

    /* 取色器标签 */
    @FXML private Label colorPickerLabel;

    /* 按钮:拷贝取色器获取的颜色代码 */
    @FXML protected Button copyButton;

    /* 颜色选择器 */
    @FXML
    protected ColorPicker selectColorPicker;
    
    @FXML
    private void initialize() {
        String value;
        /* 显示语言转换 */
        value = mainApp.languageConf.getFeild("colorPicker");
        colorPickerLabel.setText(value);

        value = mainApp.languageConf.getFeild("copy");
        copyButton.setText(value);
      
    }

    public ColorPickerViewController(LeanLifeApp mainApp){
       setMainApp(mainApp);
    }

    @FXML
    public void handleCopyColorPiker(){
        //System.out.println("复制颜色值");
        String  colorStr = selectColorPicker.getValue().toString();
        //System.out.println("颜色值:"+ colorStr  );
        mainApp.sysMw.publishMsg("copy color code:"+ colorStr  );

        Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(colorStr);
        clipboard.setContent(content);
    }
    
    /**
     *	得到主控制器的引用
     */
    public void setMainApp(LeanLifeApp mainApp) {
        this.mainApp = mainApp;
    }

}
