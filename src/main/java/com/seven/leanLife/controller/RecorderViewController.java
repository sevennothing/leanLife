package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;

import javax.sound.sampled.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Duration;


public class RecorderViewController {
    private ApplicationController parentController;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;

    private long recordTime = 0;
    @FXML private Label timecostLabel;
    @FXML private Slider timelineSlider;


    public RecorderViewController(ApplicationController controller){
        this.parentController = controller;
    }

    @FXML
    private void initialize() {

    }



    private AudioFormat getAudioFormat() {
        // 8000,11025,16000,22050,44100 采样率
        float sampleRate = 8000F;
        // 8,16 每个样本中的位数
        int sampleSizeInBits = 16;
        // 1,2 信道数（单声道为 1，立体声为 2，等等）
        int channels = 2;
        // true,false
        boolean signed = true;
        // true,false 指示是以 big-endian 顺序还是以 little-endian 顺序存储音频数据。
        boolean bigEndian = false;
        // 构造具有线性 PCM 编码和给定参数的 AudioFormat。
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
                bigEndian);
    }

    public void voiceRecorderStart(){
        parentController.sysMw.publishMsg("Start Recorder");
        try {
            // 构造具有线性 PCM 编码和给定参数的 AudioFormat。
            audioFormat = getAudioFormat();

            // 根据指定信息构造数据行的信息对象，这些信息包括单个音频格式。此构造方法通常由应用程序用于描述所需的行。
            // lineClass - 该信息对象所描述的数据行的类
            // format - 所需的格式

            final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), arg1->{
                recordTime += 100;
                Long totalSecond = recordTime / 1000;
                Long hour = totalSecond / 3600;
                Long minute = (totalSecond % 3600) / 60;
                Long second = (totalSecond % 3600) % 60;
                Long millis = recordTime % 1000;
                timecostLabel.setPrefSize(100,30);
                timecostLabel.setText(Long.toString(hour) + ":"+Long.toString(minute) + ":" + Long.toString(second) + "."
                + Long.toString(millis));
                timelineSlider.adjustValue(new Double(recordTime));
            }));

            timeline.setCycleCount( Animation.INDEFINITE );
            timeline.play();

            /*
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            // 如果请求 DataLine，且 info 是 DataLine.Info 的实例（至少指定一种完全限定的音频格式），
            // 上一个数据行将用作返回的 DataLine 的默认格式。
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            // 开启线程
            new CaptureThread().start();
            */
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
