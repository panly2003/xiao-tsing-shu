from django.http import JsonResponse
from django.db import IntegrityError
from django.utils.datastructures import MultiValueDictKeyError

from ..models import User, Chat

def user_signup(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        name = request.POST['user_name']
        password = request.POST['user_password']
        try:
            user = User.objects.get(id=id)
            res['status'] = "error"
            res['message'] = "该用户ID已存在"
        except:
            user = User.create(id, name, password)
            user.save()
            chat = Chat.create('system', id, "欢迎来到小清书")
            chat.save()
            res['status'] = "success"
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_login(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        password = request.POST['user_password']
        user = User.objects.get(id=id)
        if user.password != password:
            res['status'] = "error"
            res['message'] = "密码错误"
        else:
            res['status'] = "success"
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        elif isinstance(e, User.DoesNotExist):
            res['status'] = "error"
            res['message'] = "该用户ID不存在"
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def user_getname(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        user = User.objects.get(id=id)
        res['status'] = "success"
        res['user_name'] = user.name
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

def user_setname(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        new_name = request.POST['user_new_name']
        user = User.objects.get(id=id)
        user.name = new_name
        user.save()
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

def user_setpassword(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        password = request.POST['user_password']
        new_password = request.POST['user_new_password']
        user = User.objects.get(id=id)
        if user.password != password:
            res['status'] = "error"
            res['message'] = "原密码错误"
        else:
            user.password = new_password
            user.save()
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

def user_getprofile(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        user = User.objects.get(id=id)
        res['status'] = "success"
        res['user_profile'] = user.profile
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

def user_setprofile(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        new_profile = request.POST['user_new_profile']
        user = User.objects.get(id=id)
        user.profile = new_profile
        user.save()
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

def user_getavatar(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        user = User.objects.get(id=id)
        res['status'] = "success"
        res['user_avatar'] = user.getavatar()
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

def user_setavatar(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        new_avatar = request.FILES['user_new_avatar']
        user = User.objects.get(id=id)
        user.avatar = new_avatar
        user.save()
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

def user_getinfo(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        id = request.POST['user_id']
        user = User.objects.get(id=id)
        res['status'] = "success"
        res['user_name'] = user.name
        res['user_avatar'] = user.getavatar()
        res['user_profile'] = user.profile
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