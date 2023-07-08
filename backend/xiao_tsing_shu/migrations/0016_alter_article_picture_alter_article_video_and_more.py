# Generated by Django 4.0.6 on 2023-05-29 08:53

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('xiao_tsing_shu', '0015_alter_article_picture_alter_article_video_and_more'),
    ]

    operations = [
        migrations.AlterField(
            model_name='article',
            name='picture',
            field=models.FileField(default='picture/default.jpg', upload_to='picture'),
        ),
        migrations.AlterField(
            model_name='article',
            name='video',
            field=models.FileField(default='video/default.mp4', upload_to='video'),
        ),
        migrations.AlterField(
            model_name='user',
            name='avatar',
            field=models.FileField(default='avatar/default.jpg', upload_to='avatar'),
        ),
    ]
