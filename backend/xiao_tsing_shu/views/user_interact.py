from django.http import JsonResponse
from django.db import IntegrityError
from django.utils.datastructures import MultiValueDictKeyError

from ..models import User, Follow, Block, Notice, Chat

def user_follow(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        follower_id = request.POST['user_id']
        following_id = request.POST['user_follow_id']
        follower = User.objects.get(id=follower_id)
        following = User.objects.get(id=following_id)
        if follower == following:
            res['status'] = "error"
            res['message'] = "不能关注自己"
        else:
            try:
                follow = Follow.objects.get(user_id=follower_id, follow_id=following_id)
                follow.delete()
                res['status'] = "success"
                res['followed'] = 0
                return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
            except:
                pass
            follow = Follow.create(follower_id, following_id)
            follow.save()
            res['status'] = "success"
            res['followed'] = 1
            notice = Notice.create(following_id, f'{follower.name} 关注了您')
            notice.save()
            chat = Chat.create('system', following_id, f'{follower.name} 关注了您')
            chat.save()
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, IntegrityError):
            res['status'] = "error"
            res['message'] = "已关注"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_checkfollow(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        follower_id = request.POST['user_id']
        following_id = request.POST['user_check_id']
        follower = User.objects.get(id=follower_id)
        following = User.objects.get(id=following_id)
        if follower == following:
            res['status'] = "error"
            res['message'] = "不能关注自己"
        else:
            follow = Follow.objects.get(user_id=follower_id, follow_id=following_id)
            res['status'] = "success"
            res['followed'] = 1
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Follow.DoesNotExist):
            res['status'] = "success"
            res['followed'] = 0
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_unfollow(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        follower_id = request.POST['user_id']
        following_id = request.POST['user_unfollow_id']
        follower = User.objects.get(id=follower_id)
        following = User.objects.get(id=following_id)
        if follower == following:
            res['status'] = "error"
            res['message'] = "不能取关自己"
        else:
            follow = Follow.objects.get(user_id=follower_id, follow_id=following_id)
            follow.delete()
            res['status'] = "success"
            res['followed'] = 0
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Follow.DoesNotExist):
            res['status'] = "error"
            res['message'] = "未关注"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_getfollowing(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        user = User.objects.get(id=user_id)
        followings = Follow.objects.filter(user_id=user_id)
        res['status'] = "success"
        res['followinglist'] = []
        for following in followings:
            user = User.objects.get(id=following.follow_id)
            res['followinglist'].append({
                'user_id': user.id,
                'user_name': user.name,
                'user_avatar': user.getavatar(),
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_getfollower(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        user = User.objects.get(id=user_id)
        followers = Follow.objects.filter(follow_id=user_id)
        res['status'] = "success"
        res['followerlist'] = []
        for follower in followers:
            user = User.objects.get(id=follower.user_id)
            res['followerlist'].append({
                'user_id': user.id,
                'user_name': user.name,
                'user_avatar': user.getavatar(),
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_block(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        block_id = request.POST['user_block_id']
        user = User.objects.get(id=user_id)
        block = User.objects.get(id=block_id)
        if user == block:
            res['status'] = "error"
            res['message'] = "不能拉黑自己"
        else:
            try:
                block = Block.objects.get(user_id=user_id, block_id=block_id)
                block.delete()
                res['status'] = "success"
                res['blocked'] = 0
                return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
            except:
                pass
            block = Block.objects.create(user_id=user_id, block_id=block_id)
            block.save()
            res['status'] = "success"
            res['blocked'] = 1
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, IntegrityError):
            res['status'] = "error"
            res['message'] = "已拉黑"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_checkblock(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        block_id = request.POST['user_check_id']
        user = User.objects.get(id=user_id)
        block = User.objects.get(id=block_id)
        if user == block:
            res['status'] = "error"
            res['message'] = "不能拉黑自己"
        else:
            block = Block.objects.get(user_id=user_id, block_id=block_id)
            res['status'] = "success"
            res['blocked'] = 1
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Block.DoesNotExist):
            res['status'] = "success"
            res['blocked'] = 0
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_unblock(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        block_id = request.POST['user_unblock_id']
        user = User.objects.get(id=user_id)
        block = User.objects.get(id=block_id)
        if user == block:
            res['status'] = "error"
            res['message'] = "不能拉黑自己"
        else:
            block = Block.objects.get(user_id=user_id, block_id=block_id)
            block.delete()
            res['status'] = "success"
            res['blocked'] = 0
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        elif isinstance(e, Block.DoesNotExist):
            res['status'] = "error"
            res['message'] = "未拉黑"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_getblock(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        user = User.objects.get(id=user_id)
        blocks = Block.objects.filter(user_id=user_id)
        res['status'] = "success"
        res['blocklist'] = []
        for block in blocks:
            user = User.objects.get(id=block.block_id)
            res['blocklist'].append({
                'user_id': user.id,
                'user_name': user.name,
                'user_avatar': user.getavatar(),
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_checkrelation(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        check_id = request.POST['user_check_id']
        user = User.objects.get(id=user_id)
        check = User.objects.get(id=check_id)
        if user == check:
            res['status'] = "error"
            res['message'] = "不能查看自己"
        else:
            try:
                follow = Follow.objects.get(user_id=user_id, follow_id=check_id)
                res['status'] = "success"
                res['relation'] = 1
                return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
            except:
                pass
            try:
                block = Block.objects.get(user_id=user_id, block_id=check_id)
                res['status'] = "success"
                res['relation'] = -1
                return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
            except:
                pass
            res['status'] = "success"
            res['relation'] = 0
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "用户不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})