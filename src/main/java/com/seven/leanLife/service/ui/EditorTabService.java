package com.seven.leanLife.service.ui;

import com.seven.leanLife.component.BasicTab;
import com.seven.leanLife.component.EditorPane;
import com.seven.leanLife.component.LabelBuilt;
import com.seven.leanLife.component.WebkitCall;
import com.seven.leanLife.config.ConfigurationService;
import com.seven.leanLife.config.EditorConfigBean;
import com.seven.leanLife.config.SpellcheckConfigBean;
import com.seven.leanLife.controller.ApplicationController;
import com.seven.leanLife.controller.EditorController;
import com.seven.leanLife.service.ThreadService;
import com.seven.leanLife.service.extension.AsciiTreeGenerator;
import com.seven.leanLife.service.shortcut.ShortcutProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Optional;

/**
 *  基于TAB的编辑器服务;
 *  用于集中管理所有的编辑服务
 *  每一个tab可能位于不同的显示面板
 */
@Component
public class EditorTabService {
    private ApplicationController controller;
    private EditorPane editorPane;
    private BasicTab editorTab;

    @Autowired
    private ThreadService threadService;
    @Autowired
    private EditorService editorService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ShortcutProvider shortcutProvider;
    @Autowired
    private AsciiTreeGenerator asciiTreeGenerator;

    @Autowired
    private SpellcheckConfigBean spellcheckConfigBean;

    @Autowired
    private EditorConfigBean editorConfigBean;

    @Value("${application.editor.url}")
    private String editorUrl;

    private ObservableList<Optional<Path>> closedPaths = FXCollections.observableArrayList();
    private ObservableList<Tab> editorTabs = FXCollections.observableArrayList();

    @Autowired
    public EditorTabService(ApplicationController controller,
                            ThreadService threadService,
                            EditorService editorService,
                            EditorConfigBean editorConfigBean,
                            ShortcutProvider shortcutProvider,
                            AsciiTreeGenerator asciiTreeGenerator,
                            SpellcheckConfigBean spellcheckConfigBean){
        this.controller = controller;
        this.asciiTreeGenerator = asciiTreeGenerator;
        this.editorConfigBean = editorConfigBean;
        this.threadService = threadService;
        this.spellcheckConfigBean = spellcheckConfigBean;
        this.editorService = editorService;
        this.shortcutProvider = shortcutProvider;
    }

    private VBox createPreview(){
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        FlowPane flowPane = new FlowPane();
        flowPane.setVgap(5);
        flowPane.setHgap(15);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label htmllabel = new Label();
        htmllabel.setText("HTML");
        FontIcon icon = new FontIcon("fa-html5");
        htmllabel.setGraphic(icon);

        Label pdflabel = new Label();
        pdflabel.setText("PDF");
        icon = new FontIcon("fa-file-pdf-o");
        pdflabel.setGraphic(icon);

        flowPane.getChildren().addAll(htmllabel,pdflabel);

        //FlowPane.setMargin(htmllabel, Insets.EMPTY);
        //FlowPane.setMargin(pdflabel, Insets.EMPTY);
        Pane pane = new Pane();
        Label browser = new Label();
        browser.setText("Browser");
        browser.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.externalBrowse();
            }
        });
        hbox.getChildren().addAll(flowPane,pane, browser);
        AnchorPane previewPane = new AnchorPane();
        vbox.getChildren().addAll(hbox, previewPane);

        return vbox;
    }

    private void attachEditorPane(BasicTab tab, Runnable... runnables){
        AnchorPane anchorPane = new AnchorPane();
        SplitPane splitPane = new SplitPane();

        editorPane = new EditorPane(controller.editorController,this,
                editorConfigBean,
                threadService,
                shortcutProvider,
                applicationContext,asciiTreeGenerator,spellcheckConfigBean);

        // 加载编辑页面
        Node editorVBox = editorService.createEditorVBox(editorPane, tab);

        // editorVBox 加入splitPane
        splitPane.getItems().add(editorVBox);
        anchorPane.getChildren().add(splitPane);
        AnchorPane.setBottomAnchor(splitPane, 0D);
        AnchorPane.setTopAnchor(splitPane, 0D);
        AnchorPane.setLeftAnchor(splitPane, 0D);
        AnchorPane.setRightAnchor(splitPane, 0D);

        tab.setContent(anchorPane);
        editorPane.getHandleReadyTasks().clear();
        editorPane.getHandleReadyTasks().addAll(runnables);
        editorPane.load(String.format(editorUrl, controller.editorController.getPort()));

        // 创建preview 视图
        VBox vbox = createPreview();
        splitPane.getItems().add(vbox);

        // 将tab加入editorTabs
        editorTabs.add(tab);
        editorTab = tab;
    }

    public EditorPane getEditorPane() {
        return editorPane;
    }

    public BasicTab getEditorTab(){
        return editorTab;
    }

    //TODO: exterbalBrowse
    public void externalBrowse() {
        /* 外部浏览器中运行 */
        //rightShowerHider.getShowing().ifPresent(ViewPanel::browse);
    }

    /**
     * 将编辑器加载到指定Tab 上
     */
    public void initEditorTab(BasicTab dsttab, Path path, Runnable... runnables){
        /* 遍历查找是否已经存在指定文件的编辑器 */
        for(Tab tab:editorTabs){
            BasicTab basicTab = (BasicTab) tab;
            //Path currentPath = basicTab.getPath();
            //TODO:文件正在被编辑不允许打开编辑器,或者以只读模式打开
        }

        dsttab.setTabText(path.getFileName().toString());
        dsttab.setPath(path);
        attachEditorPane(dsttab, runnables);
    }

    public void addEditorTab(Path path, Runnable... runnables){
        /* 遍历查找是否已经存在指定文件的编辑器 */
        for(Tab tab:editorTabs){
            BasicTab basicTab = (BasicTab) tab;
            //Path currentPath = basicTab.getPath();
        }

        BasicTab tab = createEditorTab();
        tab.setTabText(path.getFileName().toString());
        attachEditorPane(tab,runnables);
    }

    public BasicTab createEditorTab(){
        final BasicTab tab = applicationContext.getBean(BasicTab.class);
        // TODO:实现编辑TAB相关特性
        return tab;
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
            controller.sysMw.publishMsg(e.getMessage());
        }
        editorPane.execCommand("paste-raw-1");
        */
    }


    @WebkitCall(from = "editor")
    public void onscroll(Object pos, Object max) {
        //TODO: editor tab on scroll
        System.out.println("TODO: editor tab onscroll");
        /*
        rightShowerHider.getShowing()
                .ifPresent(s -> s.onscroll(pos, max));
         */
    }
    @WebkitCall(from = "editor")
    public void scrollByLine(String text) {
        System.out.println("TODO: editor tab scrollByLine");
        /*
        threadService.runActionLater(() -> {
            try {
                rightShowerHider.getShowing().ifPresent(w -> w.scrollByLine(text));
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        });
        */
    }
    @WebkitCall(from = "click-binder")
    public void moveCursorTo(int line) {
        System.out.println("TODO: editor tab Move CursorTo");
    }
    @WebkitCall(from = "editor")
    public void scrollByPosition(String text) {
        System.out.println("TODO: editor tab scroll by position");
        /*
        threadService.runActionLater(() -> {
            try {
                String selection = asciidocWebkitConverter.findRenderedSelection(text);
                rightShowerHider.getShowing().ifPresent(w -> w.scrollByPosition(selection));
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        });
        */
    }

}
