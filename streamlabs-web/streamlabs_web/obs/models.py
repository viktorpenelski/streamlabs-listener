from datetime import datetime, timedelta

from django.utils.timezone import now

from django.db import models


class Donation(models.Model):
    _id = models.TextField(unique=True)
    amount = models.FloatField()
    currency = models.CharField(max_length=10)
    sender = models.TextField()
    message = models.TextField()
    tag = models.CharField(max_length=256, null=True)
    date_created = models.DateTimeField(auto_now_add=True)  # auto_now updates every time

    # seems like django creates an `id` by default as serial NOT NULL PK
    # id = models.IntegerField(primary_key=True)

    def __str__(self):
        return (
            f'id: {self.id}, '
            f'external id: {self._id}, '
            f'sender: {self.sender}, '
            f'donation for: {self.amount} {self.currency}, '
            f'message: {self.message}, '
            f'tag: {self.tag}'
        )

    # table name can be specified within inner class Meta
    class Meta:
        db_table = 'donations'


def default_end_time():
    return now() + timedelta(minutes=30)


class AggregateSlug(models.Model):
    slug = models.CharField(max_length=16, unique=True, primary_key=True, editable=True)
    tags = models.TextField(editable=True)
    min_date = models.DateTimeField(default=now)
    max_date = models.DateTimeField(default=default_end_time)

    class Meta:
        db_table = 'aggregate_slugs'
