package com.seven.leanLife.component;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.*;

/**
 *  不带名称的初始化，将输出信息到system.out
 */
public class MonitorWin extends Pane{
    private double w=800,h=400;
    private TextArea monInfoArea = null;
    private Button clrButton = null;
    private Button cpyButton = null;
    private SimpleBooleanProperty isClosed;
    private Boolean noneWin;

    /**
     *  不带参数的构建，不正的打开监控串口
     */
    public MonitorWin(){
        noneWin = true;
    }

    public MonitorWin(String name){
        openWindow(name);
    }

    public void openWindow(String name){
        noneWin = false;
        isClosed = new SimpleBooleanProperty(false);
        Stage monStage = new Stage();
        StackPane pane = new StackPane();
        monInfoArea = new TextArea();
        monInfoArea.setId("msgArea");
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
        monStage.setTitle(name);
        monStage.setAlwaysOnTop(true);

        scene.getStylesheets().add(getClass().getResource("/css/monitorWin.css").toExternalForm());
        monStage.show();

        monStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                publishMsg("监控窗口被关闭");
                setIsClosed(true);
            }
        });
    }

    public Boolean isNone(){
        return this.noneWin;
    }

    public void setIsClosed(Boolean value){
        noneWin = true;
        isClosed.set(value);
    }

    public SimpleBooleanProperty isClosedProperty(){
        return isClosed;
    }

    /**
     *  清除所有信息
     */

    public void clrAllMsg(){
        if(noneWin) return;
        monInfoArea.clear();
    }

    /***
     *  发布一条消息
     * @param msg
     */

    public void publishMsg(String msg){
        if(noneWin) {
            System.out.println(msg);
            return;
        }

        //monInfoArea.setText(msg);
        monInfoArea.appendText(msg);
    }

}
