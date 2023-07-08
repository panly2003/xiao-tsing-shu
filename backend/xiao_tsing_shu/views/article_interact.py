from django.http import JsonResponse
from django.db import IntegrityError
from django.utils.datastructures import MultiValueDictKeyError
from django.utils import timezone

from ..models import User, Article, Comment, Like, Star, Notice, Chat

def like(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        article_id = request.POST['article_id']
        user = User.objects.get(id=user_id)
        article = Article.objects.get(id=article_id)
        try:
            like = Like.objects.get(user_id=user_id, article_id=article_id)
            like.delete()
            article.likes -= 1
            article.save()
            res['status'] = "success"
            res['liked'] = 0
            return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
        except:
            pass
        like = Like.create(user_id, article_id)
        like.save()
        article.likes += 1
        article.save()
        res['status'] = "success"
        res['liked'] = 1
        notice = Notice.create(article.user_id, f'{user.name} 点赞了您的文章 {article.title}')
        notice.save()
        chat = Chat.create('system', article.user_id, f'{user.name} 点赞了您的文章 {article.title}')
        chat.save()
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Article.DoesNotExist):
            res['status'] = "error"
            res['message'] = "文章不存在"
        elif isinstance(e, IntegrityError):
            res['status'] = "error"
            res['message'] = "用户已点赞过文章"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def checklike(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        article_id = request.POST['article_id']
        user = User.objects.get(id=user_id)
        article = Article.objects.get(id=article_id)
        like = Like.objects.get(user_id=user_id, article_id=article_id)
        res['status'] = "success"
        res['liked'] = 1
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Article.DoesNotExist):
            res['status'] = "error"
            res['message'] = "文章不存在"
        elif isinstance(e, Like.DoesNotExist):
            res['status'] = "success"
            res['liked'] = 0
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def unlike(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        article_id = request.POST['article_id']
        user = User.objects.get(id=user_id)
        article = Article.objects.get(id=article_id)
        like = Like.objects.get(user_id=user_id, article_id=article_id)
        like.delete()
        article.likes -= 1
        article.save()
        res['status'] = "success"
        res['liked'] = 0
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Article.DoesNotExist):
            res['status'] = "error"
            res['message'] = "文章不存在"
        elif isinstance(e, Like.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户未点赞过文章"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def star(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        article_id = request.POST['article_id']
        user = User.objects.get(id=user_id)
        article = Article.objects.get(id=article_id)
        try:
            star = Star.objects.get(user_id=user_id, article_id=article_id)
            star.delete()
            article.stars -= 1
            article.save()
            res['status'] = "success"
            res['starred'] = 0
            return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
        except:
            pass
        star = Star.create(user_id, article_id)
        star.save()
        article.stars += 1
        article.save()
        res['status'] = "success"
        res['starred'] = 1
        notice = Notice.create(article.user_id, f'{user.name} 收藏了您的文章 {article.title}')
        notice.save()
        chat = Chat.create('system', article.user_id, f'{user.name} 收藏了您的文章 {article.title}')
        chat.save()
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Article.DoesNotExist):
            res['status'] = "error"
            res['message'] = "文章不存在"
        elif isinstance(e, IntegrityError):
            res['status'] = "error"
            res['message'] = "用户已收藏过文章"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def checkstar(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        article_id = request.POST['article_id']
        user = User.objects.get(id=user_id)
        article = Article.objects.get(id=article_id)
        star = Star.objects.get(user_id=user_id, article_id=article_id)
        res['status'] = "success"
        res['stared'] = 1
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Article.DoesNotExist):
            res['status'] = "error"
            res['message'] = "文章不存在"
        elif isinstance(e, Star.DoesNotExist):
            res['status'] = "success"
            res['stared'] = 0
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def unstar(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        article_id = request.POST['article_id']
        user = User.objects.get(id=user_id)
        article = Article.objects.get(id=article_id)
        star = Star.objects.get(user_id=user_id, article_id=article_id)
        star.delete()
        article.stars -= 1
        article.save()
        res['status'] = "success"
        res['starred'] = 0
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Article.DoesNotExist):
            res['status'] = "error"
            res['message'] = "文章不存在"
        elif isinstance(e, Star.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户未收藏过文章"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def comment(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        article_id = request.POST['article_id']
        content = request.POST['content']
        user = User.objects.get(id=user_id)
        article = Article.objects.get(id=article_id)
        comment = Comment.create(user_id, article_id, content)
        comment.save()
        article.comments += 1
        article.save()
        res['status'] = "success"
        notice = Notice.create(article.user_id, f'{user.name} 评论了您的文章 {article.title}')
        notice.save()
        chat = Chat.create('system', article.user_id, f'{user.name} 评论了您的文章 {article.title}')
        chat.save()
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Article.DoesNotExist):
            res['status'] = "error"
            res['message'] = "文章不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
