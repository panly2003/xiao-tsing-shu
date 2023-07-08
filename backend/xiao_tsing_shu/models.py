from typing import Iterable, Optional
from django.db import models
from django.utils import timezone
from django.conf import settings
import os

class User(models.Model):
    id = models.CharField(primary_key=True, max_length=64)
    name = models.CharField(max_length=64)
    password = models.CharField(max_length=64)
    profile = models.CharField(max_length=64)
    avatar = models.FileField(upload_to='user_avatar/%Y%m%d', blank=True)
    def __str__(self):
        return self.id[:10] + " " + self.name[:10]
    @classmethod
    def create(cls, id, name, password):
        if id == "":
            raise ValueError("用户ID不能为空")
        if name == "":
            raise ValueError("用户名不能为空")
        if password == "":
            raise ValueError("密码不能为空")
        user = cls(id=id, name=name, password=password, profile="", avatar="")
        return user
    def getavatar(self):
        if self.avatar:
            return os.path.join(settings.MEDIA_URL, str(self.avatar))
        else:
            return os.path.join(settings.MEDIA_URL, 'user_avatar/default.png')

class Follow(models.Model):
    user_id = models.CharField(max_length=64, db_index=True)
    follow_id = models.CharField(max_length=64, db_index=True)
    time = models.DateTimeField(default=timezone.now)
    class Meta:
        constraints = [
            models.UniqueConstraint(fields=['user_id', 'follow_id'], name='unique_follow')
        ]
        ordering = ['-time']
    def __str__(self):
        return self.user_id[:10] + " " + self.follow_id[:10]
    @classmethod
    def create(cls, user_id, follow_id):
        follow = cls(user_id=user_id, follow_id=follow_id)
        return follow

class Block(models.Model):
    user_id = models.CharField(max_length=64, db_index=True)
    block_id = models.CharField(max_length=64, db_index=True)
    time = models.DateTimeField(default=timezone.now)
    class Meta:
        constraints = [
            models.UniqueConstraint(fields=['user_id', 'block_id'], name='unique_block')
        ]
        ordering = ['-time']
    def __str__(self):
        return self.user_id[:10] + " " + self.block_id[:10]
    @classmethod
    def create(cls, user_id, block_id):
        block = cls(user_id=user_id, block_id=block_id)
        return block
    
class Chat(models.Model):
    id = models.AutoField(primary_key=True)
    user_id = models.CharField(max_length=64, db_index=True)
    chat_id = models.CharField(max_length=64, db_index=True)
    content = models.CharField(max_length=256)
    time = models.DateTimeField(default=timezone.now)
    read = models.BooleanField(default=False)
    class Meta:
        ordering = ['time']
    def __str__(self):
        return self.user_id[:10] + " " + self.chat_id[:10] + " " + self.content[:10]
    @classmethod
    def create(cls, user_id, chat_id, content):
        chat = cls(user_id=user_id, chat_id=chat_id, content=content)
        return chat
    
class Media(models.Model):
    media = models.FileField(upload_to='article_media/%Y%m%d')
    def __str__(self):
        return str(self.media)
    @classmethod
    def create(cls, media):
        media = cls(media=media)
        return media
    
class Article(models.Model):
    id = models.AutoField(primary_key=True)
    user_id = models.CharField(max_length=64, db_index=True)
    title = models.CharField(max_length=64)
    text = models.CharField(max_length=1024)
    media = models.JSONField(default=list)
    cover = models.FileField(upload_to='article_cover/%Y%m%d')
    address = models.CharField(max_length=64)
    type = models.CharField(max_length=64)
    time = models.DateTimeField(default=timezone.now)
    likes = models.IntegerField(default=0)
    stars = models.IntegerField(default=0)
    comments = models.IntegerField(default=0)
    hot = models.FloatField(db_index=True, default=0)
    class Meta:
        ordering = ['-time']
    def __str__(self):
        return str(self.id) + " " + self.user_id[:10] + " " + self.title[:10]
    @classmethod
    def create(cls, user_id, title, text, media, cover, address, type):
        article = cls(user_id=user_id, title=title, text=text, media=media, cover=cover, address=address, type=type)
        return article
    def save(self):
        self.hot = self.likes + self.stars * 2 + self.comments + self.time.timestamp() / 3600
        super().save()
    def getcover(self):
        if self.cover:
            return os.path.join(settings.MEDIA_URL, str(self.cover))
        else:
            return ""
    
class Like(models.Model):
    user_id = models.CharField(max_length=64, db_index=True)
    article_id = models.IntegerField(db_index=True)
    time = models.DateTimeField(default=timezone.now)
    class Meta:
        constraints = [
            models.UniqueConstraint(fields=['user_id', 'article_id'], name='unique_like')
        ]
        ordering = ['-time']
    def __str__(self):
        return self.user_id[:10] + " " + str(self.article_id)
    @classmethod
    def create(cls, user_id, article_id):
        like = cls(user_id=user_id, article_id=article_id)
        return like

class Star(models.Model):
    user_id = models.CharField(max_length=64, db_index=True)
    article_id = models.IntegerField(db_index=True)
    time = models.DateTimeField(default=timezone.now)
    class Meta:
        constraints = [
            models.UniqueConstraint(fields=['user_id', 'article_id'], name='unique_star')
        ]
        ordering = ['-time']
    def __str__(self):
        return self.user_id[:10] + " " + str(self.article_id)
    @classmethod
    def create(cls, user_id, article_id):
        star = cls(user_id=user_id, article_id=article_id)
        return star
    
class Comment(models.Model):
    id = models.AutoField(primary_key=True)
    user_id = models.CharField(max_length=64, db_index=True)
    article_id = models.IntegerField(db_index=True)
    content = models.CharField(max_length=256)
    time = models.DateTimeField(default=timezone.now)
    class Meta:
        ordering = ['-time']
    def __str__(self):
        return self.user_id[:10] + " " + str(self.article_id) + " " + self.content[:10]
    @classmethod
    def create(cls, user_id, article_id, content):
        comment = cls(user_id=user_id, article_id=article_id, content=content)
        return comment
    
class Notice(models.Model):
    id = models.AutoField(primary_key=True)
    user_id = models.CharField(max_length=64, db_index=True)
    content = models.CharField(max_length=256)
    def __str__(self):
        return self.user_id[:10] + " " + self.content[:10]
    @classmethod
    def create(cls, user_id, content):
        notice = cls(user_id=user_id, content=content)
        return notice