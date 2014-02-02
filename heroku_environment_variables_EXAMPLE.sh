#!/bin/bash

# Secure Social Configuration
heroku config:set FACEBOOK_APP_ID=put_app_id_here
heroku config:set FACEBOOK_APP_SECRET=put_app_secret_here

heroku config:set GOOGLE_CLIENT_ID=put_google_client_id_here
heroku config:set GOOGLE_SECRET=put_google_secret_here

heroku config:set EMAIL_USER=put_user_here
heroku config:set EMAIL_PASSWORD=put_password_here
heroku config:set EMAIL_ADDRESS=put_address_here

# Database configuration
heroku config:set DATABASE_DRIVER=put_db_driver_here
# DATABASE_URL is usually already set by heroku 
heroku config:set DATABASE_USER=put_db_user_here
heroku config:set DATABASE_PASSWORD=put_db_password_here