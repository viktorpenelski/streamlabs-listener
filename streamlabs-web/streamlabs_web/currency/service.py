class CSVasLines:
    def __init__(self, csv_path="eurofxref.csv"):
        self._csv_path = csv_path

    def lines_as_list(self):
        with open(self._csv_path, 'r') as f:
            csv = f.read()
            return csv.split('\n')


class EURCurrencyConversion:

    def __init__(self, csv_as_lines):
        self._eur_dict = self._csv_to_dict(csv_as_lines.lines_as_list())

    @staticmethod
    def _csv_to_dict(lines):
        # strings are truthy, so empty strings will be filtered out with the if c.strip() condition
        currencies = [c.strip() for c in lines[0].split(",")[1:] if c.strip()]
        rates = [float(r.strip()) for r in lines[1].split(",")[1:] if r.strip()]
        return dict(zip(currencies, rates))

    def to_eur(self, amount, from_currency):
        if from_currency.upper() == "EUR":
            return amount
        multiplier = self._eur_dict[from_currency.upper()]
        if not multiplier:
            raise Exception(f"did not find {from_currency} in currency dict!")
        return amount * multiplier


x = EURCurrencyConversion(CSVasLines())
print(x.to_eur(1, "USD"))
print(x.to_eur(1, "BGN"))
print(x.to_eur(1, "EUR"))
