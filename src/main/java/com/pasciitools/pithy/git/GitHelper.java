package com.pasciitools.pithy.git;

import com.pasciitools.pithy.config.GitConfg;
import com.pasciitools.pithy.model.FileMetaData;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Component
public class GitHelper implements Serializable {


    @Serial
    private static final long serialVersionUID = 8514080561431376157L;

    private final transient Git git;

    private transient GitConfg gitConfg;



    private final transient CredentialsProvider credProv;

    public FetchResult fetchUpdates () throws GitAPIException {

        FetchCommand fetch = git.fetch();
        if (!gitConfg.isUseSSH())
            fetch.setCredentialsProvider(credProv);
        fetch.setRemote("origin");
        return fetch.call();
    }

    public PullResult pullUpdates () throws GitAPIException {
        PullCommand pull = git.pull();
        if (!gitConfg.isUseSSH())
            pull.setCredentialsProvider(credProv);
        pull.setRemote("origin");
        return pull.call();
    }

    public List<DiffEntry> getDiffs (ObjectId oldHead, ObjectId newHead) throws IOException, GitAPIException {
        ObjectReader reader = git.getRepository().newObjectReader();
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, oldHead);
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, newHead);
        return git.diff()
                .setNewTree(newTreeIter)
                .setOldTree(oldTreeIter)
                .call();
    }

    public FileMetaData getOriginDate (String fileName) throws GitAPIException {
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
            }
            else if (!iter.hasNext()) {
                metaData.setInitialDate(timeStamp);
            }
            counter++;
        }
        return metaData;
    }

    public ObjectId getObjectId(String reference) throws IOException {
        return git.getRepository().resolve(reference);
    }

    public GitHelper(Git git, CredentialsProvider credProv, GitConfg gitConfg) {
        this.credProv = credProv;
        this.git = git;
        this.gitConfg = gitConfg;
    }

    public File getRepoRootDirectory () {
        return git.getRepository().getWorkTree();
    }
}
