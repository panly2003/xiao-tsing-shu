from django.core.management.base import BaseCommand, CommandError
from xiao_tsing_shu.models import User

class Command(BaseCommand):
    help = 'Add a system user in xiao_tsing_shu for notice.'

    def handle(self, *args, **options):
        try:
            user = User.create('system', '系统通知', 'system')
            user.save()
            self.stdout.write(self.style.SUCCESS('Successfully add system user.'))
        except Exception as e:
            self.stdout.write(e)