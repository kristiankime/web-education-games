# web-education-games

A scratch space for my work in web based education games.

This project is intended to be a working play web application that demostrates some examples of how web based educational games might be built.

##### Note to the reader

This readme is intened as documentation for all the steps that were taking in setting up this project. The idea being that, if the reader has the apporiate background in software development, they could they could replicate the steps just reading the document. And even find links to learn about the technologies as they go. As such the readme seem a little overly detail. I will, for example, cover he fact that this project is using Git and Github, which may seem a bit obvious. As a heads up I use a Mac so some of the "nice to have" things on this page will be Mac only. But the majority will be multi platform and free.

## Tools and Tutorials

The following Tools, Techniques, Services, Infrastructure etc were used in creating this application. These are listed in the expected order of usage. So reading through each section and installing them in order should make sense.

* Source Control: [Git](http://git-scm.com/)
* Markdown Editor: [Mou](http://mouapp.com/)
* Web Framework: [Play](http://www.playframework.com/)
* IDE: [Eclipse](http://www.eclipse.org/)
* Web Hosting: [Heroku](https://www.heroku.com/)
* 


### [Git](http://git-scm.com/)
This project is using Git as a version control system. If you are new to Git you can find an [install guide and crash course here](http://git-scm.com/book/en/Getting-Started-Git-Basics). 

While git can be [used locally](http://tiredblogger.wordpress.com/2009/11/09/creating-local-git-repositories-yeah-its-that-simple/), or you can host it yourself with something like [gitorious](http://gitorious.org/) this projec will use [GitHub](https://github.com/). For information on how to get started with Github [check here](https://help.github.com/articles/set-up-git).

### [Mou](http://mouapp.com/)
Mou is a [Markdown](http://daringfireball.net/projects/markdown/) editor for Mac OS X. It's a cute little program that makes editing markdown files much nicer. Installation is faily staightforward. 

As a quick side note Github actually uses a slightly modified flavor of Markdown which has some nice additional features. Details can be found [here](https://help.github.com/articles/github-flavored-markdown).

### [Play](http://www.playframework.com/)

Play is a Java or Scala based Web Framework, I've choosen to work in Scala for this project. Play requires a JDK be installed on your system. So if you don't already have one you'll need to get one. Details about how to install play (and a JDK) are on the [play install page](http://www.playframework.com/documentation/2.1.x/Installing).

Play has a lot to it. Documentation and tutorials can be found on the play web site. There is a [To Do List Example App here](http://www.playframework.com/documentation/2.1.x/ScalaTodoList). But if all you want to do is run this project you'll just need to get Play installed.

### [Eclipse](http://www.eclipse.org/)

Eclipse is a IDE which has support for Scala an even play projects. Initial installation is simple just download from the [download page](http://www.eclipse.org/downloads/) and put it where you want it.

Eclipse has many plugins that may be useful but the primary one for this project is the [Scala one](http://scala-ide.org/download/current.html). Another helpful plugin is EGit which can be found by searching in the eclipse market place.

### [Heroku](https://www.heroku.com/)

Heroku is a web hosting company that greatly simplifies deployment, maintenance and upgrading of your web application. Heroku has it a toolbelt you need to install but once in place it's pretty easy to use. Fortunately Play works nicely with heroku and the have instructions on installation etc [here](http://www.playframework.com/documentation/2.1.3/ProductionHeroku).

## Starting a new Project
Once all the tools are installed the following steps will start a blank project.

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
