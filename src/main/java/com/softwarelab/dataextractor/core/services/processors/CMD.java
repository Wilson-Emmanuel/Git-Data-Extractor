package com.softwarelab.dataextractor.core.services.processors;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public enum CMD {
    BRANCH_COMMITS(" git log --oneline --format='firstinfo: %an **** %ae **** %H **** %ad' --date=iso"),
    DEVELOPER_WHO_ADDED_A_FILE("git log --diff-filter=A -- "),//followed by file url
    ALL_GIT_MANAGED_FILES("git ls-files"),
    DOWNLOAD_PROJECT("git clone "),
    ALL_REMOTE_COMMITS_IN_ALL_BRANCHES("git rev-list --all --remotes --pretty --format='firstinfo: %an **** %ae **** %H **** %ad' --date=iso"),
    ALL_LOCAL_COMMITS_IN_ALL_BRANCHES("git rev-list --all --pretty --format=\"gjdea_firstinfo: %an **** %ae **** %H **** %aI\" "),
    ALL_LOCAL_COMMITS_IN_ALL_BRANCHES_REVERSED("git rev-list --all --pretty --reverse --format='firstinfo: %an **** %ae **** %H **** %ad' --date=iso"),
    ALL_BRANCH_DEVS("git shortlog -sne"),
    ALL_FILES_IN_A_COMMIT(" git diff-tree --no-commit-id --name-only -r "),//followed by a commit id
    LIST_LOCAL_AND_REMOTE_BRANCHES("git branch -a"),
    LIST_ONLY_REMOTE_BRANCHES("git branch -r"),
    INSIDE_WORK_DIR_CMD("git rev-parse --is-inside-work-tree"),//insdie the folder that has .git or any of the folder's subdir
    INSIDE_GIT_DIR("git rev-parse --is-inside-git-dir"),//insdie the .git folder or its subdir
    SUMMARY_COMMIT_INFO_ON_A_FILE(" git log --format='firstinfo: %an **** %ae **** %H **** %ad' --date=iso "),//followed by -- and a file url
    FIRST_CHANGE_MADE_ON_COMMIT_OR_FILE("git log -p --diff-filter=A --format='infomarkerpoint: %an * %ae * %ad' --date=iso "),//followed by commid hash or "--" and a file url
    MOST_RECENT_CHANGE_MADE_ON_COMMIT_OR_FILE("git log -p -1 --format='gjdea_firstinfo: %an * %ae * %ad' --date=iso "),//followed by commid hash or "--" and a file url
    //ALL_CHANGES_MADE_ON_A_FILE("git log -p --reverse --format=\"gjdea_firstinfo: %an **** %ae **** %H **** %aI\" -- ");//followed by -- and file url OR COMMIT HASH

    //The contributors graph (example) shows committers, but not authors if the author is different from the committer. Could it be updated to show both?
    ALL_CHANGES_MADE_ON_A_FILE("git log -p --reverse --format=\"gjdea_firstinfo: %cn **** %ce **** %an **** %ae **** %H **** %cI **** %aI\" -- ");//followed by -- and file url OR COMMIT HASH

    private String command;
    private CMD(String command){
        this.command = command;
    }
    public String getCommand(){return this.command;}
}
