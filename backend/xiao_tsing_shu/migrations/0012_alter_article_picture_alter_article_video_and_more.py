# Generated by Django 4.0.6 on 2023-05-29 07:19

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('xiao_tsing_shu', '0011_alter_article_picture_alter_article_video_and_more'),
    ]

    operations = [
        migrations.AlterField(
            model_name='article',
            name='picture',
            field=models.FileField(default='static/picture/default.jpg', upload_to='static/picture/%Y/%m/%d'),
        ),
        migrations.AlterField(
            model_name='article',
            name='video',
            field=models.FileField(default='static/video/default.mp4', upload_to='static/video/%Y/%m/%d'),
        ),
        migrations.AlterField(
            model_name='user',
            name='avatar',
            field=models.FileField(default='static/avatar/default.jpg', upload_to='static/avatar/%Y/%m/%d'),
        ),
    ]