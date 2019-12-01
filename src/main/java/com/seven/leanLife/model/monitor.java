package com.seven.leanLife.model;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Monitor {
    private StringProperty msg = new SimpleStringProperty();

    public Monitor(){
        this.msg = new SimpleStringProperty();
    }
    public StringProperty msgProperty(){
        return msg;
    }

    public final  String getMsg(){
        return  msgProperty().get();
    }

    public final void setMsg(String text){
        msgProperty().set(text);
    }

}

