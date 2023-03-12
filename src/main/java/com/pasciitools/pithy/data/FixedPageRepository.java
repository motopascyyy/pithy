package com.pasciitools.pithy.data;

import com.pasciitools.pithy.git.GitHelper;
import com.pasciitools.pithy.model.FileMetaData;
import com.pasciitools.pithy.model.FixedPage;
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.misc.Extension;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.util.UriEncoder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FixedPageRepository extends PageRepository implements Serializable {
    @Serial
    private static final long serialVersionUID = -5347643118688988698L;
    private static final String ORDER = "order";
    private final transient Logger log = LoggerFactory.getLogger(FixedPageRepository.class);

    private final GitHelper gitHelper;

    private static final String PATH_SUFFIX = DELIM + "fixed_pages";


    public List<FixedPage> getListOfFixedPages () throws IOException {
        String fullPath = gitHelper.getRepoRootDirectory() + PATH_SUFFIX;
        File fixedPageFolder = new File (fullPath);
        if (!fixedPageFolder.exists() || !fixedPageFolder.isDirectory() || !fixedPageFolder.canExecute()) {
            throw new IOException("Could not access blog's `fixed_pages` folder. Please make sure it exists at location: " + fullPath);
        }
        var listOfPages = new ArrayList<FixedPage>();
        FilenameFilter filenameFilter = (file, s) -> s.endsWith(".md");
        var listOfFiles = fixedPageFolder.listFiles(filenameFilter);
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                try {
                    FixedPage page = createFixedPageObject(file, gitHelper);
                    listOfPages.add(page);
                } catch (GitAPIException e) {
                    log.error("Couldn't get metadata from file: " + file.getName(), e);
                }

            }
            Collections.sort(listOfPages);
        }
        return listOfPages;
    }

    public FixedPage getFixedPage (String fixedPageName) throws IOException{
        File repoFolder = gitHelper.getRepoRootDirectory();
        if (!repoFolder.exists() || !repoFolder.isDirectory() || !repoFolder.canExecute()) {
            throw new IOException("Could not access blog repository folder. Please make sure the `defaultGitPath` is properly configured");
        }

        String fileName = fixedPageName + ".md";

        File file = new File (repoFolder.getAbsoluteFile() + DELIM + PATH_SUFFIX + DELIM + fileName);
        FixedPage page = null;
        if (file.exists() && file.isFile()) {
            try {
                page = createFixedPageObject(file, gitHelper);
            } catch (GitAPIException e) {
                log.error("Couldn't create a Fixed Page from input {} because of: {}", fixedPageName, e.getMessage());
            }
        }

        return page;
   }

    public FixedPage createFixedPageObject (File file, GitHelper gitHelper) throws GitAPIException, IOException {
        FileMetaData metaData = gitHelper.getOriginDate("fixed_pages/" + file.getName());
        var creationTime = metaData.getInitialDate();
        var updatedTime = metaData.getLatestTime();
        String fileName = file.getName();
        var endIndex = fileName.length() - 3;
        var startIndex = fileName.contains(".") && fileName.indexOf(".") != endIndex ? fileName.indexOf(".") + 1 : 0;

        List<Extension> extensions = Collections.singletonList(YamlFrontMatterExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        AbstractYamlFrontMatterVisitor visitor = new AbstractYamlFrontMatterVisitor();
        Node document = parser.parseReader(new FileReader(file));
        visitor.visit(document);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();

        var content = renderer.render(document);

        var label = fileName.substring(startIndex, endIndex).trim();
        var fixedPageBuilder = new FixedPage().toBuilder().
                pageOrder(getFixedPageOrderFromFrontMatter(visitor)).
                content(content).
                label(label).
                dateCreated(creationTime).
                lastUpdated(updatedTime).
                fileName(fileName).
                url(UriEncoder.encode(label));
        return fixedPageBuilder.build();
    }

    /**
     * Returns the integer value for the YAML Key <code>order</code>. If any issues arise
     * finding this value (null input, no key found, NumberFormatException...), -1 will be returned.
     */
    private int getFixedPageOrderFromFrontMatter (AbstractYamlFrontMatterVisitor visitor) {
        if (visitor != null) {
            var visitorData = visitor.getData();
            if (visitorData != null &&
                    visitorData.containsKey(ORDER) &&
                    !visitorData.get(ORDER).isEmpty()) {
                var orderString = visitorData.get(ORDER).get(0);
                try {
                    return Integer.parseInt(orderString);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }

    public FixedPageRepository (GitHelper gitHelper) {
        this.gitHelper = gitHelper;
    }

}
