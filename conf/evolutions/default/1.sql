# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "application_user_settings" ("user_id" BIGINT NOT NULL PRIMARY KEY,"consented" BOOLEAN NOT NULL,"name" VARCHAR NOT NULL,"allow_auto_match" BOOLEAN NOT NULL,"seen_help" BOOLEAN NOT NULL,"email_game_updates" BOOLEAN NOT NULL);
create unique index "application_user_settings__name_index" on "application_user_settings" ("name");
create table "courses" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"organization" BIGINT NOT NULL,"owner" BIGINT NOT NULL,"edit_code" VARCHAR NOT NULL,"view_code" VARCHAR NOT NULL,"creation_Date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "courses_2_quizzes" ("course_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL,"start_date" TIMESTAMP,"end_date" TIMESTAMP);
alter table "courses_2_quizzes" add constraint "courses_2_quizzes__pk" primary key("course_id","quiz_id");
create table "derivative_answers" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"mathml" VARCHAR NOT NULL,"rawstr" VARCHAR NOT NULL,"correct" SMALLINT NOT NULL,"creation_date" TIMESTAMP NOT NULL);
create table "derivative_questions" ("id" BIGINT NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"mathml" VARCHAR NOT NULL,"rawstr" VARCHAR NOT NULL,"creation_date" TIMESTAMP NOT NULL,"quiz_id" BIGINT,"order" INTEGER NOT NULL);
create table "derivative_quizzes" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"name" VARCHAR NOT NULL,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "games" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"request_date" TIMESTAMP NOT NULL,"requestor" BIGINT NOT NULL,"requestor_skill" DOUBLE NOT NULL,"requestee" BIGINT NOT NULL,"requestee_skill" DOUBLE NOT NULL,"response" SMALLINT NOT NULL,"course" BIGINT,"requestor_quiz" BIGINT,"requestor_quiz_done" BOOLEAN NOT NULL,"requestee_quiz" BIGINT,"requestee_quiz_done" BOOLEAN NOT NULL,"requestee_finished" BOOLEAN NOT NULL,"requestor_finished" BOOLEAN NOT NULL,"requestee_student_points" DOUBLE,"requestee_teacher_points" DOUBLE,"requestor_student_points" DOUBLE,"requestor_teacher_points" DOUBLE,"finished_date" TIMESTAMP);
create table "organizations" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"creation_Date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "question_id" ("question_id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY);
create table "secure_social_tokens" ("uuid" VARCHAR NOT NULL PRIMARY KEY,"email" VARCHAR NOT NULL,"creation_time" TIMESTAMP NOT NULL,"expiration_time" TIMESTAMP NOT NULL,"is_sign_up" BOOLEAN NOT NULL);
create table "secure_social_users" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"user_id" VARCHAR NOT NULL,"provider_id" VARCHAR NOT NULL,"first_name" VARCHAR NOT NULL,"last_name" VARCHAR NOT NULL,"full_name" VARCHAR NOT NULL,"email" VARCHAR,"avatar_url" VARCHAR,"auth_Method" VARCHAR NOT NULL,"token" VARCHAR,"secret" VARCHAR,"access_token" VARCHAR,"token_type" VARCHAR,"expires_in" INTEGER,"refresh_token" VARCHAR,"hasher" VARCHAR,"password" VARCHAR,"salt" VARCHAR,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "tangent_answers" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"mathml" VARCHAR NOT NULL,"rawstr" VARCHAR NOT NULL,"correct" SMALLINT NOT NULL,"creation_date" TIMESTAMP NOT NULL);
create table "tangent_questions" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"function" VARCHAR NOT NULL,"function_str" VARCHAR NOT NULL,"at_point_x" DOUBLE NOT NULL,"creation_date" TIMESTAMP NOT NULL,"quiz_id" BIGINT,"order" INTEGER NOT NULL);
create table "users_2_courses" ("user_id" BIGINT NOT NULL,"course_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL,"section" INTEGER NOT NULL);
alter table "users_2_courses" add constraint "users_2_courses_pk" primary key("user_id","course_id");
create table "users_2_derivative_quizzes" ("user_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_2_derivative_quizzes" add constraint "users_2_derivative_quizzes_pk" primary key("user_id","quiz_id");
alter table "application_user_settings" add constraint "application_user_settings__user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "courses" add constraint "courses__owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "courses" add constraint "courses__organization_fk" foreign key("organization") references "organizations"("id") on update NO ACTION on delete CASCADE;
alter table "courses_2_quizzes" add constraint "courses_2_quizzes__course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "courses_2_quizzes" add constraint "courses_2_quizzes__quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers__owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_questions" add constraint "derivative_questions__id_fk" foreign key("id") references "question_id"("question_id") on update NO ACTION on delete CASCADE;
alter table "derivative_questions" add constraint "derivative_questions__quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_questions" add constraint "derivative_questions__owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizzes" add constraint "derivative_quizzes__owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__requestor_fk" foreign key("requestor") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__requestee_fk" foreign key("requestee") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__course_fk" foreign key("course") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__requestor_quiz_fk" foreign key("requestor_quiz") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__requestee_quiz_fk" foreign key("requestee_quiz") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_answers" add constraint "tangent_answers_question_fk" foreign key("question_id") references "tangent_questions"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_answers" add constraint "tangent_answers__owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_questions" add constraint "tangent_questions__quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_questions" add constraint "tangent_questions__owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_courses" add constraint "users_2_courses__user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_courses" add constraint "users_2_courses__course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_quizzes" add constraint "users_2_derivative_quizzes__quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_quizzes" add constraint "users_2_derivative_quizzes__user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "application_user_settings" drop constraint "application_user_settings__user_fk";
alter table "courses" drop constraint "courses__owner_fk";
alter table "courses" drop constraint "courses__organization_fk";
alter table "courses_2_quizzes" drop constraint "courses_2_quizzes__course_fk";
alter table "courses_2_quizzes" drop constraint "courses_2_quizzes__quiz_fk";
alter table "derivative_answers" drop constraint "derivative_answers_question_fk";
alter table "derivative_answers" drop constraint "derivative_answers__owner_fk";
alter table "derivative_questions" drop constraint "derivative_questions__id_fk";
alter table "derivative_questions" drop constraint "derivative_questions__quiz_fk";
alter table "derivative_questions" drop constraint "derivative_questions__owner_fk";
alter table "derivative_quizzes" drop constraint "derivative_quizzes__owner_fk";
alter table "games" drop constraint "games__requestor_fk";
alter table "games" drop constraint "games__requestee_fk";
alter table "games" drop constraint "games__course_fk";
alter table "games" drop constraint "games__requestor_quiz_fk";
alter table "games" drop constraint "games__requestee_quiz_fk";
alter table "tangent_answers" drop constraint "tangent_answers_question_fk";
alter table "tangent_answers" drop constraint "tangent_answers__owner_fk";
alter table "tangent_questions" drop constraint "tangent_questions__quiz_fk";
alter table "tangent_questions" drop constraint "tangent_questions__owner_fk";
alter table "users_2_courses" drop constraint "users_2_courses__user_fk";
alter table "users_2_courses" drop constraint "users_2_courses__course_fk";
alter table "users_2_derivative_quizzes" drop constraint "users_2_derivative_quizzes__quiz_fk";
alter table "users_2_derivative_quizzes" drop constraint "users_2_derivative_quizzes__user_fk";
drop table "application_user_settings";
drop table "courses";
alter table "courses_2_quizzes" drop constraint "courses_2_quizzes__pk";
drop table "courses_2_quizzes";
drop table "derivative_answers";
drop table "derivative_questions";
drop table "derivative_quizzes";
drop table "games";
drop table "organizations";
drop table "question_id";
drop table "secure_social_tokens";
drop table "secure_social_users";
drop table "tangent_answers";
drop table "tangent_questions";
alter table "users_2_courses" drop constraint "users_2_courses_pk";
drop table "users_2_courses";
alter table "users_2_derivative_quizzes" drop constraint "users_2_derivative_quizzes_pk";
drop table "users_2_derivative_quizzes";

