package com.seven.leanLife.controller;

import com.seven.leanLife.utils.EventProcess;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

/***
 * @// TODO: 2019/12/3  快捷键
 * onAction 如何使用重写??
 */
public class ToolBarItem  extends Pane {
    Image icon = null;
    Button bt ;
    public ToolBarItem(){
        textProperty().addListener((observable, oldValue, newValue) ->{
            paintItem();
        } );
        getStyleClass().add("toolbar-item");
    }

    public void bindProcess(EventProcess e){
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                e.process();
            }
        });

        KeyCombination kc = new KeyCodeCombination(KeyCode.S,KeyCombination.SHORTCUT_DOWN);
        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("键盘按下了按键=" + event.getCode().name());
                e.process();
            }
        });

    }

    public void paintItem(){
        //System.out.println("paint Toolbar item");
        StackPane stackPane = new StackPane();
        ImageView imgView = new ImageView();

        Label itemText = new Label();
        itemText.getStyleClass().add("toolbar-item-text");
        itemText.setTextAlignment(TextAlignment.CENTER);
        if(icon != null) {
            imgView.setImage(icon);
        }
        imgView.setFitWidth(30);
        imgView.setFitHeight(30);
        itemText.setText(textProperty().get());
        itemText.setOpacity(0.4);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().addAll(imgView,itemText);
        //StackPane.setAlignment(itemText,Pos.CENTER);
        StackPane.setAlignment(itemText,Pos.BOTTOM_CENTER);
        //stackPane.prefWidthProperty().bind();
        stackPane.getStyleClass().add("toolbar-item");
        getChildren().addAll(stackPane);
    }

    public final void setImage(Image img){
        icon = img;
        paintItem();
    }

    public final StringProperty textProperty() {
        if (text == null) {
            text = new SimpleStringProperty(this, "text", "");
        }
        return text;
    }
    private StringProperty text;
    public final void setText(String value) { textProperty().setValue(value); }
    public final String getText() { return text == null ? "" : text.getValue(); }

    /**
     * MnemonicParsing property to enable/disable text parsing.
     * If this is set to true, then the Label text will be
     * parsed to see if it contains the mnemonic parsing character '_'.
     * When a mnemonic is detected the key combination will
     * be determined based on the succeeding character, and the mnemonic
     * added.
     *
     * <p>
     * The default value for Labeled is false, but it
     * is enabled by default on some Controls.
     * </p>
     */
    private BooleanProperty mnemonicParsing;
    public final void setMnemonicParsing(boolean value) {
        mnemonicParsingProperty().set(value);
    }
    public final boolean isMnemonicParsing() {
        return mnemonicParsing == null ? false : mnemonicParsing.get();
    }
    public final BooleanProperty mnemonicParsingProperty() {
        if (mnemonicParsing == null) {
            mnemonicParsing = new SimpleBooleanProperty(this, "mnemonicParsing");
        }
        return mnemonicParsing;
    }

    /**
     * The Item's action, which is invoked whenever the item is fired. This
     * may be due to the user clicking on the item with the mouse, or by
     * a touch event, or by a key press, or if the developer programmatically
     * invokes the {@link #fire()} method.
     */
    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }
    public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }
    public final EventHandler<ActionEvent> getOnAction() { return onActionProperty().get(); }
    private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
        @Override protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }

        @Override
        public Object getBean() {
            return ToolBarItem.this;
        }

        @Override
        public String getName() {
            return "onAction";
        }
    };

}
