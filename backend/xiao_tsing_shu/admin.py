from django.contrib import admin

# Register your models here.
from .models import User, Follow, Block, Chat, Media, Article, Comment, Like, Star, Notice

admin.site.register(User)
admin.site.register(Follow)
admin.site.register(Block)
admin.site.register(Chat)
admin.site.register(Media)
admin.site.register(Article)
admin.site.register(Comment)
admin.site.register(Like)
admin.site.register(Star)
admin.site.register(Notice)