from django.http import JsonResponse
from django.db import IntegrityError
from django.utils.datastructures import MultiValueDictKeyError
from django.utils import timezone

from ..models import User, Chat, Notice

def chat_send(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        chat_id = request.POST['user_chat_id']
        content = request.POST['content']
        chat = Chat.create(user_id, chat_id, content)
        chat.save()
        res['status'] = "success"
        notice = Notice.create(chat_id, "您有一条新消息")
        notice.save()
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def chat_get(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        chat_id = request.POST['user_chat_id']
        chat_send = Chat.objects.filter(user_id=user_id, chat_id=chat_id)
        chat_receive = Chat.objects.filter(user_id=chat_id, chat_id=user_id)
        res['status'] = "success"
        res['chat'] = []
        for c in chat_send:
            res['chat'].append({
                'user': 0,
                'user_name': User.objects.get(id=user_id).name,
                'user_avatar': User.objects.get(id=user_id).getavatar(),
                'content': c.content,
                'time': timezone.localtime(c.time).strftime("%Y-%m-%d %H:%M:%S")
            })
        for c in chat_receive:
            c.read = True
            c.save()
            res['chat'].append({
                'user': 1,
                'user_name': User.objects.get(id=chat_id).name,
                'user_avatar': User.objects.get(id=chat_id).getavatar(),
                'content': c.content,
                'time': timezone.localtime(c.time).strftime("%Y-%m-%d %H:%M:%S")
            })
        res['chat'].sort(key=lambda x: x['time'])
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def chat_unread(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        chat_id = request.POST['user_chat_id']
        chat_receive = Chat.objects.filter(user_id=chat_id, chat_id=user_id)
        res['status'] = "success"
        res['unread'] = 0
        for c in chat_receive:
            if not c.read:
                res['unread'] = 1
                return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})

def chat_list(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        chat_send = Chat.objects.filter(user_id=user_id)
        chat_receive = Chat.objects.filter(chat_id=user_id)
        res['status'] = "success"
        user2status = {}
        for c in chat_send:
            if c.chat_id not in user2status:
                user2status[c.chat_id] = [c.time, c.content, 0]
            elif c.time > user2status[c.chat_id][0]:
                user2status[c.chat_id] = [c.time, c.content, 0]
        for c in chat_receive:
            if c.user_id not in user2status:
                user2status[c.user_id] = [c.time, c.content, int(not c.read)]
            elif c.time > user2status[c.user_id][0]:
                user2status[c.user_id] = [c.time, c.content, user2status[c.user_id][2] + int(not c.read)]
        res['chatlist'] = []
        for user_id, last in sorted(user2status.items(), key=lambda x: x[1], reverse=True):
            user = User.objects.get(id=user_id)
            res['chatlist'].append({
                'user_id': user_id,
                'user_name': user.name,
                'user_avatar': user.getavatar(),
                'last_time': timezone.localtime(last[0]).strftime("%Y-%m-%d %H:%M:%S"),
                'last_content': last[1],
                'unread_num': last[2]
            })
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})