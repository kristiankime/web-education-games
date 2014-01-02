
# --- !Ups

create table "token" ("uuid" VARCHAR NOT NULL PRIMARY KEY,"email" VARCHAR NOT NULL,"creationTime" TIMESTAMP NOT NULL,"expirationTime" TIMESTAMP NOT NULL,"isSignUp" BOOLEAN NOT NULL);
create table "user" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"userId" VARCHAR NOT NULL,"providerId" VARCHAR NOT NULL,"firstName" VARCHAR NOT NULL,"lastName" VARCHAR NOT NULL,"fullName" VARCHAR NOT NULL,"email" VARCHAR,"avatarUrl" VARCHAR,"authMethod" VARCHAR NOT NULL,"token" VARCHAR,"secret" VARCHAR,"accessToken" VARCHAR,"tokenType" VARCHAR,"expiresIn" INTEGER,"refreshToken" VARCHAR,"hasher" VARCHAR,"password" VARCHAR,"salt" VARCHAR);

# --- !Downs

drop table "token";
drop table "user";
