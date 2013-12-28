
# --- !Ups

create table "derivative_question_answers" ("id" SERIAL NOT NULL PRIMARY KEY,"question_id" BIGINT NOT NULL,"mathml" VARCHAR(254) NOT NULL,"rawstr" VARCHAR(254) NOT NULL,"synched" BOOLEAN NOT NULL,"correct" BOOLEAN NOT NULL);
create table "derivative_question_set_links" ("question_id" BIGINT NOT NULL,"question_set_id" BIGINT NOT NULL);
alter table "derivative_question_set_links" add constraint "derivative_question_set_links_pk" primary key("question_id","question_set_id");
create table "derivative_question_sets" ("id" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL);
create table "derivative_questions" ("id" SERIAL NOT NULL PRIMARY KEY,"mathml" VARCHAR(254) NOT NULL,"rawstr" VARCHAR(254) NOT NULL,"synched" BOOLEAN NOT NULL);
create table "equations" ("id" SERIAL NOT NULL PRIMARY KEY,"equation" VARCHAR(254) NOT NULL);
alter table "derivative_question_answers" add constraint "question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_question_set_links" add constraint "derivative_question_set_links_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_question_set_links" add constraint "derivative_question_set_links_question_set_fk" foreign key("question_set_id") references "derivative_question_sets"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "derivative_question_answers" drop constraint "question_fk";
alter table "derivative_question_set_links" drop constraint "derivative_question_set_links_question_fk";
alter table "derivative_question_set_links" drop constraint "derivative_question_set_links_question_set_fk";
drop table "derivative_question_answers";
alter table "derivative_question_set_links" drop constraint "derivative_question_set_links_pk";
drop table "derivative_question_set_links";
drop table "derivative_question_sets";
drop table "derivative_questions";
drop table "equations";
