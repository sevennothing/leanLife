package com.seven.leanLife.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seven.leanLife.component.BasicTab;
import com.seven.leanLife.component.EditorPane;
import com.seven.leanLife.component.WebkitCall;
import com.seven.leanLife.config.ConfigurationService;
import com.seven.leanLife.config.EditorConfigBean;
import com.seven.leanLife.config.PreviewConfigBean;
import com.seven.leanLife.config.SpellcheckConfigBean;
import com.seven.leanLife.engine.AsciidocConverterProvider;
import com.seven.leanLife.engine.AsciidocWebkitConverter;
import com.seven.leanLife.service.ThreadService;
import com.seven.leanLife.service.extension.AsciiTreeGenerator;
import com.seven.leanLife.service.shortcut.ShortcutProvider;
import com.seven.leanLife.service.ui.EditorTabService;
import com.seven.leanLife.utils.ConverterResult;
import com.seven.leanLife.utils.Current;
import com.seven.leanLife.utils.DocumentMode;
import com.seven.leanLife.utils.Tuple;
import javafx.application.HostServices;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class EditorController extends TextWebSocketHandler implements Initializable {
    //private EditorPane editorPane;
    private Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    /** 全局监听端口, webEngine*/
    private int port = 8080;
    private List<WebSocketSession> sessionList = new ArrayList<>();

    private BooleanProperty stopRendering = new SimpleBooleanProperty(false);
    private ObservableList<DocumentMode> modeList = FXCollections.observableArrayList();

    private HostServices hostServices;
    private List<String> supportedModes;
    private ConverterResult lastConverterResult;

    @Autowired
    private Current current;

    @Autowired
    private ApplicationController parentController;
    @Autowired
    private EditorTabService editorTabService;
    @Autowired
    private AsciiTreeGenerator asciiTreeGenerator;
    @Autowired
    private EditorConfigBean editorConfigBean;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ShortcutProvider shortcutProvider;
    @Autowired
    private SpellcheckConfigBean spellcheckConfigBean;
    @Autowired
    private PreviewConfigBean previewConfigBean;
    @Autowired
    private AsciidocConverterProvider converterProvider;

    @Autowired
    public AsciidocWebkitConverter asciidocWebkitConverter;

    @Value("${application.editor.url}")
    private String editorUrl;
    @Value("${application.worker.url}")
    private String workerUrl;
    @Value("${application.preview.url}")
    private String previewUrl;
    @Value("${application.mathjax.url}")
    private String mathjaxUrl;
    @Value("${application.live.url}")
    private String liveUrl;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
        Optional
                .ofNullable(lastConverterResult)
                .map(ConverterResult::getRendered)
                .ifPresent(this::sendOverWebSocket);
    }

    private void sendOverWebSocket(String html) {
        if (sessionList.size() > 0) {
            threadService.runTaskLater(() -> {
                sessionList.stream().filter(WebSocketSession::isOpen).forEach(e -> {
                    try {
                        e.sendMessage(new TextMessage(html));
                    } catch (Exception ex) {
                        logger.error("Problem occured while sending content over WebSocket", ex);
                    }
                });
            });
        }
    }

    public void initializeEditorController(int port){
        this.port = port;
        String listenInfo = "[Port]:" + Integer.toUnsignedString(port);
        logger.info(listenInfo);
        parentController.sysMw.publishMsg(listenInfo);
    }

    @PostConstruct
    public void init(){
        initializeDoctypes();
        threadService.runTaskLater(() -> {
            while (true) {
                try {
                    renderLoop();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        asciidocWebkitConverter.webEngine().setOnAlert(event -> {
            if ("WORKER_LOADED".equals(event.getData())) {
                asciidocWebkitConverter.setMember("leanlife", this);
                ////htmlPane.load(String.format(previewUrl, port, directoryService.interPath()));
            }
        });
        asciidocWebkitConverter.load(String.format(workerUrl, port));
    }

    /*
    @Autowired
    public EditorController(ApplicationController controller,
                            ThreadService threadService,
                            EditorTabService editorTabService,
                            EditorConfigBean editorConfigBean,
                            ShortcutProvider shortcutProvider,
                            AsciiTreeGenerator asciiTreeGenerator,
                            AsciidocWebkitConverter asciidocWebkitConverter,
                            SpellcheckConfigBean spellcheckConfigBean){
        this.parentController = controller;
        this.asciiTreeGenerator = asciiTreeGenerator;
        this.editorConfigBean = editorConfigBean;
        this.editorTabService = editorTabService;
        this.threadService = threadService;
        this.spellcheckConfigBean = spellcheckConfigBean;
        this.shortcutProvider = shortcutProvider;
        this.asciidocWebkitConverter = asciidocWebkitConverter;

        initializeDoctypes();
        threadService.runTaskLater(() -> {
            while (true) {
                try {
                    renderLoop();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        asciidocWebkitConverter.webEngine().setOnAlert(event -> {
            if ("WORKER_LOADED".equals(event.getData())) {
                asciidocWebkitConverter.setMember("leanlife", this);
                ////htmlPane.load(String.format(previewUrl, port, directoryService.interPath()));
            }
        });
        asciidocWebkitConverter.load(String.format(workerUrl, port));
    }
    */

    public Path getConfigPath(){
        return parentController.getConfigPath();
    }

    public int getPort() {
        return port;
    }

    public void loadEditPane(BasicTab tab){
        String tmpPath = parentController.getTempPath() + "/";
        Date date = new Date();
        Long ts = date.getTime();
        tmpPath += ts.toString();
        Path path = Paths.get(tmpPath);
        System.out.println("Editor:"+  tmpPath);
        editorTabService.initEditorTab(tab,path);
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


    private Stage[] getAllStages() {
        return new Stage[]{
                /*
                        stage,
                        detachStage,
                        asciidocTableStage,
                        markdownTableStage
                */
        };
    }
    /**
     *  初始化编辑器的配置
     */
    public void applyInitialConfigurations() {
        String aceTheme = editorConfigBean.getAceTheme().get(0);
        editorConfigBean.getEditorTheme().stream().findFirst().ifPresent(theme -> {
            applyTheme(theme, getAllStages());
            editorConfigBean.updateAceTheme(aceTheme);
        });
        /*
        editorConfigBean.getAceTheme().stream().findFirst().ifPresent(ace -> {
            applyForAllEditorPanes(editorPane -> editorPane.setTheme(ace));
        });
        */
    }
    /**
     *  初始化文档类型
     */
    private void initializeDoctypes() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object readValue = mapper.readValue(parentController.getConfigPath().resolve("ace_doctypes.json").toFile(),
                    new TypeReference<List<DocumentMode>>() {
            });
            modeList.addAll((Collection) readValue);
            supportedModes = modeList.stream()
                    .map(d -> d.getExtensions())
                    .filter(Objects::nonNull)
                    .flatMap(d -> Arrays.asList(d.split("\\|")).stream())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Problem occured while loading document types", e);
        }
    }

    public void applyTheme(EditorConfigBean.Theme theme, Stage... stages) {
        String themeUri = theme.themeUri();
        if (isNull(themeUri)) {
            return;
        }
        threadService.runActionLater(() -> {
            try {
                for (Stage stage : stages) {
                    if (nonNull(stage) && nonNull(stage.getScene())) {
                        ObservableList<String> stylesheets = stage.getScene().getStylesheets();
                        stylesheets.clear();
                        stylesheets.add(themeUri);
                    }
                }
                String aceTheme = theme.getAceTheme();
                editorConfigBean.updateAceTheme(aceTheme);
            } catch (Exception e) {
                logger.error("Error occured while setting new theme {}", theme);
            }
        });
    }
    private final Pattern bookArticleHeaderRegex
            = Pattern.compile("^:doctype:.*(book|article)", Pattern.MULTILINE);
    private final Pattern forceIncludeRegex
            = Pattern.compile("^:forceinclude:", Pattern.MULTILINE);
    private AtomicBoolean includeAsciidocResource = new AtomicBoolean(false);
    private AtomicReference<Tuple<String, String>> latestTupleReference = new AtomicReference<>();
    private Semaphore renderLoopSemaphore = new Semaphore(1);
    public boolean getIncludeAsciidocResource() {
        return includeAsciidocResource.get();
    }
    public void setIncludeAsciidocResource(boolean includeAsciidocResource) {
        this.includeAsciidocResource.set(includeAsciidocResource);
    }
    private void renderLoop() throws InterruptedException {
        System.out.println("renderLoop()");
        renderLoopSemaphore.acquire();
        if (stopRendering.get()) {
            return;
        }
        Tuple<String, String> tuple = latestTupleReference.get();
        if (isNull(tuple)) {
            return;
        }
        String text = tuple.getKey();
        String mode = tuple.getValue();
        try {
            boolean bookArticleHeader = this.bookArticleHeaderRegex.matcher(text).find();
            boolean forceInclude = this.forceIncludeRegex.matcher(text).find();
            if ("asciidoc".equalsIgnoreCase(mode)) {
                if (bookArticleHeader && !forceInclude) {
                    setIncludeAsciidocResource(true);
                }
                ConverterResult converterResult = converterProvider.get(previewConfigBean).convertAsciidoc(text);
                setIncludeAsciidocResource(false);
                if (lastConverterResult != null) {
                    if (converterResult.getDateTime().isBefore(lastConverterResult.getDateTime())) {
                        return;
                    }
                }
                this.lastConverterResult = converterResult;
                if (this.lastConverterResult.isBackend("html5")) {
                    updateRendered(this.lastConverterResult.getRendered());
                    //// rightShowerHider.showNode(htmlPane);
                }
                if (this.lastConverterResult.isBackend("revealjs") || this.lastConverterResult.isBackend("deckjs")) {
                    //// slidePane.setBackend(this.lastConverterResult.getBackend());
                    //// slideConverter.convert(this.lastConverterResult.getRendered());
                    //// rightShowerHider.showNode(slidePane);
                    System.out.println("TODO: .....");
                }
            } else if ("html".equalsIgnoreCase(mode)) {
//                if (liveReloadPane.getReady()) {
//                    liveReloadPane.updateDomdom();
//                } else {
                threadService.buff("htmlEditor")
                        .schedule(() -> {
                            //// liveReloadPane.load(String.format(liveUrl, port, directoryService.interPath()));
                        }, 500, TimeUnit.MILLISECONDS);
//                }
                //// rightShowerHider.showNode(liveReloadPane);
            }
//            else if ("plantuml".equalsIgnoreCase(mode)) {
//                MarkdownService markdownService = applicationContext.getBean(MarkdownService.class);
//                markdownService.convertToAsciidoc(text, asciidoc -> {
//                    ConverterResult result = converterProvider.get(previewConfigBean).convertAsciidoc(asciidoc);
//                    result.afterRender(this::updateRendered);
//                });
//                rightShowerHider.showNode(htmlPane);
//            }
        } catch (Exception e) {
            setIncludeAsciidocResource(false);
            logger.error("Problem occured while rendering content", e);
        }
    }

    private void updateRendered(String rendered) {
        System.out.println("TODO: updateRendered(" + rendered + ")");
        /*
        Optional.ofNullable(rendered)
                .ifPresent(html -> {
                    htmlPane.refreshUI(html);
                    sendOverWebSocket(html);
                });
                */
    }

    @WebkitCall(from = "editor")
    public void textListener(String text, String mode) {
        //System.out.println("textListener()");
        latestTupleReference.set(new Tuple<>(text, mode));
        if (renderLoopSemaphore.hasQueuedThreads()) {
            renderLoopSemaphore.release();
        }
    }

    @WebkitCall(from = "editor")
    public void checkWordSuggestions(String word) {
        System.out.println("TODO: checkWordSuggestions");
        /*
        final List<String> stringList = dictionaryService.getSuggestionMap()
                .getOrDefault(word, Arrays.asList());
        current.currentEditor().showSuggestions(stringList);
        */
    }

    @WebkitCall(from = "converter.js")
    public void completeWebWorkerExceptionally(Object taskId) {
        threadService.runTaskLater(() -> {
            final Map<String, CompletableFuture<ConverterResult>> workerTasks = asciidocWebkitConverter.getWebWorkerTasks();
            Optional.ofNullable(workerTasks.get(taskId))
                    .filter(c -> !c.isDone())
                    .ifPresent(c -> {
                        final RuntimeException ex = new RuntimeException(String.format("Task: %s is not completed", taskId));
                        c.completeExceptionally(ex);
                    });
            workerTasks.remove(taskId);
        });
    }
    @WebkitCall(from = "converter.js")
    public void completeWebWorker(String taskId, String rendered, String backend, String doctype) {
        threadService.runTaskLater(() -> {
            final ConverterResult converterResult = new ConverterResult(taskId, rendered, backend, doctype);
            final Map<String, CompletableFuture<ConverterResult>> workerTasks = asciidocWebkitConverter.getWebWorkerTasks();
            final CompletableFuture<ConverterResult> completableFuture = workerTasks.get(converterResult.getTaskId());
            Optional.ofNullable(completableFuture)
                    .filter(c -> !c.isDone())
                    .ifPresent(c -> {
                        c.complete(converterResult);
                    });
            workerTasks.remove(converterResult.getTaskId());
        });
    }

    @WebkitCall
    public void updateStatusBox(long row, long column, long linecount, long wordcount) {
        System.out.println("TODO: updateStatusBox" + String.format("(Characters: %d) (Lines: %d) (%d:%d)", wordcount, linecount, row, column));
        /*
        threadService.runActionLater(() -> {
            String charset = getCurrent().currentPath().map(IOHelper::getEncoding).orElse("-");
            statusText.setText(String.format("(Characters: %d) (Lines: %d) (%d:%d) | %s", wordcount, linecount, row, column, charset));
        });
        */
    }

    @WebkitCall(from = "click-binder")
    public void moveCursorTo(int line) {
        System.out.println("TODO:EditorController:: moveCursorTo()");
        //current.currentEditor().moveCursorTo(line);
    }



    public boolean getStopRendering() {
        return stopRendering.get();
    }
    public BooleanProperty stopRenderingProperty() {
        return stopRendering;
    }

    public void browseInDesktop(String url) {
        try {
            hostServices.showDocument(UriComponentsBuilder.fromUriString(url).build().toUri().toASCIIString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public ObservableList<DocumentMode> getModeList() {
        return modeList;
    }

    public EditorConfigBean getEditorConfigBean() {
        return editorConfigBean;
    }
    public void saveDoc(Event... event) {
        //TODO: 保存文档
        parentController.sysMw.publishMsg("TODO:在这里保存文档:" + Thread.currentThread().getStackTrace()[2].getLineNumber());
    }
    public void newDoc(Event... event) {
        //TODO: 新建文档
        parentController.sysMw.publishMsg("TODO:在这里新建文档:" + Thread.currentThread().getStackTrace()[2].getLineNumber());
    }

    public void openDoc(Event... event) {
        //TODO: 打开文档
        parentController.sysMw.publishMsg("TODO:在这里打开文档:" + Thread.currentThread().getStackTrace()[2].getLineNumber());
    }

    /**
     *  用于调试js
     * @param message
     */
    public void log(String message){
        parentController.sysMw.publishMsg("[js]: " + message);
    }


}
