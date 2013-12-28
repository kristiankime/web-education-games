# --- !Ups

create table "user" ("id" SERIAL NOT NULL PRIMARY KEY,"userId" VARCHAR(254) NOT NULL,"providerId" VARCHAR(254) NOT NULL,"firstName" VARCHAR(254) NOT NULL,"lastName" VARCHAR(254) NOT NULL,"fullName" VARCHAR(254) NOT NULL,"email" VARCHAR(254),"avatarUrl" VARCHAR(254),"authMethod" VARCHAR(254) NOT NULL,"token" VARCHAR(254),"secret" VARCHAR(254),"accessToken" VARCHAR(254),"tokenType" VARCHAR(254),"expiresIn" INTEGER,"refreshToken" VARCHAR(254));

# --- !Downs

drop table "user";

