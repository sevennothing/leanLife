package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;
import org.springframework.stereotype.Controller;


@Controller
public class EditorViewController {
    private LeanLifeApp mainApp;
    public EditorViewController(LeanLifeApp mainApp){
        this.mainApp = mainApp;
    }

}
