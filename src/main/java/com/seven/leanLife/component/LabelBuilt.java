package com.seven.leanLife.component;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;


/**
 *  提供标签构建
 */
public class LabelBuilt {
    private Label label;
    public LabelBuilt(Label label) {
        this.label = label;
    }
    public static LabelBuilt icon( Ikon ikon,double minSize) {
        Label iconLabel = new Label();
        iconLabel.setGraphic(new FontIcon(ikon));
        iconLabel.setMinWidth(minSize);
        return new LabelBuilt(iconLabel);
    }

    /**
     * 对已有的label进行重新设定
     * @param label
     * @param ikon
     * @param minSize
     * @return
     */
    public static LabelBuilt resetIcon( Label label,Ikon ikon,double minSize) {
        LabelBuilt lb = new LabelBuilt(label);
        label.setGraphic(new FontIcon(ikon));
        label.setMinWidth(minSize);
        return lb;
    }

    public LabelBuilt tip(String text) {
        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(label, tooltip);
        return this;
    }
    public LabelBuilt click(EventHandler eventHandler) {
        label.setOnMouseClicked(eventHandler);
        return this;
    }
    public Label build() {
        return label;
    }
    public LabelBuilt clazz(String clazz) {
        label.getStyleClass().add(clazz);
        return this;
    }
}