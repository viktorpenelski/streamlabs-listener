from django.urls import path, include
from rest_framework import routers

from . import views, views_api

# without defining the app_name here, urls cannot be constructed using `reverse({app_name}:{url_pattern_name})` syntax
app_name = 'obs'
router = routers.DefaultRouter()
router.register(r'donations', views_api.DonationViewSet)
router.register(r'aggregate_slugs', views_api.AggregateSlugViewSet)

urlpatterns = [
    path('', views.index, name='index'),
    path('aggregate', views.aggregate_hashtags, name='aggregate'),
    path('aggregate_chart', views.aggregate_chart, name='aggregate_chart'),
    path('api/', include(router.urls)),
    path('api/aggregate', views_api.AggregateView.as_view(), name='api_aggregate'),
    path('barchart', views.barchart, name='barchart')
]

