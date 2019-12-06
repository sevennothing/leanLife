package com.seven.leanLife.config;

import com.seven.leanLife.LeanLifeApp;
import com.seven.leanLife.controller.ApplicationController;
import com.seven.leanLife.service.ThreadService;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by usta on 17.07.2015.
 */
@Component
public class ConfigurationService {
    //private final LocationConfigBean locationConfigBean;
    //private final EditorConfigBean editorConfigBean;
    //private final PreviewConfigBean previewConfigBean;
    //private final HtmlConfigBean htmlConfigBean;
    //private final DocbookConfigBean docbookConfigBean;
    //private final StoredConfigBean storedConfigBean;
    private final ApplicationController controller;
    private final ThreadService threadService;
    //private final LangConfigBean langConfigBean;
    private final SpellcheckConfigBean spellcheckConfigBean;
    //private final TerminalConfigBean terminalConfigBean;
    //private final ExtensionConfigBean extensionConfigBean;
    private VBox configBox;

    @Autowired
    public ConfigurationService(ApplicationController controller,
                                ThreadService threadService,
                                SpellcheckConfigBean spellcheckConfigBean
                                ) {
        this.controller = controller;
        this.threadService = threadService;
        this.spellcheckConfigBean = spellcheckConfigBean;
        //this.langConfigBean = langConfigBean;
    }

    public void loadConfigurations(Runnable... runnables) {
        spellcheckConfigBean.load();
        //langConfigBean.load();

        List<ConfigurationBase> configBeanList = Arrays.asList(
//                ,spellcheckConfigBean
        );

        ScrollPane formsPane = new ScrollPane();

        ToggleGroup toggleGroup = new ToggleGroup();
        FlowPane flowPane = new FlowPane(5, 5);
        flowPane.setPadding(new Insets(5, 0, 0, 0));

        List<ToggleButton> toggleButtons = new ArrayList<>();
        VBox editorConfigForm = null;

        /*
        for (ConfigurationBase configBean : configBeanList) {
            VBox form = configBean.createForm();
            ToggleButton toggleButton = ToggleButtonBuilt.item(configBean.formName()).click(event -> {
                formsPane.setContent(form);
            });
            toggleButtons.add(toggleButton);

            if (Objects.isNull(editorConfigForm))
                editorConfigForm = form;
        }
        */

        final VBox finalEditorConfigForm = editorConfigForm;
        threadService.runActionLater(() -> {

            formsPane.setContent(finalEditorConfigForm);

            for (ToggleButton toggleButton : toggleButtons) {
                toggleGroup.getToggles().add(toggleButton);
                flowPane.getChildren().add(toggleButton);
            }

            /*
            configBox = controller.getConfigBox();
            configBox.getChildren().add(flowPane);
            configBox.getChildren().add(formsPane);
            */

            VBox.setVgrow(formsPane, Priority.ALWAYS);

            for (Runnable runnable : runnables) {
                runnable.run();
            }
        });
    }
}