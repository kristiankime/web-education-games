# web-education-games

A scratch space for my work in web based education games.

This project is intended to be a working play web application that demonstrates some examples of how web based educational games might be built.

##### Note to the reader

This readme is intended as documentation for all the steps that were taking in setting up this project. The idea being that, if the reader has the appropriate background in software development, they could they could replicate the steps just reading the document. And even find links to learn about the technologies as they go. As such the readme seem a little overly detail. I will, for example, cover he fact that this project is using Git and Github, which may seem a bit obvious. As a heads up I use a Mac so some of the "nice to have" things on this page will be Mac only. But the majority will be multi platform and free.

## Tools and Tutorials

The following Tools, Techniques, Services, Infrastructure etc were used in creating this application. These are listed in the expected order of usage. So reading through each section and installing them in order should make sense.

* Source Control: [Git](http://git-scm.com/)
* Markdown Editor: [Mou](http://mouapp.com/)
* Web Framework: [Play](http://www.playframework.com/)
* IDE: [Eclipse](http://www.eclipse.org/) or [IntelliJ](http://www.jetbrains.com/idea/)
* Web Hosting: [Heroku](https://www.heroku.com/)
* Database: [Slick](http://slick.typesafe.com/) for accessing [H2](http://www.h2database.com/) for development & [PostgreSQL](http://www.postgresql.org/) for production
* Authentication (TDB): [SecureSocial.ws](http://securesocial.ws/)
* HTML UI: [Twitter Bootstrap](http://getbootstrap.com/)
* Profanity Filter: [PurgoMalum](http://www.purgomalum.com/)

### [Git](http://git-scm.com/)
This project is using Git as a version control system. If you are new to Git you can find an [install guide and crash course here](http://git-scm.com/book/en/Getting-Started-Git-Basics). 

While git can be [used locally](http://tiredblogger.wordpress.com/2009/11/09/creating-local-git-repositories-yeah-its-that-simple/), or you can host it yourself with something like [gitorious](http://gitorious.org/) this project will use [GitHub](https://github.com/). For information on how to get started with Github [check here](https://help.github.com/articles/set-up-git).

### [Mou](http://mouapp.com/)
Mou is a [Markdown](http://daringfireball.net/projects/markdown/) editor for Mac OS X. It's a cute little program that makes editing markdown files much nicer. Installation is fairly straightforward.

As a quick side note Github actually uses a slightly modified flavor of Markdown which has some nice additional features. Details can be found [here](https://help.github.com/articles/github-flavored-markdown).

### [Play](http://www.playframework.com/)

Play is a Java or Scala based Web Framework, I've chosen to work in Scala for this project. Play requires a JDK be installed on your system. So if you don't already have one you'll need to get one. The project is currently using play 2.2.0. Details about how to install play (and a JDK) are on the [play install page](http://www.playframework.com/documentation/2.2.x/Installing).

Play has a lot to it. Documentation and tutorials can be found on the play web site. There is a [To Do List Example App here](http://www.playframework.com/documentation/2.2.x/ScalaTodoList). But if all you want to do is run this project you'll just need to get Play installed.

### [Eclipse](http://www.eclipse.org/)

Eclipse is a IDE which has support for Scala an even play projects. Initial installation is simple just download from the [download page](http://www.eclipse.org/downloads/) and put it where you want it. The project is tested under eclipse version 4.2 (Juno). 

Eclipse has many plugins that may be useful but the primary one for this project is the [Scala one](http://scala-ide.org/download/current.html). This project is currently tested using the scala ide from this download site http://download.scala-ide.org/sdk/e38/scala210/stable/site. The plugin recommends increasing the JVM heap size, instructions for which can be found [here](http://wiki.eclipse.org/FAQ_How_do_I_increase_the_heap_size_available_to_Eclipse%3F).

Another helpful plugin is EGit which can be found by searching in the eclipse market place.

### [Heroku](https://www.heroku.com/)

Heroku is a web hosting company that greatly simplifies deployment, maintenance and upgrading of your web application. Heroku has it a toolbelt you need to install but once in place it's pretty easy to use. Fortunately Play works nicely with heroku and they have instructions on installation etc [here](http://www.playframework.com/documentation/2.1.3/ProductionHeroku).

* http://www.playframework.com/documentation/2.2.x/ProductionHeroku* 

### [Slick](http://slick.typesafe.com/) 
Using H2 with play 

* https://github.com/freekh/play-slick
* http://blog.lunatech.com/2013/08/08/play-slick-getting-started
 
Quick note when using slick with postgres, String is currently mapped to VARCHAR(254)
https://github.com/slick/slick/issues/129 
 
Using Joda with Slick
https://gist.github.com/dragisak/4756344
https://github.com/tototoshi/slick-joda-mapper 
 
### [H2](http://www.h2database.com/) 
Using H2 with play 

* http://www.playframework.com/documentation/2.2.x/ScalaDatabase
* http://www.playframework.com/documentation/2.2.x/Developing-with-the-H2-Database
* http://www.playframework.com/documentation/2.1.1/ProductionConfiguration

### [PostgreSQL](http://www.postgresql.org/)

* https://devcenter.heroku.com/articles/heroku-postgresql#local-setup
* http://postgresapp.com/

### [SecureSocial.ws](http://securesocial.ws/)

http://securesocial.ws/guide/getting-started.html
Samples
https://github.com/jaliss/securesocial/tree/master/samples/scala/demo

### [Twitter Bootstrap](http://getbootstrap.com/)
http://stackoverflow.com/questions/10436815/how-to-use-twitter-bootstrap-2-with-play-framework-2-x

#### mailer
https://github.com/typesafehub/play-plugins/tree/master/mailer

Setting up a gmail account

#### Slick UserService
http://blog.lunatech.com/2013/07/04/play-securesocial-slick

#### Get Keys for each provider

##### Facebook
facebook login
https://developers.facebook.com/docs/web/tutorials/scrumptious/register-facebook-application
https://developers.facebook.com/apps/?action=create
https://developers.facebook.com/
https://developers.facebook.com/docs/facebook-login/

Create a production and local app on Facebook since facebooks auth doesn't like localhost
http://stackoverflow.com/questions/2459728/how-to-test-facebook-connect-locally

Set config vars locally and on Heroku
https://devcenter.heroku.com/articles/config-vars

Make sure to make the app publicly available
http://stackoverflow.com/questions/21329250/the-developers-of-this-app-have-not-set-up-this-app-properly-for-facebook-login

##### Google
https://developers.google.com/accounts/docs/OAuth2Login#registeringyourapp
https://cloud.google.com/console#/project
http://stackoverflow.com/questions/19384906/secure-social-connect-provider

local vs prod
https://developers.google.com/accounts/docs/OAuth2InstalledApp
https://groups.google.com/forum/#!topic/instagram-api-developers/XOp42EUFzDo
http://stackoverflow.com/questions/11485271/google-oauth-2-authorization-error-redirect-uri-mismatch

Local Setup
create "Client ID for web application"
Redirect URIs = http://localhost:9000/authenticate/google
Javascript Origins = https://www.localhost.com

oauth without registration
http://googlecode.blogspot.com/2009/11/oauth-enhancements.html

##### Backing up Postgress
https://devcenter.heroku.com/articles/pgbackups
https://devcenter.heroku.com/articles/heroku-postgres-import-export#export

## Starting a new Project
Once all the tools are installed the following steps will start a blank project.

maybe look at http://flurdy.com/docs/herokuplay/play2.html

#### Create a new Repo on Github

#### Clone the repo
git clone <github url>

##### Build a Blank project in play
build and copy files into repo

##### Setup eclipse support
eclipse with-source=true
http://www.playframework.com/documentation/2.1.x/IDE

##### Update .gitignore
/.target
/.cache
/bin
.DS_Store

##### Push back to Github

##### Start a project on Heroku

heroku create
git push heroku master

##### Start a project on Heroku

set the environment variables for heroku

* look in heroku_environment_variables_EXAMPLE.sh
* repace all the "put_xxx_here" with your values (found on the auth websites)
* rename the script to heroku_environment_variables.sh (this should be git ignored)
* run the script




##### Problems with Evolutions
When the evolution file is generated for a string field it defaults to VARCHAR but TEXT is preferable
    http://blog.jonanin.com/2013/11/20/postgresql-char-varchar/

Failed evolutions "stick"
https://groups.google.com/forum/#!topic/play-framework/ukwA8W9voXU

if all else fails you can drop all the tables:
http://stackoverflow.com/questions/3327312/drop-all-tables-in-postgresql

select 'drop table if exists "' || tablename || '" cascade;' 
  from pg_tables
 where schemaname = 'public';

##### Adding Java Version
https://devcenter.heroku.com/articles/scala-support
https://devcenter.heroku.com/articles/add-java-version-to-an-existing-maven-app 

##### Character encoding problems
https://groups.google.com/forum/?fromgroups#!topic/play-framework/QsC0LubU_30

previous config:
JAVA_OPTS:                    -Xmx384m -Xss512k -XX:+UseCompressedOops
SBT_OPTS:                     -Xmx384m -Xss512k -XX:+UseCompressedOops

updating to:
heroku config:set SBT_OPTS="-Xmx384m -Xss512k -XX:+UseCompressedOops -Dfile.encoding=UTF8" 
heroku config:set JAVA_OPTS="-Xmx384m -Xss512k -XX:+UseCompressedOops -Dfile.encoding=UTF8"

---- DIDN'T WORK----
Note as of 2014-02-04 heroku uses the scala buildpack for play 2 apps
    https://github.com/heroku/heroku-buildpack-scala
which uses sbt to build not play :(

Potential Solution set the buildpack:
heroku config:set BUILDPACK_URL=https://github.com/imikushin/heroku-buildpack-play2.git
Didn't work back to 
heroku config:set BUILDPACK_URL=https://github.com/heroku/heroku-buildpack-play.git


##### Recompile on Heroku without Git Push (TBD)
https://github.com/heroku/heroku/issues/514
git commit --allow-empty -m "empty commit"
git push heroku master

##### Scala Compile error 
http://stackoverflow.com/questions/18872062/scala-compile-server-error-when-using-nailgun

find the process (PID) || lsof -i :3200
check that process     || ps axu |grep <PID>
kill the process       || kill -9 <PID>

##### Git Tagging
http://git-scm.com/book/en/Git-Basics-Tagging

To create a tag for Major x Minor y and Build z

first check to see what tags you already have:
    git tag

next create a tag with the next version:
    git tag -a vx.y.z -m 'my version x.y.z'

tags need to be push explicity
    git push origin vx.y.x

##### SBT clean
Heroku will keep left over build files. If you get errors due to files that shouldn't exist try turning on clean
    heroku config:set SBT_CLEAN=true
It can be turned off later
    heroku config:unset SBT_CLEAN
for more info : https://devcenter.heroku.com/articles/scala-support
