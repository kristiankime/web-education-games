# --- !Ups

create table "equations" ("id" BIGSERIAL PRIMARY KEY, "equation" VARCHAR NOT NULL);

# --- !Downs

drop table "equations";

