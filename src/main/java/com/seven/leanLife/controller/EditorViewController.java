package com.seven.leanLife.controller;

import com.seven.leanLife.LeanLifeApp;
import com.seven.leanLife.component.EditorPane;
import com.seven.leanLife.config.ConfigurationService;
import com.seven.leanLife.config.SpellcheckConfigBean;
import com.seven.leanLife.service.ThreadService;
import com.seven.leanLife.service.extension.AsciiTreeGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@Controller
public class EditorViewController {
    private ApplicationController parentController;
    private EditorPane editorPane;

    @Autowired
    private ThreadService threadService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AsciiTreeGenerator asciiTreeGenerator;

    @Autowired
    private SpellcheckConfigBean spellcheckConfigBean;

    @FXML
    private AnchorPane leftEditPane;

    public EditorViewController(ApplicationController controller,
                                ThreadService threadService,
                                AsciiTreeGenerator asciiTreeGenerator,
                                SpellcheckConfigBean spellcheckConfigBean){
        this.parentController = controller;
        this.asciiTreeGenerator = asciiTreeGenerator;
        this.threadService = threadService;
        this.spellcheckConfigBean = spellcheckConfigBean;
    }
    public void load(){
        editorPane = new EditorPane(this,threadService,applicationContext,asciiTreeGenerator,spellcheckConfigBean);
        leftEditPane.getChildren().add(editorPane);
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


    public void cutCopy(String data) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(data.replaceAll("\\R", "\n"));
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public void pasteRaw() {
        /*
        EditorPane editorPane = current.currentEditor();
        Clipboard systemClipboard = Clipboard.getSystemClipboard();
        if (systemClipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(systemClipboard.getFiles());
            if (block.isPresent()) {
                editorPane.insert(block.get());
                return;
            }
        }
        if (systemClipboard.hasImage()) {
            Image image = systemClipboard.getImage();
            Optional<String> block = parserService.toImageBlock(image);
            if (block.isPresent()) {
                editorPane.insert(block.get());
                return;
            }
        }
        */
        editorPane.execCommand("paste-raw-1");
    }
    public void paste() {
        /*
        EditorPane editorPane = current.currentEditor();
        Clipboard systemClipboard = Clipboard.getSystemClipboard();
        if (systemClipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(systemClipboard.getFiles());
            if (block.isPresent()) {
                editorPane.insert(block.get());
                return;
            }
        }
        if (systemClipboard.hasImage()) {
            Image image = systemClipboard.getImage();
            Optional<String> block = parserService.toImageBlock(image);
            if (block.isPresent()) {
                editorPane.insert(block.get());
                return;
            }
        }
        try {
            if (systemClipboard.hasHtml() || asciidocWebkitConverter.isHtml(systemClipboard.getString())) {
                String content = Optional.ofNullable(systemClipboard.getHtml()).orElse(systemClipboard.getString());
                if (current.currentTab().isAsciidoc() || current.currentTab().isMarkdown()) {
                    content = (String) asciidocWebkitConverter.call(current.currentTab().htmlToMarkupFunction(), content);
                }
                editorPane.insert(content);
                return;
            }
        } catch (Exception e) {
            parentController.sysMw.publishMsg(e.getMessage());
        }
        editorPane.execCommand("paste-raw-1");
        */
    }


}
