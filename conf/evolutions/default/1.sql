# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "equations" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"equation" VARCHAR NOT NULL);
create table "derivative_courses_quizzes" ("course_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL);
alter table "derivative_courses_quizzes" add constraint "derivative_courses_quizzes_pk" primary key("course_id","quiz_id");
create table "courses" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL);
create table "users_courses" ("user_id" BIGINT NOT NULL,"course_id" BIGINT NOT NULL);
alter table "users_courses" add constraint "users_courses_pk" primary key("user_id","course_id");
create table "derivative_answers" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"question_id" BIGINT NOT NULL,"mathml" VARCHAR NOT NULL,"rawstr" VARCHAR NOT NULL,"synched" BOOLEAN NOT NULL,"correct" BOOLEAN NOT NULL);
create table "derivative_questions" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"mathml" VARCHAR NOT NULL,"rawstr" VARCHAR NOT NULL,"synched" BOOLEAN NOT NULL);
create table "derivative_quizzes_questions" ("quiz_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL);
alter table "derivative_quizzes_questions" add constraint "derivative_quizzes_question_pk" primary key("question_id","quiz_id");
create table "derivative_quizzes" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL);
create table "derivative_users_answers" ("user_id" BIGINT NOT NULL,"answer_id" BIGINT NOT NULL);
alter table "derivative_users_answers" add constraint "derivative_users_answers_pk" primary key("user_id","answer_id");
create table "derivative_users_questions" ("user_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL);
alter table "derivative_users_questions" add constraint "derivative_users_questions_pk" primary key("user_id","question_id");
create table "derivative_users_quizzes" ("user_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL);
alter table "derivative_users_quizzes" add constraint "derivative_users_quiz_pk" primary key("user_id","quiz_id");
create table "token" ("uuid" VARCHAR NOT NULL PRIMARY KEY,"email" VARCHAR NOT NULL,"creationTime" TIMESTAMP NOT NULL,"expirationTime" TIMESTAMP NOT NULL,"isSignUp" BOOLEAN NOT NULL);
create table "user" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"userId" VARCHAR NOT NULL,"providerId" VARCHAR NOT NULL,"firstName" VARCHAR NOT NULL,"lastName" VARCHAR NOT NULL,"fullName" VARCHAR NOT NULL,"email" VARCHAR,"avatarUrl" VARCHAR,"authMethod" VARCHAR NOT NULL,"token" VARCHAR,"secret" VARCHAR,"accessToken" VARCHAR,"tokenType" VARCHAR,"expiresIn" INTEGER,"refreshToken" VARCHAR,"hasher" VARCHAR,"password" VARCHAR,"salt" VARCHAR);
alter table "derivative_courses_quizzes" add constraint "derivative_courses_quizzes_course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_courses_quizzes" add constraint "derivative_courses_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "users_courses" add constraint "users_courses_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "users_courses" add constraint "users_courses_course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizzes_questions" add constraint "derivative_quizzes_question_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizzes_questions" add constraint "derivative_quizzes_question_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_answers" add constraint "derivative_users_answers_question_fk" foreign key("answer_id") references "derivative_answers"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_answers" add constraint "derivative_users_answers_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_questions" add constraint "derivative_users_questions_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_questions" add constraint "derivative_users_questions_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_quizzes" add constraint "derivative_users_quizzes_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_quizzes" add constraint "derivative_users_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "derivative_courses_quizzes" drop constraint "derivative_courses_quizzes_course_fk";
alter table "derivative_courses_quizzes" drop constraint "derivative_courses_quizzes_quiz_fk";
alter table "users_courses" drop constraint "users_courses_user_fk";
alter table "users_courses" drop constraint "users_courses_course_fk";
alter table "derivative_answers" drop constraint "derivative_answers_fk";
alter table "derivative_quizzes_questions" drop constraint "derivative_quizzes_question_quiz_fk";
alter table "derivative_quizzes_questions" drop constraint "derivative_quizzes_question_question_fk";
alter table "derivative_users_answers" drop constraint "derivative_users_answers_question_fk";
alter table "derivative_users_answers" drop constraint "derivative_users_answers_user_fk";
alter table "derivative_users_questions" drop constraint "derivative_users_questions_user_fk";
alter table "derivative_users_questions" drop constraint "derivative_users_questions_question_fk";
alter table "derivative_users_quizzes" drop constraint "derivative_users_quizzes_user_fk";
alter table "derivative_users_quizzes" drop constraint "derivative_users_quizzes_quiz_fk";
drop table "equations";
alter table "derivative_courses_quizzes" drop constraint "derivative_courses_quizzes_pk";
drop table "derivative_courses_quizzes";
drop table "courses";
alter table "users_courses" drop constraint "users_courses_pk";
drop table "users_courses";
drop table "derivative_answers";
drop table "derivative_questions";
alter table "derivative_quizzes_questions" drop constraint "derivative_quizzes_question_pk";
drop table "derivative_quizzes_questions";
drop table "derivative_quizzes";
alter table "derivative_users_answers" drop constraint "derivative_users_answers_pk";
drop table "derivative_users_answers";
alter table "derivative_users_questions" drop constraint "derivative_users_questions_pk";
drop table "derivative_users_questions";
alter table "derivative_users_quizzes" drop constraint "derivative_users_quiz_pk";
drop table "derivative_users_quizzes";
drop table "token";
drop table "user";

