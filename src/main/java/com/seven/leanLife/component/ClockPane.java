package com.seven.leanLife.component;

import java.util.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.layout.*;

public class ClockPane extends Pane{
	private int hour;
	private int minute;
	private int second;
	private Color hourColor = Color.GREEN;
	private Color minuteColor = Color.BLUE;
	private Color secondColor = Color.RED;
	private Boolean isPM = false;
	private String descText = "";

	private double w =160 , h = 160;
	public ClockPane(){
		setCurrentTime();
	}

	public ClockPane(int hour, int minute, int second){
		this.hour=hour;
		this.minute=minute;
		this.second=second;
		paintClock();
	}

	public void setSize(int wide , int high){
		w = wide;
		h = high;
		paintClock();
	}
	public void setFingerColor(Color hour, Color minute, Color second){
		hourColor = hour;
		minuteColor = minute;
		secondColor = second;
		paintClock();
	}

	public void setDescText(String text){
		descText = text;
		paintClock();
	}

	public void setPM(Boolean pm){
		this.isPM = pm;
		paintClock();
	}
	public int getHour(){
		return hour;
	}
	public void setHour(int hour){
		this.hour=hour;
		paintClock();
	}
	public int getMinute(){
		return minute;
	}
	public void setMinute(int minute){
		this.minute=minute;
		paintClock();
	}
	public int getSecond(){
		return second;
	}
	public void setSecond(int second){
		this.second=second;
		paintClock();
	}
	public double getW(){
		return w;
	}
	public double getH(){
		return h;
	}
	public void setCurrentTime(){
		Calendar calendar= new GregorianCalendar();
		this.hour=calendar.get(Calendar.HOUR_OF_DAY);
		this.minute=calendar.get(Calendar.MINUTE);
		this.second=calendar.get(Calendar.SECOND);
		paintClock();
	}
	public void paintClock(){
		double clockR = Math.min(w, h)*0.8*0.5;
		double centerX=w/2;
		double centerY= h/2;

		Circle circle = new Circle(centerX,centerY,clockR);
		circle.setFill(Color.WHITE);
		circle.setStroke(Color.BLACK);

		double r1=Math.PI/6;
		double r2=Math.PI/3;
		Text t1 = new Text(centerX+(clockR-20)*Math.sin(r1),
				centerY-(clockR-20)*Math.cos(r1),"1");
		Text t2= new Text(centerX+(clockR-20)*Math.sin(r2),
				centerY-(clockR-20)*Math.cos(r2),"2");
		Text t3= new Text(centerX+clockR-20,centerY+3,"3");
		Text t4= new Text(centerX+(clockR-20)*Math.cos(r1),
				centerY+(clockR-10)*Math.sin(r1),"4");
		Text t5= new Text(centerX+(clockR-20)*Math.cos(r2),
				centerY+(clockR-10)*Math.sin(r2),"5");
		Text t6= new Text(centerX-3,centerY+clockR-15,"6");
		Text t7= new Text(centerX-(clockR-10)*Math.sin(r1),
				centerY+(clockR-10)*Math.cos(r1),"7");
		Text t8= new Text(centerX-(clockR-10)*Math.sin(r2),
				centerY+(clockR-5)*Math.cos(r2),"8");
		Text t9= new Text(centerX-clockR+10,centerY+5,"9");
		Text t10=new Text(centerX-(clockR-10)*Math.cos(r1),
				centerY-(clockR-20)*Math.sin(r1),"10");
		Text t11= new Text(centerX-(clockR)*Math.sin(r1),
				centerY-(clockR-20)*Math.cos(r1),"11");
		Text t12 = new Text(centerX-8,centerY-clockR+20,"12");


		Line [] line = new Line[60];
		double jiaodu = 0;
		for(int j=0;j<60;j++){
			if((j)%5!=0)
				line[j]=new Line(centerX+clockR*Math.sin(jiaodu),
						centerY+clockR*Math.cos(jiaodu),
						centerX+(clockR-5)*Math.sin(jiaodu),
						centerY+(clockR-5)*Math.cos(jiaodu));
			else
				line[j]=new Line(centerX+clockR*Math.sin(jiaodu),
						centerY+clockR*Math.cos(jiaodu),
						centerX+(clockR-10)*Math.sin(jiaodu),
						centerY+(clockR-10)*Math.cos(jiaodu));
			jiaodu+=Math.PI/30;
		}

		double sLength = clockR*0.8;
		double secondX = centerX + sLength*Math.sin(second*(2*Math.PI/60));
		double secondY = centerY - sLength*Math.cos(second*(2*Math.PI/60));
		Line sLine = new Line(centerX,centerY,secondX,secondY);
		sLine.setStroke(secondColor);

		double mLength = clockR*0.65;
		double xMinute = centerX + mLength*Math.sin(minute*(2*Math.PI/60));
		double yMinute = centerY - mLength*Math.cos(minute*(2*Math.PI/60));
		Line mLine = new Line(centerX, centerY,xMinute,yMinute);
		mLine.setStroke(minuteColor);

		double hLength = clockR*0.5;
		double hourX=centerX+hLength*Math.sin((hour%12+minute/60.0)*(2*Math.PI/12));
		double hourY=centerY-hLength*Math.cos((hour%12+minute/60.0)*(2*Math.PI/12));
		Line hLine = new Line(centerX,centerY,hourX,hourY);
		hLine.setStroke(hourColor);

		getChildren().clear();
		getChildren().addAll(circle,sLine,mLine,hLine,t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12);

		if(isPM){
			Text tm = new Text(centerX-8,centerY + clockR - 30,"PM");
			getChildren().add(tm);
		}else{
			Text tm = new Text(centerX-8,centerY + clockR - 30,"AM");
			getChildren().add(tm);
		}
		for(int j=0;j<60;j++) {
			getChildren().addAll(line[j]);
		}

		/* 添加注释 */
        Integer descLen = descText.length();
		Text descT = new Text(centerX - (descLen / 2) * 10, centerY + clockR + 14, descText);
		getChildren().add(descT);

	}

}

