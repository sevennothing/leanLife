package com.seven.leanLife.engine;

import com.seven.leanLife.utils.ConverterResult;

/**
 * Created by usta on 22.08.2015.
 */
public interface AsciidocConvertible {

    public ConverterResult convertDocbook(String asciidoc);

    public ConverterResult convertAsciidoc(String asciidoc);

    public ConverterResult convertHtml(String asciidoc);

    public void convertOdf(String asciidoc);

    public void fillOutlines(Object doc);

    public String applyReplacements(String asciidoc);
}
