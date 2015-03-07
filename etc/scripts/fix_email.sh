git filter-branch --env-filter 'if [ $GIT_COMMITTER_NAME = fortuna ]; then GIT_COMMITTER_EMAIL=fortuna@users.sourceforge.net; fi; export GIT_COMMITTER_EMAIL' -f -- --all

git filter-branch --env-filter 'if [ $GIT_AUTHOR_NAME = fortuna ]; then GIT_AUTHOR_EMAIL=fortuna@users.sourceforge.net; fi; export GIT_AUTHOR_EMAIL' -f -- --all

