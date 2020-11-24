import uuid

from django.test import TestCase
from django.urls import reverse

from .models import Donation


class DonationIndexViewTests(TestCase):
    def test_foo(self):
        response = self.client.get(reverse('obs:aggregate'))
        self.assertEqual(response.status_code, 200)

    def test_aggregation(self):
        create_donation("#foo", 42.42)
        create_donation("#foo", 12.12)
        create_donation("#bar", 33)
        response = self.client.get(f"{reverse('obs:aggregate')}?tag=%23foo&tag=%23bar&tag=%23zar")
        self.assertEqual(response.status_code, 200)
        self.assertContains(response, "#foo -> 54.54")
        self.assertContains(response, "#bar -> 33.00")
        self.assertContains(response, "#zar -> 0.00")


def create_donation(tag, amount):
    create = Donation.objects.create(tag=tag, amount=amount, _id=uuid.uuid4())
    return create


class DonationModelTest(TestCase):

    def test_foo(self):
        """
        is this the javadoc of python?
        """
        foo = Donation(amount=42.42, currency="EUR")
        self.assertTrue("donation for: 42.42 EUR" in str(foo))
