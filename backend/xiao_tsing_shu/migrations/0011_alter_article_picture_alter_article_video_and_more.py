# Generated by Django 4.0.6 on 2023-05-27 14:57

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('xiao_tsing_shu', '0010_alter_user_name'),
    ]

    operations = [
        migrations.AlterField(
            model_name='article',
            name='picture',
            field=models.FileField(default='picture/default.jpg', upload_to='picture/%Y/%m/%d'),
        ),
        migrations.AlterField(
            model_name='article',
            name='video',
            field=models.FileField(default='video/default.mp4', upload_to='video/%Y/%m/%d'),
        ),
        migrations.AlterField(
            model_name='user',
            name='avatar',
            field=models.FileField(default='avatar/default.jpg', upload_to='avatar/%Y/%m/%d'),
        ),
    ]
