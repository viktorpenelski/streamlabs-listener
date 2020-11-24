# Create your views here.
from dataclasses import dataclass

from django.http import HttpResponse
from django.shortcuts import render

from .models import Donation


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


def aggregate_for(tag):
    tagged_donations = Donation.objects.all().filter(tag=tag)
    aggregated = sum(d.amount for d in tagged_donations)
    return AggregatedDonation(tag, aggregated)


@dataclass
class AggregatedDonation:
    tag: str
    amount: float