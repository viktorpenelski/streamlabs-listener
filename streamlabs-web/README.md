# Streamlabs web


## environment

ensure already in a venv
(creating a new one with `python3 -m venv /path/to/new/virtual/env` )

activate venv:
    - Windows `cmd.exe C:\> <venv>\Scripts\activate.bat`
    - POSIX `source <venv>/bin/activate`

generate requirements.txt:
`pip freeze > requirements.txt`

install requirements.txt:
`pip install -r requirements.txt`

## DB Migrations

By default, all fields are `NOT NULL`.
When migrating (e.g. adding new col), a one-off migration can be performed.

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

## Testing

[Adding `--keepdb`](https://docs.djangoproject.com/en/3.1/ref/django-admin/#cmdoption-test-keepdb) makes the integration tests preserve DB between runs, speeding up the process by quite a lot
`python manage.py test --keepdb`

## Dist package

[ref](https://packaging.python.org/tutorials/packaging-projects/)

Package:
`python setup.py sdist`

Install:
`python -m pip install django-obs/dist/django-obs-0.1.tar.gz`