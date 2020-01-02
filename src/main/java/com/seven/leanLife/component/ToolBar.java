package com.seven.leanLife.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;

/**
 *  ToolBar 用于实现工具栏
 *  该工具栏可以有多个flowPane
 */
public class ToolBar extends Pane {
    private Boolean isVertical = false;
    private double w,h;
    private HBox hbox;
    private ArrayList<FlowPane> flowPanes;

    public ToolBar(){
        flowPanes = new ArrayList<>();
        setIsVertical(false);
        getStyleClass().add("toolbar");
        paintToolbar();
    }

    public void setIsVertical(Boolean value){
        isVertical = value;
    }

    private void paintToolbar(){
        if(hbox == null) {
            hbox = new HBox();
            getChildren().add(hbox);
            hbox.setSpacing(10.0);
            hbox.setFillHeight(true);
            AnchorPane.setTopAnchor(hbox, Double.valueOf(2));
            AnchorPane.setBottomAnchor(hbox, Double.valueOf(0D));
            AnchorPane.setRightAnchor(hbox, Double.valueOf(0));
            AnchorPane.setLeftAnchor(hbox, Double.valueOf(0));
        }
        //hbox.setPrefSize(w,h);
        hbox.getChildren().clear();
        for(int i=0; i<flowPanes.size(); i++){
            FlowPane fp = flowPanes.get(i);
            fp.setPrefSize(w,h);
            hbox.getChildren().add(fp);
            HBox.setHgrow(fp, Priority.ALWAYS);
            HBox.setMargin(fp, Insets.EMPTY);
        }
    }

    public void clear(){
        flowPanes.clear();
        hbox.getChildren().clear();
    }

    /*
    @Override
    public ObservableList<Node> getChildren(){
        return hbox.getChildren();
    }
    */

    private FlowPane checkoutFlowPaneWithId(String id){
        for(int i=0; i<flowPanes.size(); i++){
            FlowPane fp = flowPanes.get(i);
            if(fp.getId().equals(id)){
                return fp;
            }
        }
        return null;
    }

    /**
     *  向某个flowPane中添加Item
     * @param item
     * @param id
     */
    public void addItem(Label item, String id) {
        FlowPane fp = checkoutFlowPaneWithId(id);
        if (fp == null) {
            // 先创建一个FlowPane
            fp = new FlowPane();
            fp.setId(id);
            flowPanes.add(fp);
            fp.setHgap(7.5);
            fp.setVgap(5);
            fp.setAlignment(Pos.CENTER_LEFT);
            fp.setMinWidth(0); // fix
            fp.getStyleClass().add(id);
        }
        fp.getChildren().add(item);
        paintToolbar();
    }

    public void addFlowPane(FlowPane flowPane, String id){
        if(checkoutFlowPaneWithId(id) != null) {
            System.out.println(id + " already exist");
            return;
        }
        flowPane.setId(id);
        flowPanes.add(flowPane);
        paintToolbar();
    }

    @Override
    public void setPrefSize(double width, double height){
        w = width;
        h = height;
        paintToolbar();
    }
}
