from django.db import models


# Create your models here.
# data class Donation(
#     val _id: String,
#     val amount: Double,
#     val currency: String,
#     val sender: String,
#     val message: String,
#     val id: Long? = null,
#     val tag: String? = null
# )


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
