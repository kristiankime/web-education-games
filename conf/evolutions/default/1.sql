# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "derivative_question_answers" ("id" SERIAL NOT NULL PRIMARY KEY,"question_id" BIGINT NOT NULL,"mathml" VARCHAR(254) NOT NULL,"rawstr" VARCHAR(254) NOT NULL,"synched" BOOLEAN NOT NULL,"correct" BOOLEAN NOT NULL);
create table "derivative_questions" ("id" SERIAL NOT NULL PRIMARY KEY,"mathml" VARCHAR(254) NOT NULL,"rawstr" VARCHAR(254) NOT NULL,"synched" BOOLEAN NOT NULL);
create table "equations" ("id" SERIAL NOT NULL PRIMARY KEY,"equation" VARCHAR(254) NOT NULL);
alter table "derivative_question_answers" add constraint "question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "derivative_question_answers" drop constraint "question_fk";
drop table "derivative_question_answers";
drop table "derivative_questions";
drop table "equations";

