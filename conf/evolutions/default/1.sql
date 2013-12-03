
# --- !Ups

create table "derivative_questions" ("id" SERIAL NOT NULL PRIMARY KEY,"mathml" VARCHAR(254) NOT NULL,"rawstr" VARCHAR(254) NOT NULL,"synched" BOOLEAN NOT NULL);
create table "equations" ("id" SERIAL NOT NULL PRIMARY KEY,"equation" VARCHAR(254) NOT NULL);

# --- !Downs

drop table "derivative_questions";
drop table "equations";

