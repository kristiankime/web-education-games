#!/bin/bash

# Secure Social Configuration
heroku config:set FACEBOOK_APP_ID=put_app_id_here
heroku config:set FACEBOOK_APP_SECRET=put_app_secret_here

# Database configuration
heroku config:set DATABASE_DRIVER=put_db_driver_here
# DATABASE_URL is usually already set by heroku 
heroku config:set DATABASE_USER=put_db_user_here
heroku config:set DATABASE_PASSWORD=put_db_password_here