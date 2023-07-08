# “小清书”API文档

### 用户


- 注册

```
POST: /user-signup
request{
    "user_id":,
    "user_name":,
    "user_password":,
}
response{
    "status":,		// 状态：ok/error
    "message":,		// 错误信息
}
```

- 登录

```
POST: /user-login
request{
    "user_id":,
    "user_password":,
}
response{
    "status":,		// 状态：ok/error
    "message":,		// 错误信息
}
```

- 获取用户名

```
POST: /user-getname
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 错误信息
    "user_name":,
}
```

- 修改用户名

```
POST: /user-setname
request{
    "user_id":,
    "user_new_name":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 错误信息
}
```

- 修改密码

```
POST: /user-setpassword
request{
    "user_id":,
    "user_password":,
    "user_new_password":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 获取简介

```
POST: /user-getprofile
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "user_profile":,
}
```

- 修改简介

```
POST: /user-setprofile
request{
    "user_id":,
    "user_new_profile":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 获取头像

```
POST: /user-getavatar
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "user_avatar":,
}
```

- 修改头像

```
POST: /user-setavatar
request{
    "user_id":,
    "user_new_avatar": FILE,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 获取信息

```
POST: /user-getinfo
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "user_name":,
    "user_avatar":,
    "user_profile":,
}
```


### 用户交互


- 关注用户

```
POST: /user-follow
request{
    "user_id":,
    "user_follow_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 是否已关注用户

```
POST: /user-checkfollow
request{
    "user_id":,
    "user_check_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "followed":,     // 1表示followed，0表示not
}
```

- 取消关注

```
POST: /user-unfollow
request{
    "user_id":,
    "user_unfollow_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 获取用户关注列表

```
POST: /user-getfollowing
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "followinglist":[{
        "user_id":,
        "user_name":,
        "user_avatar":,
    }],
}
```

- 获取用户粉丝列表

```
POST: /user-getfollower
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "followerlist":[{
        "user_id":,
        "user_name":,
        "user_avatar":,
    }],
}
```

- 屏蔽用户

```
POST: /user-block
request{
    "user_id":,
    "user_block_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 是否已屏蔽用户

```
POST: /user-checkblock
request{
    "user_id":,
    "user_check_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "blocked":,     // 1表示blocked，0表示not
}
```

- 取消屏蔽

```
POST: /user-unblock
request{
    "user_id":,
    "user_unblock_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 获取用户屏蔽列表

```
POST: /user-getblock
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "blocklist":[{
        "user_id":,
        "user_name":,
        "user_avatar":,
    }],
}
```

- 查看用户关系

```
POST: /user-checkrelation
request{
    "user_id":,
    "user_check_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "relation":,     // 1表示followed，-1表示blocked，0表示无关系
}
```


### 私信（包括通知）


- 获取私信用户列表（有对话的用户）

```
POST: /chat-list
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "chatlist":[{
        "user_id":,
        "user_name":,
        "user_avatar":,
        "last_time":,
        "last_content":,
        "unread_num":,
    }],
}
```


- 获取私信

```
POST: /chat-get
request{
    "user_id":,
    "user_chat_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "chat":[{
        "user":,	// 0代表我方，1代表对方
    	"user_name":,
    	"user_avatar":,
        "content":,
        "time":,
    }],
}
```


- 私信是否含未读

```
POST: /chat-unread
request{
    "user_id":,
    "user_chat_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "unread":,    // 1代表未读，0代表已读
}
```


- 发送私信

```
POST: /chat-send
request{
    "user_id":,
    "user_chat_id":,
    "content":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```



### 文章


- 发布

```
POST: /article-post
request{
    "user_id":,
    "title":,
    "text":,
    "address":,
    "type":,
    "cover": FILE,
    "media_num":,
    ["media0": FILE,]
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```


- 获取文章详细内容

```
POST: /article-get
request{
    "article_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "user_id":,
    "user_name":,
    "user_avatar":,
    "title":,
    "text":,
    "media": [],
    "address":,
    "type":,
    "time":,
    "likes":,
    "stars":,
    "comments":,
}
```


- 获取用户发布的文章

```
POST: /article-user
request{
    "user_id":,
    "order":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "articles":[{
        "article_id":,
    	"user_id":,
        "user_name":,
    	"user_avatar":,
        "title":,
        "text":,
        "cover":,
        "address":,
        "type":,
        "time":,
        "likes":,
        "stars":,
        "comments":,
	}]
}
```

- 获取用户收藏的文章

```
POST: /article-star
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "articles":[{
        "article_id":,
    	"user_id":,
        "user_name":,
    	"user_avatar":,
        "title":,
        "text":,
        "cover":,
        "address":,
        "type":,
        "time":,
        "likes":,
        "stars":,
        "comments":,
	}]
}
```

- 获取已关注发布者的文章

```
POST: /article-follow
request{
    "user_id":,
    "order":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "articles":[{
        "article_id":,
    	"user_id":,
        "user_name":,
    	"user_avatar":,
        "title":,
        "text":,
        "cover":,
        "address":,
        "type":,
        "time":,
        "likes":,
        "stars":,
        "comments":,
	}]
}
```

- 获取热门文章

```
POST: /article-hot
request{
    "user_id":,
    "order";,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "articles":[{
        "article_id":,
    	"user_id":,
        "user_name":,
    	"user_avatar":,
        "title":,
        "text":,
        "cover":,
        "address":,
        "type":,
        "time":,
        "likes":,
        "stars":,
        "comments":,
	}]
}
```


- 获取指定类型文章

```
POST: /article-type
request{
    "user_id":,
    "type":,
    "order":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "articles":[{
        "article_id":,
    	"user_id":,
        "user_name":,
    	"user_avatar":,
        "title":,
        "text":,
        "cover":,
        "address":,
        "type":,
        "time":,
        "likes":,
        "stars":,
        "comments":,
	}]
}
```

- 搜索文章

```
POST: /article-search
request{
    "user_id":,
    "words":[],
    "order":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "articles":[{
        "article_id":,
    	"user_id":,
        "user_name":,
    	"user_avatar":,
        "title":,
        "text":,
        "cover":,
        "address":,
        "type":,
        "time":,
        "likes":,
        "stars":,
        "comments":,
	}]
}
```

- 获取文章的评论

```
POST: /article-comments
request{
    "article_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "comments":[{
    	"user_id":,
    	"user_name":,
    	"user_avatar":,
    	"content":,
    	"time":,
	}]
}
```




### 文章操作


- 点赞

```
POST: /like
request{
    "user_id":,
    "article_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 是否已点赞

```
POST: /checklike
request{
    "user_id":,
    "article_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "liked":,		// 1代表liked，0代表not
}
```

- 取消点赞

```
POST: /unlike
request{
    "user_id":,
    "article_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 收藏

```
POST: /star
request{
    "user_id":,
    "article_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

- 是否已收藏

```
POST: /checkstar
request{
    "user_id":,
    "article_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "stared":,		// 1代表stared，0代表not
}
```

- 取消收藏

```
POST: /unstar
request{
    "user_id":,
    "article_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```


- 评论

```
POST: /comment
request{
    "user_id":,
    "article_id":,
    "content":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
}
```

### 通知

- 获取通知

```
POST: /notice-get
request{
    "user_id":,
}
response{
    "status":,      // 状态：ok/error
    "message":,     // 报错信息
    "notices":[]
}
```




### 附：数据库表（Out Of Date）

```
User {
	id primary key
	name unique
	password
	profile
	avatar
}

Follow {
	user_id primary key foreign key refer User
	follow_id primary key foreign key refer User
        time
}

Block {
	user_id primary key foreign key refer User
	block_id primary key foreign key refer User
        time
}

Chat {
	id primary key
	user_id foreign key refer User
	chat_id foreign key refer User
	content
	time
}

Article {
	id primary key
	user_id foreign key refer User
	title
	text
	picture
	video
	address
	type
	time
	likes
	stars
	comments
}

Like {
	user_id primary key foreign key refer User
	article_id primary key foreign key refer Article
}

Star {
	user_id primary key foreign key refer User
	article_id primary key foreign key refer Article
}

Comment {
	id primary key
	user_id foreign key refer User
	article_id foreign key refer Article
	content
	time
}
```



