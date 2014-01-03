
# --- !Ups

create table "token" ("uuid" TEXT NOT NULL PRIMARY KEY,"email" TEXT NOT NULL,"creationTime" TIMESTAMP NOT NULL,"expirationTime" TIMESTAMP NOT NULL,"isSignUp" BOOLEAN NOT NULL);
create table "user" ("id" SERIAL NOT NULL PRIMARY KEY,"userId" TEXT NOT NULL,"providerId" TEXT NOT NULL,"firstName" TEXT NOT NULL,"lastName" TEXT NOT NULL,"fullName" TEXT NOT NULL,"email" TEXT,"avatarUrl" TEXT,"authMethod" TEXT NOT NULL,"token" TEXT,"secret" TEXT,"accessToken" TEXT,"tokenType" TEXT,"expiresIn" INTEGER,"refreshToken" TEXT,"hasher" TEXT,"password" TEXT,"salt" TEXT);


# --- !Downs

drop table "token";
drop table "user";
