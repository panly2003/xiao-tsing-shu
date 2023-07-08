# Generated by Django 4.0.6 on 2023-05-07 09:34

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Block',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('user_id', models.IntegerField(db_index=True)),
                ('block_id', models.IntegerField()),
                ('time', models.DateTimeField(auto_now_add=True)),
            ],
            options={
                'ordering': ['-time'],
            },
        ),
        migrations.CreateModel(
            name='Chat',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('content', models.CharField(max_length=256)),
                ('time', models.DateTimeField(auto_now_add=True)),
            ],
            options={
                'ordering': ['time'],
            },
        ),
        migrations.CreateModel(
            name='Follow',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('user_id', models.IntegerField(db_index=True)),
                ('follow_id', models.IntegerField()),
                ('time', models.DateTimeField(auto_now_add=True)),
            ],
            options={
                'ordering': ['-time'],
            },
        ),
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('name', models.CharField(max_length=64)),
                ('password', models.CharField(max_length=64)),
                ('profile', models.CharField(max_length=64)),
                ('image', models.CharField(max_length=64)),
            ],
        ),
        migrations.AddConstraint(
            model_name='follow',
            constraint=models.UniqueConstraint(fields=('user_id', 'follow_id'), name='unique_follow'),
        ),
        migrations.AddField(
            model_name='chat',
            name='chat_id',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='receiver', to='xiao_tsing_shu.user'),
        ),
        migrations.AddField(
            model_name='chat',
            name='user_id',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='sender', to='xiao_tsing_shu.user'),
        ),
        migrations.AddConstraint(
            model_name='block',
            constraint=models.UniqueConstraint(fields=('user_id', 'block_id'), name='unique_block'),
        ),
    ]
