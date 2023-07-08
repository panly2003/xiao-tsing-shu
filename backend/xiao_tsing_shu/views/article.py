from django.http import JsonResponse
from django.db import IntegrityError
from django.utils.datastructures import MultiValueDictKeyError
from django.utils import timezone
from django.conf import settings
import os

from ..models import User, Follow, Block, Article, Comment, Star, Media

def article_post(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        user = User.objects.get(id=user_id)
        title = request.POST['title']
        text = request.POST.get('text', '')
        cover = request.FILES.get('cover', None)
        address = request.POST.get('address', '')
        type = request.POST.get('type', '')
        media_num = int(request.POST.get('media_num', 0))
        media = []
        for i in range(media_num):
            article_media = Media.create(request.FILES['media' + str(i)])
            article_media.save()
            media.append(os.path.join(settings.MEDIA_URL, str(article_media.media)))
        if not (text or media or cover):
            res['status'] = "error"
            res['message'] = "文章内容不能为空"
        else:
            article = Article.create(user_id, title, text, media, cover, address, type)
            article.save()
            res['status'] = "success"
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "该用户不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def article_get(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        article_id = request.POST['article_id']
        article = Article.objects.get(id=article_id)
        res['status'] = "success"
        res['user_id'] = article.user_id
        res['user_name'] = User.objects.get(id=article.user_id).name
        res['user_avatar'] = User.objects.get(id=article.user_id).getavatar()
        res['title'] = article.title
        res['text'] = article.text
        res['media'] = article.media
        res['address'] = article.address
        res['type'] = article.type
        res['time'] = timezone.localtime(article.time).strftime("%Y-%m-%d %H:%M:%S")
        res['likes'] = article.likes
        res['stars'] = article.stars
        res['comments'] = article.comments
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, Article.DoesNotExist):
            res['status'] = "error"
            res['message'] = "该文章不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

order_dict = {
    'time': '-time',
    'likes': '-likes',
    'stars': '-stars',
    'comments': '-comments',
    'hot': '-hot',
    '-time': 'time',
    '-likes': 'likes',
    '-stars': 'stars',
    '-comments': 'comments',
    '-hot': 'hot',
}

def article_user(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        order = request.POST.get('order', 'time')
        user = User.objects.get(id=user_id)
        articles = Article.objects.filter(user_id=user_id).order_by(order_dict[order])
        res['status'] = "success"
        res['articles'] = []
        for article in articles:
            res['articles'].append({
                'article_id': article.id,
                'user_id': article.user_id,
                'user_name': User.objects.get(id=article.user_id).name,
                'user_avatar': User.objects.get(id=article.user_id).getavatar(),
                'title': article.title,
                'text': article.text,
                'cover': article.getcover(),
                'address': article.address,
                'type': article.type,
                'time': timezone.localtime(article.time).strftime("%Y-%m-%d %H:%M:%S"),
                'likes': article.likes,
                'stars': article.stars,
                'comments': article.comments,
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "该用户不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def article_star(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        user = User.objects.get(id=user_id)
        stars = Star.objects.filter(user_id=user_id)
        res['status'] = "success"
        res['articles'] = []
        for star in stars:
            article = Article.objects.get(id=star.article_id)
            res['articles'].append({
                'article_id': article.id,
                'user_id': article.user_id,
                'user_name': User.objects.get(id=article.user_id).name,
                'user_avatar': User.objects.get(id=article.user_id).getavatar(),
                'title': article.title,
                'text': article.text,
                'cover': article.getcover(),
                'address': article.address,
                'type': article.type,
                'time': timezone.localtime(article.time).strftime("%Y-%m-%d %H:%M:%S"),
                'likes': article.likes,
                'stars': article.stars,
                'comments': article.comments,
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "该用户不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def article_follow(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        order = request.POST.get('order', 'time')
        user = User.objects.get(id=user_id)
        follows = Follow.objects.filter(user_id=user_id)
        follow_set = {follow.follow_id for follow in follows}
        articles = Article.objects.filter(user_id__in=follow_set).order_by(order_dict[order])
        res['status'] = "success"
        res['articles'] = []
        for article in articles:
            res['articles'].append({
                'article_id': article.id,
                'user_id': article.user_id,
                'user_name': User.objects.get(id=article.user_id).name,
                'user_avatar': User.objects.get(id=article.user_id).getavatar(),
                'title': article.title,
                'text': article.text,
                'cover': article.getcover(),
                'address': article.address,
                'type': article.type,
                'time': timezone.localtime(article.time).strftime("%Y-%m-%d %H:%M:%S"),
                'likes': article.likes,
                'stars': article.stars,
                'comments': article.comments,
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "该用户不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def article_type(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        order = request.POST.get('order', 'time')
        user = User.objects.get(id=user_id)
        blocks = Block.objects.filter(user_id=user_id)
        block_set = {block.block_id for block in blocks}
        type = request.POST['type']
        articles = Article.objects.filter(type=type).order_by(order_dict[order])
        res['status'] = "success"
        res['articles'] = []
        for article in articles:
            if article.user_id in block_set:
                continue
            res['articles'].append({
                'article_id': article.id,
                'user_id': article.user_id,
                'user_name': User.objects.get(id=article.user_id).name,
                'user_avatar': User.objects.get(id=article.user_id).getavatar(),
                'title': article.title,
                'text': article.text,
                'cover': article.getcover(),
                'address': article.address,
                'type': article.type,
                'time': timezone.localtime(article.time).strftime("%Y-%m-%d %H:%M:%S"),
                'likes': article.likes,
                'stars': article.stars,
                'comments': article.comments,
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def article_comments(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        article_id = request.POST['article_id']
        article = Article.objects.get(id=article_id)
        comments = Comment.objects.filter(article_id=article_id)
        res['status'] = "success"
        res['comments'] = []
        for comment in comments:
            res['comments'].append({
                'user_id': comment.user_id,
                'user_name': User.objects.get(id=comment.user_id).name,
                'user_avatar': User.objects.get(id=comment.user_id).getavatar(),
                'content': comment.content,
                'time': timezone.localtime(comment.time).strftime("%Y-%m-%d %H:%M:%S"),
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, Article.DoesNotExist):
            res['status'] = "error"
            res['message'] = "该文章不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def article_hot(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        order = request.POST.get('order', 'hot')
        user = User.objects.get(id=user_id)
        blocks = Block.objects.filter(user_id=user_id)
        block_set = {block.block_id for block in blocks}
        articles = Article.objects.order_by(order_dict[order])
        res['status'] = "success"
        res['articles'] = []
        for article in articles:
            if article.user_id in block_set:
                continue
            res['articles'].append({
                'article_id': article.id,
                'user_id': article.user_id,
                'user_name': User.objects.get(id=article.user_id).name,
                'user_avatar': User.objects.get(id=article.user_id).getavatar(),
                'title': article.title,
                'text': article.text,
                'cover': article.getcover(),
                'address': article.address,
                'type': article.type,
                'time': timezone.localtime(article.time).strftime("%Y-%m-%d %H:%M:%S"),
                'likes': article.likes,
                'stars': article.stars,
                'comments': article.comments,
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def article_search(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        order = request.POST.get('order', 'time')
        user = User.objects.get(id=user_id)
        blocks = Block.objects.filter(user_id=user_id)
        block_set = {block.block_id for block in blocks}
        keywords = request.POST['words']
        keywords = keywords.strip().split()
        articles = Article.objects.order_by(order_dict[order])
        res['status'] = "success"
        res['articles'] = []
        for article in articles:
            if article.user_id in block_set:
                continue
            content = User.objects.get(id=article.user_id).name + article.title + article.text + article.address + article.type
            for keyword in keywords:
                if keyword not in content:
                    break
            else:
                res['articles'].append({
                    'article_id': article.id,
                    'user_id': article.user_id,
                    'user_name': User.objects.get(id=article.user_id).name,
                    'user_avatar': User.objects.get(id=article.user_id).getavatar(),
                    'title': article.title,
                    'text': article.text,
                    'cover': article.getcover(),
                    'address': article.address,
                    'type': article.type,
                    'time': timezone.localtime(article.time).strftime("%Y-%m-%d %H:%M:%S"),
                    'likes': article.likes,
                    'stars': article.stars,
                    'comments': article.comments,
                })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})