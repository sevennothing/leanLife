/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seven.leanLife.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.seven.leanLife.model.Monitor;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;

/**
 * FXML Controller class
 *
 * @author Admin
 */
@Controller
public class TimeToolViewController {
    @FXML
    private TextField currentTimeText;
    @FXML
    private TextField currentTimeStamp;
    @FXML
    private Button copyCurrentTsButton;
    @FXML
    private TextField assignDateText;
    @FXML
    private TextField dateToTsText;
    @FXML
    private TextField assignTsText;
    @FXML
    private TextField tsToDateText;
    @FXML
    private ClockPane BeiJingClock;
    @FXML
    private ClockPane ShanghaiClock;
    @FXML
    private ClockPane HongKongClock;
    @FXML
    private ClockPane LondonClock;
    @FXML
    private ClockPane AmericanClock;
    @FXML
    private Label descLabel;

    private MonitorWin sysMw;

    private final String BeiJingTZ = "Asia/Shanghai";
    private final String ShangHaiTZ = "Asia/Shanghai";
    private final String HongKongTZ = "Asia/Hong_Kong";
    private final String LonDonTZ = "Europe/London";
    private final String AmericaTZ = "America/Dawson";


    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        descLabel.setWrapText(true);
        BeiJingClock.setSize(160,160);
        BeiJingClock.setFingerColor(Color.GREEN, Color.BLUE, Color.RED);
        ShanghaiClock.setSize(160,160);
        HongKongClock.setSize(160,160);
        LondonClock.setSize(160,160);
        AmericanClock.setSize(160,160);
        final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), arg1->{
            Date date = new Date();
            Long ts = date.getTime();
            currentTimeText.setText(df.format(date));
            currentTimeStamp.setText(Long.toString(ts / 1000));

            /* 北京 时间*/
            Map<String,Integer> res = timestampConvertToDate(ts,BeiJingTZ );
            BeiJingClock.setHour(res.get("hour") % 12);
            BeiJingClock.setMinute(res.get("minute"));
            BeiJingClock.setSecond(res.get("second"));
            BeiJingClock.setPM(res.get("hour") / 12 > 0);
            BeiJingClock.setDescText("北京:" + res.get("year") + "-" + res.get("month") + "-" + res.get("day"));

            /* 伦敦 时间*/
            res = timestampConvertToDate(ts,LonDonTZ );
            LondonClock.setHour(res.get("hour") % 12);
            LondonClock.setMinute(res.get("minute"));
            LondonClock.setSecond(res.get("second"));
            LondonClock.setPM(res.get("hour") / 12 > 0);
            LondonClock.setDescText("伦敦:" + res.get("year") + "-" + res.get("month") + "-" + res.get("day"));

            /* 美国 时间*/
            res = timestampConvertToDate(ts,AmericaTZ );
            AmericanClock.setHour(res.get("hour") % 12);
            AmericanClock.setMinute(res.get("minute"));
            AmericanClock.setSecond(res.get("second"));
            AmericanClock.setPM(res.get("hour") / 12 > 0);
            AmericanClock.setDescText("华盛顿:" + res.get("year") + "-" + res.get("month") + "-" + res.get("day"));
        }));
        timeline.setCycleCount( Animation.INDEFINITE );
        timeline.play();

    }

    private Map<String,Integer> timestampConvertToDate(long ts, String timezone){
        Map<String,Integer> map = new HashMap<String,Integer>();
        Integer hour,minute,second,year,month,day;

        //计算年月日
        Date date = new Date(ts);
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.setTimeZone(TimeZone.getTimeZone(timezone));
        year = calender.get(Calendar.YEAR);
        month= calender.get(Calendar.MONTH) + 1;
        day = calender.get(Calendar.DATE);

        second = calender.get(Calendar.SECOND);
        minute = calender.get(Calendar.MINUTE);
        hour = calender.get(Calendar.HOUR_OF_DAY);

        /*
        // 根据时区调整时间
        ts = ts + new Double(timezone * 60 * 60).longValue();
        second = new Double(ts % 60).intValue();
        minute = new Double((ts / (60)) % 60).intValue();
        hour = new Double((ts / (60 * 60)) % 24).intValue();
        */

        map.put("hour",hour);
        map.put("minute",minute);
        map.put("second",second);
        map.put("year",year);
        map.put("month",month);
        map.put("day",day);
        return map;
    }

    @FXML
    private void handleCopyCurrentTs(){
        String  currentTs = currentTimeStamp.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(currentTs);
        clipboard.setContent(content);
    }

    @FXML
    private void handleDateToTs(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String dateStr = assignDateText.getText();
        if(dateStr.length() < 1){
            //System.out.println("请输入正确内容");
            sysMw.publishMsg("请输入正确的时间:yyyy-MM-dd HH:mm:ss\r\n");
            return;
        }
        try {
            Date date = df.parse(dateStr);
            Long ts = date.getTime();
            dateToTsText.setText(ts.toString());
        } catch (ParseException e) {
            sysMw.publishMsg(e.toString());
        }
    }

    @FXML
    private void handleTsToDate(){
        String tsStr = assignTsText.getText();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Long ts = Long.parseLong(tsStr);
        if(tsStr.length() < 13) {
            ts = ts * 1000;
        }
        Date date= new Date(ts);

        tsToDateText.setText(df.format(date));
    }

    public void setMw(MonitorWin mw){
        this.sysMw = mw;
        mw.publishMsg("Time Tool opened");
    }

}
