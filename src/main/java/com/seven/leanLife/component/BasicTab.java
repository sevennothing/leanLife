package com.seven.leanLife.component;

import com.seven.leanLife.controller.ApplicationController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 定义基础Tab组件
 *  本系统使用通用风格的TAB组件组织功能部分
 */
@Component
@Scope("prototype")
public class BasicTab  extends Tab{
    private final Logger logger = LoggerFactory.getLogger(BasicTab.class);
    private final ApplicationController controller;
    private ContextMenu contextMenu;

    /** 功能Tab的菜单栏 */
    private ToolBar toolBar = null;

    /** 用来保存tab的特定菜单 */
    //private FlowPane privateMenu;

    // 对于编辑器窗口存在一个文件路径，非编辑器不需要使用path
    private Path path;

    @Autowired
    public BasicTab(ApplicationController controller){
        this.controller  = controller;
    }

    public Label getLabel() {
        if (Objects.isNull(this.getGraphic()))
            this.setGraphic(new Label());
        return (Label) this.getGraphic();
    }

    public String getTabText(){
        Label label = getLabel();
        return label.getText();
    }

    public void setTabText(String tabText) {
        Label label = getLabel();
        label.setText(tabText);
    }
    public void select() {
        this.getTabPane().getSelectionModel().select(this);
    }
    @Override
    public String toString() {
        return getTabText();
    }

    public ButtonType close() {
        this.select();
        closeIt();
        return ButtonType.YES;
    }

    public void closeIt() {
        this.getTabPane().getTabs().remove(this); // keep it here
    }

    private void closeTabs(boolean withoutMe){
        ObservableList<Tab> blackList = FXCollections.observableArrayList();
        blackList.addAll(this.getTabPane().getTabs());
        if(withoutMe)
            blackList.remove(this);
        blackList.stream()
                .filter(t -> t instanceof BasicTab)
                .map(t -> (BasicTab) t).sorted((mo1, mo2) -> {
            return 0;
        }).forEach(basicTab -> {
            ButtonType close = basicTab.close();
        });
    }

    public void enableMenu(){
        contextMenu = new ContextMenu();
        addMenu("Close", actionEvent->{
            this.close();
        } );
        addMenu("Close All", actionEvent->{
            closeTabs(false);
        });

        addMenu("Close Others", actionEvent->{
            closeTabs(true);
        });

        addMenu("Select Next Tab", actionEvent->{
            TabPane tabPane = this.getTabPane();
            if(tabPane.getSelectionModel().isSelected(tabPane.getTabs().size() - 1)){
                tabPane.getSelectionModel().selectFirst();
            }else{
                tabPane.getSelectionModel().selectNext();
            }
        });

        addMenu("Select Previous Tab", actionEvent->{
            SingleSelectionModel<Tab> selectionModel = this.getTabPane().getSelectionModel();
            if(selectionModel.isSelected(0)){
                selectionModel.selectLast();
            }else{
                selectionModel.selectPrevious();
            }
        });

        this.contextMenuProperty().setValue(contextMenu);
    }

    public MenuItem addMenu(String name, EventHandler<ActionEvent> value){
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(value);
        contextMenu.getItems().add(menuItem);
        return menuItem;
    }

    public Path getPath(){
        return path;
    }

    public void setPath(Path path){
        this.path = path;
    }
    /*
    public void setPrivateMenu(FlowPane flowPane){
        this.privateMenu = flowPane;
    }
    public FlowPane getPrivateMenu(){
        return this.privateMenu;
    }
    */

    public void setToolBar(ToolBar toolBar) {
        this.toolBar = toolBar;
    }
    public ToolBar getToolBar(){
        return this.toolBar;
    }
}
