# Generated by Django 4.0.6 on 2023-05-08 12:49

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('xiao_tsing_shu', '0007_article_comment_like_star_star_unique_star_and_more'),
    ]

    operations = [
        migrations.AlterField(
            model_name='article',
            name='type',
            field=models.CharField(max_length=64),
        ),
    ]
