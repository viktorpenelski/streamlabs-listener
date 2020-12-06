from rest_framework import serializers

from .models import Donation


class DonationSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Donation
        fields = ['id', '_id', 'amount', 'currency', 'sender', 'message', 'tag', 'date_created']


class AggregateSerializer(serializers.Serializer):
    tag = serializers.CharField()
    amount = serializers.FloatField()
