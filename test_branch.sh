#!/bin/bash
#Tests all commits between master and HEAD, non-inclusive
branch_name="$(git branch | grep \* | cut -d ' ' -f 2)"
for i in $(git log master..HEAD^ --pretty=%H | cat -n | sort -nr -k1 | cut -f2)
do
    if git checkout -q $i \
        && mvn -B -q clean install >/dev/null
    then
        echo "$i OK"
    else
        echo "$i FAILED"
        break
    fi
done
git checkout "$branch_name"

