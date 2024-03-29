package com.pasciitools.pithy.git;

import com.pasciitools.pithy.data.AppConfigRepo;
import com.pasciitools.pithy.model.FileMetaData;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@DependsOn({"baseConfig", "gitBean", "credProv"})
public class GitHelper implements Serializable {


    @Serial
    private static final long serialVersionUID = 8514080561431376157L;

    private final transient Git git;


    private transient AppConfigRepo appConfigRepo;

    private final transient CredentialsProvider credProv;

    public FetchResult fetchUpdates () throws GitAPIException {

        if (git != null) {
//            var git = gitOptional.get();
            FetchCommand fetch = git.fetch();
            var useSSHOpt = appConfigRepo.findByConfigKey("git.useSSH");
            boolean useSSH = useSSHOpt.filter(appConfig -> Boolean.parseBoolean(appConfig.getConfigValue())).isPresent();

            if (!useSSH && credProv != null)
                fetch.setCredentialsProvider(credProv);
            fetch.setRemote("origin");
            return fetch.call();
        }
        return null;
    }

    public PullResult pullUpdates () throws GitAPIException {
        if (git != null) {
//            var git = gitOptional.get();
            PullCommand pull = git.pull();
            var useSSHOpt = appConfigRepo.findByConfigKey("git.useSSH");
            boolean useSSH = useSSHOpt.filter(appConfig -> Boolean.parseBoolean(appConfig.getConfigValue())).isPresent();
            if (!useSSH && credProv != null)
                pull.setCredentialsProvider(credProv);
            pull.setRemote("origin");
            return pull.call();
        }
        return null;
    }

    public List<DiffEntry> getDiffs (ObjectId oldHead, ObjectId newHead) throws IOException, GitAPIException {
        if (git != null) {
//            var git = gitOptional.get();
            ObjectReader reader = git.getRepository().newObjectReader();
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldHead);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, newHead);
            return git.diff()
                    .setNewTree(newTreeIter)
                    .setOldTree(oldTreeIter)
                    .call();
        } else
            return Collections.emptyList();
    }

    public FileMetaData getOriginDate (String fileName) throws GitAPIException {
        if (git != null) {
//            var git = gitOptional.get();

            LogCommand logCommand = git.log();
            var revisions = logCommand.addPath(fileName).call();
            FileMetaData metaData = new FileMetaData();
            var iter = revisions.iterator();
            int counter = 0;
            while (iter.hasNext()) {
                RevCommit commit = iter.next();
                PersonIdent author = commit.getAuthorIdent();
                metaData.getAuthors().add(author.getName());
                var timeStamp = ZonedDateTime.ofInstant(author.getWhenAsInstant(), author.getZoneId());
                if (counter == 0) {
                    metaData.setLatestTime(timeStamp);
                    metaData.setInitialDate(timeStamp);
                } else if (!iter.hasNext()) {
                    metaData.setInitialDate(timeStamp);
                }
                counter++;
            }
            return metaData;
        } else
            return new FileMetaData();
    }

    public ObjectId getObjectId(String reference) throws IOException {
        if (git != null) {
//            var git = gitOptional.get();

            return git.getRepository().resolve(reference);
        } else
            return null;
    }

    public GitHelper(Git git, CredentialsProvider credProv, AppConfigRepo appConfigRepo) {
        this.credProv = credProv;
        this.git = git;
        this.appConfigRepo = appConfigRepo;
    }

    public File getRepoRootDirectory () {
        if (git != null) {
//            var git = git;
            return git.getRepository().getWorkTree();
        } else
            return null;
    }



}
