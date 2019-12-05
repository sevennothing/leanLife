package com.seven.leanLife.component;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class ToolBar extends Pane {
    private Boolean isVertical = false;
    private double w,h;
    private HBox hbox;
    private ArrayList<ToolBarItem> itemList;

    public ToolBar(){
        itemList = new ArrayList<>();
        setIsVertical(false);
        paintToolbar();
        getStyleClass().add("toolbar");
    }

    public void setIsVertical(Boolean value){
        isVertical = value;
    }


    public void paintToolbar(){
        hbox = new HBox();
        hbox.setSpacing(10.0);
        for(int i=0; i<itemList.size(); i++){
            ToolBarItem item = itemList.get(i);
            hbox.getChildren().add(item);
        }
        getChildren().add(hbox);
    }

    public void clear(){
        itemList.clear();
        hbox.getChildren().clear();
    }

    /*
    @Override
    public ObservableList<Node> getChildren(){
        return hbox.getChildren();
    }
    */


    public void addItem(ToolBarItem item){
        itemList.add(item);
        paintToolbar();
    }
}
