package com.seven.leanLife.utils;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;
/**
 * Created by usta on 20.06.2015.
 */
public class ConverterResult {
    private LocalDateTime dateTime = LocalDateTime.now();
    private String taskId;
    private String rendered;
    private String backend;
    private String doctype;
    public ConverterResult(String taskId, String rendered, String backend, String doctype) {
        this.taskId = taskId;
        this.rendered = fixLineEnding(rendered);
        this.backend = backend;
        this.doctype = doctype;
    }
    public void setRendered(String rendered) {
        this.rendered = fixLineEnding(rendered);
    }
    private String fixLineEnding(String rendered) {
        if (Objects.isNull(rendered)) {
            return null;
        }
        return rendered.replaceAll("\\R", "\n");
    }
    public String getRendered() {
        return rendered;
    }
    public void setBackend(String backend) {
        this.backend = backend;
    }
    public String getBackend() {
        return backend;
    }
    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }
    public String getDoctype() {
        return doctype;
    }
    public boolean isBackend(String backend) {
        return backend.equals(this.backend);
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void afterRender(Consumer<String> consumer, Runnable... runnable) {
        if (Objects.nonNull(rendered)) {
            consumer.accept(rendered);
            for (Runnable r : runnable) {
                r.run();
            }
        }
    }
}

