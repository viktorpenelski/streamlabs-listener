# Generated by Django 3.1.3 on 2020-11-24 08:54

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('obs', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='donation',
            name='date_created',
            field=models.DateTimeField(auto_now_add=True, default=django.utils.timezone.now),
            preserve_default=False,
        ),
    ]
