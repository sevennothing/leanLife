package com.seven.leanLife.controller;
import com.seven.leanLife.model.Monitor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.*;

public class MonitorWin extends Pane{
    private double w=800,h=400;
    private TextArea monInfoArea = null;
    private Button clrButton = null;
    private Button cpyButton = null;
    public MonitorWin(){
        Stage monStage = new Stage();
        StackPane pane = new StackPane();
        monInfoArea = new TextArea();
        monInfoArea.setWrapText(true);
        monInfoArea.setPrefSize(w,h);
        monInfoArea.setEditable(false);
        pane.getChildren().add(monInfoArea);

        AnchorPane anchorPane = new AnchorPane();
        VBox vbox = new VBox();

        clrButton = new Button();
        clrButton.setText("清屏");
        clrButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clrAllMsg();
            }
        });
        cpyButton = new Button();
        cpyButton.setText("拷贝");
        cpyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String info = monInfoArea.getText();
                Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(info);
                clipboard.setContent(content);
            }
        });

        vbox.getChildren().addAll(clrButton,cpyButton);
        anchorPane.getChildren().addAll(vbox);
        AnchorPane.setRightAnchor(vbox,Double.valueOf(5));
        pane.getChildren().add(anchorPane);


        Scene scene = new Scene(pane, w, h);
        monStage.setScene(scene);
        monStage.setTitle("监控终端");
        monStage.show();
    }


    public void clrAllMsg(){
        monInfoArea.clear();
    }

    public void publishMsg(String msg){
        //monInfoArea.setText(msg);
        monInfoArea.appendText(msg);
    }

}
