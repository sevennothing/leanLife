package com.seven.leanLife.component;

import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

/**
 *  构件ImageView
 */
public class ImageViewBuilt {
    private ImageView imageView;
    public ImageViewBuilt(ImageView imageView) {
        this.imageView = imageView;
    }
    public static ImageViewBuilt image(URL url, double minSize) {
        ImageView imageView = null;
        imageView = new ImageView(new Image(url.toExternalForm()));
        return new ImageViewBuilt(imageView);
    }

    public ImageViewBuilt tip(String text) {
        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(imageView, tooltip);
        return this;
    }
    public ImageViewBuilt click(EventHandler eventHandler) {
        imageView.setOnMouseClicked(eventHandler);
        return this;
    }
    public ImageView build() {
        return this.imageView;
    }
    public ImageViewBuilt clazz(String clazz) {
        imageView.getStyleClass().add(clazz);
        return this;
    }

}
