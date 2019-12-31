package com.seven.leanLife.engine;
import com.seven.leanLife.component.EditorViewPanel;
import com.seven.leanLife.config.AsciidocConfigMerger;
import com.seven.leanLife.config.EditorConfigBean;
import com.seven.leanLife.config.PreviewConfigBean;
import com.seven.leanLife.controller.ApplicationController;
import com.seven.leanLife.controller.EditorController;
import com.seven.leanLife.helper.IOHelper;
import com.seven.leanLife.service.DirectoryService;
import com.seven.leanLife.service.ThreadService;
import com.seven.leanLife.utils.ConverterResult;
import com.seven.leanLife.utils.Current;
import javafx.application.Platform;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.json.JsonObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by usta on 09.04.2015.
 */
@Component("WebkitEngine")
public class AsciidocWebkitConverter extends EditorViewPanel implements AsciidocConvertible {

    private static final List<String> extensions = Arrays.asList("stem", "asciimath", "latexmath", "mathml", "math", "plantuml", "uml", "ditaa", "graphviz", "tree");

    private final PreviewConfigBean previewConfigBean;
    //// private final DocbookConfigBean docbookConfigBean;
    //// private final HtmlConfigBean htmlConfigBean;
     private final AsciidocConfigMerger configMerger;

    private static final Map<String, CompletableFuture<ConverterResult>> webWorkerTasks = new ConcurrentHashMap();

    @Value("${application.index.url}")
    private String indexUrl;

    private Logger logger = LoggerFactory.getLogger(AsciidocWebkitConverter.class);
    private final DirectoryService directoryService;

    @Autowired
    public AsciidocWebkitConverter(ThreadService threadService,
                                   ApplicationController controller,
                                   ///Current current,
                                   EditorConfigBean editorConfigBean,
                                   PreviewConfigBean previewConfigBean,
                                   ////DocbookConfigBean docbookConfigBean,
                                   ////HtmlConfigBean htmlConfigBean,
                                   AsciidocConfigMerger configMerger,
                                   DirectoryService directoryService) {
        super(threadService, controller, editorConfigBean);
        this.previewConfigBean = previewConfigBean;
        ////this.docbookConfigBean = docbookConfigBean;
        ////this.htmlConfigBean = htmlConfigBean;
        this.configMerger = configMerger;
        this.directoryService = directoryService;
    }

    public String getTemplate(String templateDir) {

        Path path = controller.getConfigPath().resolve("slide/templates").resolve(templateDir);

        if (Files.notExists(path)) {
            logger.error("Template not found in {}", path);
            return "";
        }

        String template = IOHelper.readFile(path);
        return template;
    }

    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    @Override
    public void runScroller(String text) {
        // no-op
    }

    @Override
    public void scrollByPosition(String text) {
        // no-op
    }

    @Override
    public void scrollByLine(String text) {
        // no-op
    }

    @Override
    public void browse() {
        controller.editorController.browseInDesktop(String.format(indexUrl, controller.editorController.getPort(),
                directoryService.interPath()));
    }

    @Override
    public void fillOutlines(Object doc) {
        threadService.runActionLater(() -> {
            try {
                getWindow().call("fillOutlines", doc);
            } catch (Exception e) {
                logger.debug("Problem occured while filling outlines", e);
            }
        });
    }

    @Override
    public String applyReplacements(String asciidoc) {

        if (!Platform.isFxApplicationThread()) {
            CompletableFuture<String> completableFuture = new CompletableFuture<>();
            completableFuture.runAsync(() -> {
                threadService.runActionLater(() -> {
                    try {
                        String replacements = applyReplacements(asciidoc);
                        completableFuture.complete(replacements);
                    } catch (Exception e) {
                        completableFuture.completeExceptionally(e);
                    }
                });
            });
            return completableFuture.join();
        }

        try {
            return (String) getWindow().call("apply_replacements", asciidoc);
        } catch (Exception e) {
            logger.debug("Problem occured while applying replacements", e);
        }

        return asciidoc;

    }

    public String findRenderedSelection(String content) {
        this.setMember("context", content);
        return (String) webEngine().executeScript("findRenderedSelection(context)");
    }

    protected ConverterResult convert(String functionName, String asciidoc, JsonObject config) {
        try {
            asciidoc = applyContentReplacements(asciidoc);
            return convertContent(functionName, asciidoc, config).get(5, TimeUnit.SECONDS);
        } catch (Exception e1) {

            try {
                return convertContent(functionName, asciidoc, config).get(60, TimeUnit.SECONDS);
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    private CompletableFuture<ConverterResult> convertContent(String functionName, String asciidoc, JsonObject config) {

        final CompletableFuture<ConverterResult> completableFuture = new CompletableFuture();
        final String taskId = UUID.randomUUID().toString();

        webWorkerTasks.put(taskId, completableFuture);
        final String conf = config.toString();
        threadService.runActionLater(() -> {
            this.setMember("taskId", taskId);
            this.setMember("editorValue", asciidoc);
            this.setMember("editorOptions", conf);
            try {
                webEngine().executeScript(String.format("if ((typeof %s)!== \"undefined\"){ %s(taskId,editorValue,editorOptions) }", functionName, functionName));
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    private JsonObject updateConfig(String asciidoc, JsonObject config) {
        return configMerger.updateConfig(asciidoc, config);
    }

    @Override
    public ConverterResult convertDocbook(String asciidoc) {
        /////return convert("convertDocbook", asciidoc, updateConfig(asciidoc, docbookConfigBean.getJSON()));
        return null;
    }

    @Override
    public ConverterResult convertAsciidoc(String asciidoc) {
        return convert("convertAsciidoc", asciidoc, updateConfig(asciidoc, previewConfigBean.getJSON()));
    }

    @Override
    public ConverterResult convertHtml(String asciidoc) {
        ////return convert("convertHtml", asciidoc, updateConfig(asciidoc, htmlConfigBean.getJSON()));
        return null;
    }

    @Override
    public void convertOdf(String asciidoc) {

    }

    private String applyContentReplacements(String content) {
        for (String extension : extensions) {
            String replacement = String.join("_", extension.split(""));
            content = content.replaceAll("\\[" + extension, "[" + replacement);
            content = content.replaceAll(extension + "::",  replacement + "::");
            content = content.replaceAll(extension + ":\\[",  replacement + ":[");
        }
        return content;
    }

    public Map<String, CompletableFuture<ConverterResult>> getWebWorkerTasks() {
        return webWorkerTasks;
    }

    public boolean isHtml(String text) {
        Object isHtml = this.call("isHtml", text);

        if (isHtml instanceof Boolean) {
            return (boolean) isHtml;
        }

        return false;
    }
}
