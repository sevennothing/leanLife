package com.seven.leanLife.utils;

import com.seven.leanLife.component.BasicTab;
import com.seven.leanLife.component.EditorPane;
import com.seven.leanLife.controller.ApplicationController;
import com.seven.leanLife.service.ThreadService;
import com.seven.leanLife.service.ui.EditorTabService;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
/**
 *  当前软件系统工作状态
 */
@Component
public class Current {
    private final ApplicationController controller;
    private final ThreadService threadService;
    private final EditorTabService editorTabService;
    private Map<String, Integer> cache;
    private Path currentEpubPath;
    private BasicTab choiceTab;

    @Autowired
    public Current(final ApplicationController controller,
                   final ThreadService threadService,
                   final EditorTabService editorTabService
                   ) {
        this.controller = controller;
        this.threadService = threadService;
        this.editorTabService = editorTabService;
    }

    public  BasicTab currentEidtorTab() {
        return editorTabService.getEditorTab();
    }

    public Optional<Path> currentPath() {
        return Optional.ofNullable(currentEidtorTab()).map(BasicTab::getPath);
    }
    public WebView currentWebView() {
        //return currentEidtorTab().getEditorPane().getWebView();
        return editorTabService.getEditorPane().getWebView();
    }

    public EditorPane currentEditor() {
        return editorTabService.getEditorPane();
    }
    public WebEngine currentEngine() {
        return currentWebView().getEngine();
    }
    public Map<String, Integer> getCache() {
        if (Objects.isNull(cache))
            cache = new ConcurrentHashMap<String, Integer>();
        return cache;
    }
    public void setCache(Map<String, Integer> cache) {
        this.cache = cache;
    }
    public void setCurrentTabText(String currentEidtorTabText) {
        currentEidtorTab().setTabText(currentEidtorTabText);
    }
    public String getCurrentTabText() {
        return currentEidtorTab().getTabText();
    }
    public String currentEditorValue() {
        if (Platform.isFxApplicationThread())
            return (String) currentEngine().executeScript("editor.getValue()");
        final CompletableFuture<String> completableFuture = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                try {
                    Object result = currentEngine().executeScript("editor.getValue()");
                    completableFuture.complete((String) result);
                } catch (Exception ex) {
                    completableFuture.completeExceptionally(ex);
                }
            });
        }, threadService.executor());
        return completableFuture.join();
    }
    public String currentEditorSelection() {
        String value = (String) currentEngine().executeScript("editor.session.getTextRange(editor.getSelectionRange())");
        return value;
    }
    public void insertEditorValue(String content) {
        ((JSObject) currentEngine().executeScript("window")).setMember("insertValue", content);
        currentEngine().executeScript("editor.insert(insertValue)");
    }
    public void clearEditorValue() {
        currentEngine().executeScript(String.format("editor.setValue('')"));
    }
    public String currentEditorMode() {
        return currentEditor().editorMode();
    }
    public void setCurrentEpubPath(Path currentEpubPath) {
        this.currentEpubPath = currentEpubPath;
    }
    public Path getCurrentEpubPath() {
        return currentEpubPath;
    }
    public String currentClearTabText() {
        String tabText = getCurrentTabText().replace("*", "").trim();
        tabText = tabText.contains(".") ? tabText.split("\\.")[0] : tabText;
        return tabText;
    }
    public void setChoiceTab(BasicTab tab){
        this.choiceTab = tab;
    }

    public BasicTab getChoiceTab(){
        return this.choiceTab;
    }
}

