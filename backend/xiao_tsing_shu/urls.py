from django.urls import path, re_path

from .views import user, user_interact, chat, article, article_interact, notice

urlpatterns = [
    # 用户
    path("user-signup", user.user_signup, name="user_signup"),
    path("user-login", user.user_login, name="user_login"),
    path("user-getname", user.user_getname, name="user_getname"),
    path("user-setname", user.user_setname, name="user_setname"),
    path("user-setpassword", user.user_setpassword, name="user_setpassword"),
    path("user-getprofile", user.user_getprofile, name="user_getprofile"),
    path("user-setprofile", user.user_setprofile, name="user_setprofile"),
    path("user-getavatar", user.user_getavatar, name="user_getavatar"),
    path("user-setavatar", user.user_setavatar, name="user_setavatar"),
    path("user-getinfo", user.user_getinfo, name="user_getinfo"),
    # 用户交互
    path("user-follow", user_interact.user_follow, name="user_follow"),
    path("user-checkfollow", user_interact.user_checkfollow, name="user_checkfollow"),
    path("user-unfollow", user_interact.user_unfollow, name="user_unfollow"),
    path("user-getfollowing", user_interact.user_getfollowing, name="user_getfollowing"),
    path("user-getfollower", user_interact.user_getfollower, name="user_getfollower"),
    path("user-block", user_interact.user_block, name="user_block"),
    path("user-checkblock", user_interact.user_checkblock, name="user_checkblock"),
    path("user-unblock", user_interact.user_unblock, name="user_unblock"),
    path("user-getblock", user_interact.user_getblock, name="user_getblock"),
    path("user-checkrelation", user_interact.user_checkrelation, name="user_checkrelation"),
    # 私信
    path("chat-send", chat.chat_send, name="chat_send"),
    path("chat-get", chat.chat_get, name="chat_get"),
    path("chat-unread", chat.chat_unread, name="chat_unread"),
    path("chat-list", chat.chat_list, name="chat_list"),
    # 文章
    path("article-post", article.article_post, name="article_post"),
    path("article-get", article.article_get, name="article_get"),
    path("article-user", article.article_user, name="article_user"),
    path("article-star", article.article_star, name="article_star"),
    path("article-follow", article.article_follow, name="article_follow"),
    path("article-type", article.article_type, name="article_type"),
    path("article-comments", article.article_comments, name="article_comments"),
    path("article-hot", article.article_hot, name="article_hot"),
    path("article-search", article.article_search, name="article_search"),
    # 文章操作
    path("like", article_interact.like, name="like"),
    path("checklike", article_interact.checklike, name="checklike"),
    path("unlike", article_interact.unlike, name="unlike"),
    path("star", article_interact.star, name="star"),
    path("checkstar", article_interact.checkstar, name="checkstar"),
    path("unstar", article_interact.unstar, name="unstar"),
    path("comment", article_interact.comment, name="comment"),
    # 通知
    path("notice-get", notice.notice_get, name="notice_get"),
]