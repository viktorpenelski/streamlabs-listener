from django.urls import path

from .. import views

# without defining the app_name here, urls cannot be constructed using `reverse({app_name}:{url_pattern_name})` syntax
app_name = 'obs'

urlpatterns = [
    path('', views.index, name='index'),
    path('aggregate', views.aggregate_hashtags, name='aggregate'),
    path('time', views.time_view, name='time'),
    path('aggregate_chart', views.aggregate_chart, name='aggregate_chart'),
    path('barchart', views.barchart, name='barchart')
]

