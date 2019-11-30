/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.utils;

/**
 *
 * @author Admin
 */
public class moreUtils {
    

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
        
    public static String convertToARGB(int color) {
        // alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toString(red(color));
        String green = Integer.toString(green(color));
        String blue = Integer.toString(blue(color));
        // System.out.println(Color.red(color));
        return "#" + red + green + blue;
    }
}
