# Streamlabs web


## generating `requirements.txt`
ensure already in a venv
(creating a new one with `python3 -m venv /path/to/new/virtual/env` )

`pip freeze > requirements.txt`

After that can be used with `pip install -r requirements.txt`

## DB Migrations

`python manage.py makemigrations obs`

Creates the next sequential migration from all models defined int `obs/models.py`
to `obs/migrations/xxxx_yy.py`

`python manage.py sqlmigrate obs 0001`
Prints the SQL that would be generated for the particular migration

`python manage.py migrate`
Executes the actual migrations.

Migration info is stored inside table `django_migrations` where the last ran migration
can be seen. In case the migration has already been applied (e.g. in my case by an external app)
the `--fake` flag can be provided, pretending to apply all migrations without actually doing so.


## Admin

Create an admin user:
`python manage.py createsuperuser`

Make sure to add the app in admin

`obs.admin.py`:
```
from django.contrib import admin
from .models import Donation
admin.site.register(Donation)
```

As well as that it is installed under main apps' `settings.py`

```
INSTALLED_APPS = [
    'obs.apps.ObsWebConfig',
    'django.contrib.admin',
    ...
]
```