from unittest import TestCase

# Create your tests here.
from .service import EURCurrencyConversion


class TestConversion(TestCase):

    def test_lowercase_currency(self):
        stub = StubCache()
        service = EURCurrencyConversion(csv_as_lines=stub)
        self.assertEqual(1.2193, service.to_eur(1, "usd"))

    def test_accuracy(self):
        stub = StubCache()
        service = EURCurrencyConversion(csv_as_lines=stub)
        self.assertEqual(2.9337, service.to_eur(1.5, "BGN"))

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
