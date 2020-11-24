from django.contrib import admin
from rangefilter.filter import DateTimeRangeFilter

from .models import Donation


class DonationAdmin(admin.ModelAdmin):
    fields = ['tag', 'amount', 'currency', 'sender', 'message', '_id']
    list_display = ['id', 'sender', 'tag', 'amount', 'currency', 'message', 'date_created', '_id']
    search_fields = ['tag', 'sender', '_id']
    list_filter = ['tag', ('date_created', DateTimeRangeFilter)]


admin.site.register(Donation, DonationAdmin)
