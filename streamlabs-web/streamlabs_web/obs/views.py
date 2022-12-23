# Create your views here.
import json
from dataclasses import dataclass

from django.http import HttpResponse, JsonResponse
from django.shortcuts import render

from .models import Donation
from currency.service import EURCurrencyConversion, CSVasLines

currency_converter = EURCurrencyConversion(CSVasLines())


def index(request):
    return HttpResponse("Hello, world. You're at the obs web index.")


def barchart(request):
    from .testchart import MyBarChartDrawing
    d = MyBarChartDrawing()
    # get a GIF (or PNG, JPG, or whatever)
    binary_stuff = d.asString('png')
    return HttpResponse(binary_stuff, 'image/png')


def aggregate_hashtags(request):
    hashtags = request.GET.getlist("tag")
    aggregated_tags = [aggregate_for(tag) for tag in hashtags]
    context = {"aggregated_tags": aggregated_tags}
    return render(request, "obs/aggregate.html", context)


def time_view(request):
    context = {}
    return render(request, "obs/time.html", context)

def aggregate_chart(request):
    return render(request, "obs/aggregate_chart.html")


def aggregate_for(tag, min_date=None, max_date=None):
    tagged_donations = Donation.objects.all().filter(tag=tag)
    if min_date:
        tagged_donations = tagged_donations.filter(date_created__gte=min_date)
    if max_date:
        tagged_donations = tagged_donations.filter(date_created__lte=max_date)
    aggregated = sum(_converted_to_eur_or_0(d) for d in tagged_donations)
    return AggregatedDonation(tag, aggregated)


def _converted_to_eur_or_0(donation):
    try:
        return currency_converter.to_eur(donation.amount, donation.currency)
    except Exception:
        return 0


class AggregatedDonationEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, AggregatedDonation):
            return obj.__dict__
        # Base class default() raises TypeError:
        return json.JSONEncoder.default(self, obj)


@dataclass
class JsonAggregated:
    aggregate_hashtags: list


@dataclass
class AggregatedDonation:
    tag: str
    amount: float

    def to_json(self):
        return json.dumps(self, default=lambda obj: obj.__dict__)
