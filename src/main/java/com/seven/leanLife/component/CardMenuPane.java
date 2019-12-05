/**
 * @author caijun.Li
 * @version 0.1
 *
 */

package com.seven.leanLife.component;
import java.util.*;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.layout.*;
import javafx.scene.control.Label;

/*** 绘制 卡片式菜单
 *
 */
public class CardMenuPane extends  Pane{
    private double w =100 , h = 200;
    private VBox rootVbox = null;
    private ArrayList menuList = null;
    public CardMenuPane(){
        this.menuList = new ArrayList();
        this.rootVbox = new VBox();
        this.rootVbox.setPrefSize(w,h);
    }

    public int getMenuItemNum(){
        return rootVbox.getChildren().size();
    }

    public Label addMenuItem(String name){
        Label newLabel = new Label();
        newLabel.setText(name);
        this.rootVbox.getChildren().add(newLabel);
        return newLabel;
    }
    public void binClickedEventHandle(Label label,
                                      EventHandler<? super MouseEvent> value) {
            label.setOnMouseClicked(value);
    }

    public void removeMenuItem(String name){
        int i;
        for (i=0; i<this.rootVbox.getChildren().size(); i++){
            //TODO
        }
    }
}

