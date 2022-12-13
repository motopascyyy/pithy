package com.pasciitools.pithy.data;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class PageRepository {

    protected static final String DELIM = File.separator;

    protected String githubMarkdown(File file) throws IOException {
        List<Extension> extensions = Collections.singletonList(YamlFrontMatterExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parseReader(new FileReader(file));
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        return renderer.render(document);
    }

}
