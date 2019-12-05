package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;
import javafx.fxml.FXML;
import org.springframework.stereotype.Controller;

@Controller
public class EditorViewController {
    private LeanLifeApp mainApp;


    public EditorViewController(LeanLifeApp mainApp){
        this.mainApp = mainApp;
    }

    @FXML
    private void togglePreviewView(){

    }

    @FXML
    private void toggleConfigurationView(){

    }

    @FXML
    private void changeWorkingDir(){

    }
}
