package com.wilcotech.dataextractor.core.core.domain.enums;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public enum CMD {
    BRANCH_COMMITS(" git log --oneline --format='firstinfo: author_name: %an **** author_email: %ae **** commit_hash: %H **** author_date: %ad' --date=iso"),
    DEVELOPER_WHO_ADDED_A_FILE("git log --diff-filter=A -- "),//followed by file url
    ALL_GIT_MANAGED_FILES("git ls-files"),
    ALL_REMOTE_COMMITS_IN_ALL_BRANCHES("git rev-list --all --remotes --pretty --format='%an * %ae * %ad * %H' --date=iso"),
    ALL_LOCAL_COMMITS_IN_ALL_BRANCHES("git rev-list --all --remotes --pretty --format='%an * %ae * %ad * %H' --date=iso"),
    ALL_BRANCH_DEVS("git shortlog -sne"),
    ALL_FILES_IN_A_COMMIT(" git diff-tree --no-commit-id --name-only -r "),//followed by a commit id
    LIST_LOCAL_AND_REMOTE_BRANCHES("git branch -a"),
    LIST_ONLY_REMOTE_BRANCHES("git branch -r"),
    INSIDE_WORK_DIR_CMD("git rev-parse --is-inside-work-tree"),//insdie the folder that has .git or any of the folder's subdir
    INSIDE_GIT_DIR("git rev-parse --is-inside-git-dir"),//insdie the .git folder or its subdir
    SUMMARY_COMMIT_INFO_ON_A_FILE(" git log --format='firstinfo: author_name: %an **** author_email: %ae **** commit_hash: %H **** author_date: %ad' --date=iso "),//followed by -- and a file url
    FIRST_CHANGE_MADE_ON_COMMIT_OR_FILE("git log -p --diff-filter=A --format='infomarkerpoint: %an * %ae * %ad' --date=iso "),//followed by commid hash or "--" and a file url
    MOST_RECENT_CHANGE_MADE_ON_COMMIT_OR_FILE("git log -p -1 --format='infomarkerpoint: %an * %ae * %ad' --date=iso "),//followed by commid hash or "--" and a file url
    ALL_CHANGES_MADE_ON_A_FILE("git log -p --format='infomarkerpoint: %an * %ae * %ad' ");//followed by -- and file url OR COMMIT HASH

    private String command;
    private CMD(String command){
        this.command = command;
    }
    public String getCommand(){return this.command;}
}
