# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "equations" ("id" SERIAL NOT NULL PRIMARY KEY,"equation" TEXT NOT NULL);
create table "derivative_answers" ("id" SERIAL NOT NULL PRIMARY KEY,"question_id" BIGINT NOT NULL,"mathml" TEXT NOT NULL,"rawstr" TEXT NOT NULL,"synched" BOOLEAN NOT NULL,"correct" BOOLEAN NOT NULL);
create table "derivative_questions" ("id" SERIAL NOT NULL PRIMARY KEY,"mathml" TEXT NOT NULL,"rawstr" TEXT NOT NULL,"synched" BOOLEAN NOT NULL);
create table "derivative_quizes_question" ("quiz_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL);
alter table "derivative_quizes_question" add constraint "derivative_quizes_question_pk" primary key("question_id","quiz_id");
create table "derivative_quizes" ("id" SERIAL NOT NULL PRIMARY KEY,"name" TEXT NOT NULL);
create table "derivative_users_answers" ("user_id" BIGINT NOT NULL,"answer_id" BIGINT NOT NULL);
alter table "derivative_users_answers" add constraint "derivative_users_answers_pk" primary key("user_id","answer_id");
create table "derivative_users_questions" ("user_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL);
alter table "derivative_users_questions" add constraint "derivative_users_questions_pk" primary key("user_id","question_id");
create table "token" ("uuid" TEXT NOT NULL PRIMARY KEY,"email" TEXT NOT NULL,"creationTime" TIMESTAMP NOT NULL,"expirationTime" TIMESTAMP NOT NULL,"isSignUp" BOOLEAN NOT NULL);
create table "user" ("id" SERIAL NOT NULL PRIMARY KEY,"userId" TEXT NOT NULL,"providerId" TEXT NOT NULL,"firstName" TEXT NOT NULL,"lastName" TEXT NOT NULL,"fullName" TEXT NOT NULL,"email" TEXT,"avatarUrl" TEXT,"authMethod" TEXT NOT NULL,"token" TEXT,"secret" TEXT,"accessToken" TEXT,"tokenType" TEXT,"expiresIn" INTEGER,"refreshToken" TEXT,"hasher" TEXT,"password" TEXT,"salt" TEXT);
alter table "derivative_answers" add constraint "derivative_answers_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizes_question" add constraint "derivative_quizes_question_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizes_question" add constraint "derivative_quizes_question_quiz_fk" foreign key("quiz_id") references "derivative_quizes"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_answers" add constraint "derivative_users_answers_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_answers" add constraint "derivative_users_answers_question_fk" foreign key("answer_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_questions" add constraint "derivative_users_questions_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_questions" add constraint "derivative_users_questions_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "derivative_answers" drop constraint "derivative_answers_fk";
alter table "derivative_quizes_question" drop constraint "derivative_quizes_question_question_fk";
alter table "derivative_quizes_question" drop constraint "derivative_quizes_question_quiz_fk";
alter table "derivative_users_answers" drop constraint "derivative_users_answers_user_fk";
alter table "derivative_users_answers" drop constraint "derivative_users_answers_question_fk";
alter table "derivative_users_questions" drop constraint "derivative_users_questions_user_fk";
alter table "derivative_users_questions" drop constraint "derivative_users_questions_question_fk";
drop table "equations";
drop table "derivative_answers";
drop table "derivative_questions";
alter table "derivative_quizes_question" drop constraint "derivative_quizes_question_pk";
drop table "derivative_quizes_question";
drop table "derivative_quizes";
alter table "derivative_users_answers" drop constraint "derivative_users_answers_pk";
drop table "derivative_users_answers";
alter table "derivative_users_questions" drop constraint "derivative_users_questions_pk";
drop table "derivative_users_questions";
drop table "token";
drop table "user";

