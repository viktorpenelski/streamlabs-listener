from unittest import TestCase

# Create your tests here.
from streamlabs_web.currency.service import EURCurrencyConversion


class TestConversion(TestCase):

    def test_eur_returns_same_amount(self):
        stub = StubCache()
        service = EURCurrencyConversion(csv_as_lines=stub)
        self.assertEqual(42.42, service.to_eur(42.42, "EUR"))

    def test_lowercase_currency(self):
        stub = StubCache()
        service = EURCurrencyConversion(csv_as_lines=stub)
        self.assertEqual(1, service.to_eur(1.2193, "usd"))

    def test_accuracy_to_one(self):
        stub = StubCache()
        service = EURCurrencyConversion(csv_as_lines=stub)
        self.assertEqual(1, service.to_eur(1.9558, "BGN"))

    def test_accurate_to_4_decimal_places(self):
        stub = StubCache()
        service = EURCurrencyConversion(csv_as_lines=stub)
        self.assertEqual(7.6695, service.to_eur(15, "BGN"))

    def test_accurate_to_4_decimal_places_bigger_number(self):
        stub = StubCache()
        service = EURCurrencyConversion(csv_as_lines=stub)
        self.assertEqual(7912.6444, service.to_eur(1000000, "JPY"))

    def test_missing_raises_exception(self):
        stub = StubCache()
        service = EURCurrencyConversion(csv_as_lines=stub)
        with self.assertRaises(Exception):
            service.to_eur(1.5, "GGG")


# this CSV format comes from ecb.europa.eu
# https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html
# in particular: https://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip
class StubCache:
    def lines_as_list(self):
        return [
            "Date, USD, JPY, BGN,",
            "24 December 2020, 1.2193, 126.38, 1.9558, "
            ""
        ]
