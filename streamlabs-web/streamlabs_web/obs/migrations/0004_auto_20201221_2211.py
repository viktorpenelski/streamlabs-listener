# Generated by Django 3.1.3 on 2020-12-21 20:11

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('obs', '0003_aggregateslug'),
    ]

    operations = [
        migrations.AddField(
            model_name='aggregateslug',
            name='max_date',
            field=models.DateTimeField(default=django.utils.timezone.now),
        ),
        migrations.AddField(
            model_name='aggregateslug',
            name='min_date',
            field=models.DateTimeField(default=django.utils.timezone.now),
        ),
    ]