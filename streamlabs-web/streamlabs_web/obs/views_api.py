
from rest_framework import status, viewsets, permissions
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.views import APIView

from .models import Donation, AggregateSlug
from .serializers import DonationSerializer, AggregateSerializer, AggregateSlugSerializer


@api_view(['GET', 'POST'])
def snippet_list(request):
    """
    List all code snippets, or create a new snippet.
    """
    return Response(status=status.HTTP_204_NO_CONTENT)


class DonationViewSet(viewsets.ModelViewSet):
    queryset = Donation.objects.all().order_by('-date_created')
    serializer_class = DonationSerializer
    permission_classes = [permissions.AllowAny]


class AggregateSlugViewSet(viewsets.ModelViewSet):
    queryset = AggregateSlug.objects.all()
    serializer_class = AggregateSlugSerializer
    permission_classes = [permissions.AllowAny]


class AggregateView(APIView):
    def get(self, request):
        from .views import aggregate_for
        tags = request.query_params.getlist('tag')
        min_date = request.query_params.get('min_date')
        max_date = request.query_params.get('max_date')
        aggregated_tags = [aggregate_for(tag, min_date, max_date) for tag in tags]
        result = AggregateSerializer(aggregated_tags, many=True).data
        return Response({'aggregated_tags': result})
