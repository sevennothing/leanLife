/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Stack;

import com.seven.leanLife.utils.MoreUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.w3c.dom.css.RGBColor;

/**
 * FXML Controller class
 *
 * @author Admin
 */
public class ColorPickerViewController {
    private ApplicationController parentController;

    /* 取色器标签 */
    @FXML private Label colorPickerLabel;

    /* 按钮:拷贝取色器获取的颜色代码 */
    @FXML protected Button copyButton;

    /* 颜色选择器 */
    @FXML
    private ColorPicker selectColorPicker;

    @FXML
    private VBox colorToolVbox;

    private Properties colorCode;
    @FXML
    private void initialize() {
        String value;
        colorCode = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("/config/colors.code.properties");
            colorCode.load(in);
            in.close();
        }catch(IOException e) {
            parentController.sysMw.publishMsg(e.toString());
        }

        /* 加载 CSS 颜色定义 */
        int maxItemPerRow = 15;
        int colum = 0;
        HBox hbox = new HBox();
        hbox.getStyleClass().add("color-hbox");
        colorToolVbox.getChildren().add(hbox);
        for(String key : colorCode.stringPropertyNames()){

            StackPane csp = new StackPane();

            Rectangle rect = new Rectangle();
            rect.setHeight(38);
            rect.setWidth(38);
            rect.setFill(Color.web(colorCode.getProperty(key)));
            rect.setId(key);
            rect.getStyleClass().add("color-rect");
            //rect.setOpacity(0.6);
            rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    MouseButton mousebtn = event.getButton();
                    MoreUtils util = new MoreUtils();
                    if(mousebtn == MouseButton.PRIMARY) {
                        /* 鼠标左击拷贝 颜色代码 */
                        util.setClipboard(util.convertColorToRGB((Color) rect.getFill()));
                    }
                    if(mousebtn == MouseButton.SECONDARY){
                        /* 鼠标右键单击拷贝颜色名称 */
                        util.setClipboard(rect.getId());
                    }

                    if(event.getClickCount() == 2){
                        /* 双击操作*/
                    }
                }
            });


            Label label = new Label();
            Tooltip tip = new Tooltip();
            //tip.setContentDisplay();
            tip.setText(key + "=" + colorCode.getProperty(key) + "\r\n" +"鼠标左键拷贝颜色代码,鼠标右键拷贝颜色名称");
            tip.getStyleClass().add("color-rect-tip");
            label.setText("");
            label.setPrefSize(38,38);
            label.setTooltip(tip);

            csp.getChildren().addAll(rect, label);

            hbox.getChildren().add(csp);

            colum ++;
            //parentController.sysMw.publishMsg(key + "=" + colorCode.getProperty(key));
            if((colum % maxItemPerRow) == 0){
                hbox = new HBox();
                hbox.getStyleClass().add("color-hbox");
                colorToolVbox.getChildren().add(hbox);
            }
        }

        /* 显示语言转换 */
        value = parentController.languageConf.getFeild("colorPicker");
        colorPickerLabel.setText(value);

        value = parentController.languageConf.getFeild("copy");
        copyButton.setText(value);
      
    }

    public ColorPickerViewController(ApplicationController controller){
        this.parentController = controller;
    }

    @FXML
    public void handleCopyColorPiker(){
        //System.out.println("复制颜色值");
        String  colorStr = selectColorPicker.getValue().toString();
        //System.out.println("颜色值:"+ colorStr  );
        parentController.sysMw.publishMsg("copy color code:"+ colorStr  );

        Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(colorStr);
        clipboard.setContent(content);
    }

}
