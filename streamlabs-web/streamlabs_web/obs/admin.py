from django.contrib import admin
from rangefilter.filter import DateTimeRangeFilter

from .models import Donation, AggregateSlug


class DonationAdmin(admin.ModelAdmin):
    fields = ['tag', 'amount', 'currency', 'sender', 'message', '_id']
    list_display = ['id', 'sender', 'tag', 'amount', 'currency', 'message', 'date_created', '_id']
    search_fields = ['tag', 'sender', '_id']
    list_filter = ['tag', ('date_created', DateTimeRangeFilter)]


class AggregateSlugAdmin(admin.ModelAdmin):
    fields = ['slug', 'tags', 'min_date', 'max_date']
    list_display = ['slug', 'tags', 'min_date', 'max_date']
    search_fields = ['slug', 'tags']


admin.site.register(Donation, DonationAdmin)
admin.site.register(AggregateSlug, AggregateSlugAdmin)
