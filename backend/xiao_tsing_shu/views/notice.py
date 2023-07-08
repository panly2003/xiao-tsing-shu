from django.http import JsonResponse
from django.utils.datastructures import MultiValueDictKeyError

from ..models import Notice, User

def notice_get(request):
    res = {}
    if request.method != "POST":
        res['status'] = "error"
        res['message'] = "请求方法错误"
        return JsonResponse(res, json_dumps_params={'ensure_ascii': False})
    try:
        user_id = request.POST['user_id']
        user = User.objects.get(id=user_id)
        notices = Notice.objects.filter(user_id=user_id)
        res['status'] = "success"
        res['notices'] = []
        for notice in notices:
            res['notices'].append(notice.content)
            notice.delete()
    except Exception as e:
        if isinstance(e, MultiValueDictKeyError):
            res['status'] = "error"
            res['message'] = "请求参数错误" + str(e)
        else:
            res['status'] = "error"
            res['message'] = str(e)
    return JsonResponse(res, json_dumps_params={'ensure_ascii': False})