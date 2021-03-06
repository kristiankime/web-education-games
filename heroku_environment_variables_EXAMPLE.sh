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

# Cache configuration
heroku config:set EHCACHE_ENABLED=disabled
heroku config:set MEMCACHE_ENABLED=enabled

# Heroku seems to allow the JVM to ask for too much memory with Play 2.2 with default (http://stackoverflow.com/questions/13370694/heroku-memory-leak-with-play2-scala)
# heroku config:set java_opts='-Xmx384m -Xms384m -Xss512k -XX:+UseCompressedOops'
# heroku config:set JAVA_OPTS='-Xmx384m -Xms384m -Xss512k -XX:+UseCompressedOops'