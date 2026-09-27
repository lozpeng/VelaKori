# 添加Remote Url
git remote add upstream https://github.com/PimpinPumpkin/Vela.git
## 禁用upstream的push避免把本地修改push到upstream
git remote set-url --push upstream no_push

# 更新代码
要把 upstream/main 合并到当前本地分支，先执行 git fetch upstream，然后直接运行 git merge upstream/main。‌‌

具体步骤如下：

‌获取上游更新‌：git fetch upstream canary
‌切换到目标本地分支‌：git checkout <local-branch>（确保在要合并的分支上）
‌合并上游分支‌：git merge upstream/main
如果遇到冲突，手动解决后 git add 再 git commit；不想要这次合并可以 git merge --abort 回滚。‌‌

如果当前本地分支还没有配置 upstream 远程仓库，先添加：git remote add upstream <原始仓库URL>，再用 git remote -v 确认。‌‌


