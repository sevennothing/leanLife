package com.seven.leanLife.controller;


import com.seven.leanLife.component.BasicTab;
import com.seven.leanLife.component.LabelBuilt;
import com.seven.leanLife.component.ToolBar;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;

import java.net.URL;
import java.nio.file.Path;

public class PicturesViewController {
    private ApplicationController parentController;
    private BasicTab tab;
    private Path picturePath;

    @FXML
    private SubScene pictureSubScene;
    @FXML
    private HBox pictureCenterHbox;

    public PicturesViewController(ApplicationController controller){
            this.parentController = controller;
    }

    public void setBasicTab(BasicTab tab){
        this.tab = tab;
        createToolBar();
    }


    private String nextPicture(){
        String url  = "file:" + picturePath + "/IMG_20171015_151114.jpg";
        return  url;
    }

    private void createToolBar(){
        double minSize = 16.01;
        ToolBar toolBar = new ToolBar();
        final Label pre = LabelBuilt.icon(FontAwesome.ARROW_CIRCLE_LEFT, minSize).clazz("picture-toolbar").
                tip("pre-picture").
                build();

        final Label next = LabelBuilt.icon(FontAwesome.ARROW_CIRCLE_RIGHT, minSize).clazz("picture-toolbar").
                tip("next-picture").
                build();

        final Label searchPlus = LabelBuilt.icon(FontAwesome.SEARCH_PLUS, minSize).clazz("picture-toolbar").
                tip("放大").
                build();
        final Label searchMinus = LabelBuilt.icon(FontAwesome.SEARCH_MINUS, minSize).clazz("picture-toolbar").
                tip("縮小").
                build();

        toolBar.addItem(pre,"picture-toolbar-pre");
        toolBar.addItem(next,"picture-toolbar-pre");
        toolBar.addItem(searchPlus,"picture-toolbar-pre");
        toolBar.addItem(searchMinus,"picture-toolbar-pre");

        tab.setToolBar(toolBar);
    }

    public void initialized(){
        double minSize = 30.01;
        final Label leftLabel = LabelBuilt.icon(FontAwesome.CHEVRON_LEFT, minSize).
                clazz("pre-picture").build();
        final Label rightLabel = LabelBuilt.icon(FontAwesome.CHEVRON_RIGHT, minSize).
                clazz("next-picture").build();

        picturePath = parentController.getPicturesPath();

        double w = parentController.getScene().getWidth() - 260;
        double h = parentController.getScene().getHeight() - 120;

        pictureCenterHbox.setSpacing(w - (minSize * 2));
        pictureCenterHbox.setAlignment(Pos.CENTER);
        pictureCenterHbox.getChildren().addAll(leftLabel,rightLabel);

        pictureSubScene.setWidth(w);
        pictureSubScene.setHeight(h);

        String url = nextPicture();
        AnchorPane anchorPane = new AnchorPane();
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(url));
        imageView.setFitWidth(w);
        imageView.setFitHeight(h);
        imageView.setPreserveRatio(true);

        anchorPane.getChildren().add(imageView);
        AnchorPane.setLeftAnchor(imageView, Double.valueOf(10));
        AnchorPane.setRightAnchor(imageView, Double.valueOf(10));
        AnchorPane.setBottomAnchor(imageView, Double.valueOf(10));
        AnchorPane.setTopAnchor(imageView, Double.valueOf(10));
        pictureSubScene.setRoot(anchorPane);
        pictureSubScene.setLayoutX(100);
        pictureSubScene.setLayoutY(60);

    }

}
