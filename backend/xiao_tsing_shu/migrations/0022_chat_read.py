# Generated by Django 4.0.6 on 2023-06-04 11:45

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('xiao_tsing_shu', '0021_notice'),
    ]

    operations = [
        migrations.AddField(
            model_name='chat',
            name='read',
            field=models.BooleanField(default=False),
        ),
    ]