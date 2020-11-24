=====
Obs
=====

Obs is a Django app to display donation info in a way friendly for obs integration.

Detailed documentation is in the "docs" directory.

Quick start
-----------

1. Add "obs" to your INSTALLED_APPS setting like this::

    INSTALLED_APPS = [
        ...
        'obs',
    ]

2. Include the polls URLconf in your project urls.py like this::

    path('obs/', include('obs.urls')),

3. Run ``python manage.py migrate`` to create the obs models.

4. Start the development server and visit http://127.0.0.1:8000/admin/ (you'll need the Admin app enabled).

5. Visit http://127.0.0.1:8000/obs