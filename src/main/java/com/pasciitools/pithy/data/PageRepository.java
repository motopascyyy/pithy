package com.pasciitools.pithy.data;

import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public abstract class PageRepository {

    protected static final String DELIM = File.separator;

    protected String githubMarkdown(File file) throws IOException {
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.GITHUB);
        options.set(Parser.EXTENSIONS, Arrays.asList(
                YamlFrontMatterExtension.create(),
                FootnoteExtension.create()));
        Parser parser = Parser.builder(options).build();
        Node document = parser.parseReader(new FileReader(file));
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        return renderer.render(document);
    }

}
