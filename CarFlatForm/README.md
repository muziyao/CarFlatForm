https://www.w3cschool.cn/git/git-workspace-index-repo.html 

【哪个死仔把git的stage翻译为暂存区】
【stage = index 】对应的是.git/index文件。
【索引是名词也是动词】
【整个.git隐藏目录就是版本库】
【工作区就是本地仓库，是一个文件夹，也就是.git所在的目录】
【objects 是一个对象库，存储实际的内容】index只是记录索引，但objects则是作为实际存储的目录。
  GIT的内容存储使用的是SHA-1哈希算法，所以打开objects目录会发现其子目录和文件名都是一些hash值。
【index和objects的关系是，index记录了索引，objects负责实际的存储。】
index是银行卡，objects银行里的钱。拿着index到objects肯定找到对应的东西。
index是一大串钥匙，objects里是房间，拿着钥匙去开对应的房门，肯定去得出房间里的宝藏。

【Unstaged changes after reset】重置后未临时缓存的改变。
【添加远程仓库地址】 git remote add  remote_namexxx  [https or ssh url]
【查看添加的远程仓库地址】git remote -v 
【删除添加的远程仓库地址】git remote remove 添加的名字

【从远程仓库拉取并合并】 
	克隆而来的仓库，直接git pull  
	本地创建的仓库，要从远程仓库拉取需要指定名字。git pull remote_xxxname  





