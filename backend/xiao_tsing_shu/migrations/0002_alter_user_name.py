# Generated by Django 4.0.6 on 2023-05-07 10:10

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('xiao_tsing_shu', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='user',
            name='name',
            field=models.CharField(max_length=64, unique=True),
        ),
    ]
