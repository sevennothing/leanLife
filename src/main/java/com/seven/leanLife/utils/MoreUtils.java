/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.utils;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;

/**
 *
 * @author Admin
 */
public class MoreUtils {
    

    public static int alpha(int color) {
            return color >>> 24;
    }
        /**
         * Return the red component of a color int. This is the same as saying
         * (color >> 16) & 0xFF
         */
        public static int red(int color) {
            return (color >> 16) & 0xFF;
        }
        /**
         * Return the green component of a color int. This is the same as saying
         * (color >> 8) & 0xFF
         */
        public static int green(int color) {
            return (color >> 8) & 0xFF;
        }
        /**
         * Return the blue component of a color int. This is the same as saying
         * color & 0xFF
         */
    public static int blue(int color) {
            return color & 0xFF;
    }
        
    public String convertToARGB(int color) {
        System.out.println(Integer.toString(color));
        // alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toString(red(color));
        String green = Integer.toString(green(color));
        String blue = Integer.toString(blue(color));
        // System.out.println(Color.red(color));
        return "#" + red + green + blue;
    }

    public String convertColorToRGB(Color c){
        String hex = String.format( "#%02X%02X%02X",
                (int)( c.getRed() * 255 ),
                (int)( c.getGreen() * 255 ),
                (int)( c.getBlue() * 255 ) );
        return hex;
    }

    public void setClipboard(String text){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
}
