# STOP AUTO GEN
# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "application_user_settings" ("user_id" BIGINT NOT NULL PRIMARY KEY,"consented" BOOLEAN NOT NULL,"name" TEXT NOT NULL,"allow_auto_match" BOOLEAN NOT NULL,"seen_help" BOOLEAN NOT NULL,"email_game_updates" BOOLEAN NOT NULL);
create unique index "application_user_settings_name_index" on "application_user_settings" ("name");
create table "courses" ("id" SERIAL NOT NULL PRIMARY KEY,"name" TEXT NOT NULL,"organization" BIGINT NOT NULL,"owner" BIGINT NOT NULL,"edit_code" TEXT NOT NULL,"view_code" TEXT NOT NULL,"creation_Date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "courses_2_derivative_quizzes" ("course_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL);
alter table "courses_2_derivative_quizzes" add constraint "courses_2_derivative_quizzes_pk" primary key("course_id","quiz_id");
create table "derivative_answer_times" ("user_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"time" TIMESTAMP NOT NULL);
alter table "derivative_answer_times" add constraint "derivative_answer_times_pk" primary key("user_id","question_id");
create table "derivative_answers" ("id" SERIAL NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"mathml" TEXT NOT NULL,"rawstr" TEXT NOT NULL,"correct" BOOLEAN NOT NULL,"creation_date" TIMESTAMP NOT NULL);
create table "derivative_questions" ("id" SERIAL NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"mathml" TEXT NOT NULL,"rawstr" TEXT NOT NULL,"creation_date" TIMESTAMP NOT NULL);
create table "derivative_quizzes" ("id" SERIAL NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"name" TEXT NOT NULL,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "derivative_quizzes_2_questions" ("quiz_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL);
alter table "derivative_quizzes_2_questions" add constraint "derivative_quizzes_2_questions_pk" primary key("question_id","quiz_id");
create table "equations" ("id" SERIAL NOT NULL PRIMARY KEY,"equation" TEXT NOT NULL);
create table "games" ("id" SERIAL NOT NULL PRIMARY KEY,"request_date" TIMESTAMP NOT NULL,"requestor" BIGINT NOT NULL,"requestee" BIGINT NOT NULL,"response" SMALLINT NOT NULL,"course" BIGINT,"requestor_quiz" BIGINT,"requestor_quiz_done" BOOLEAN NOT NULL,"requestee_quiz" BIGINT,"requestee_quiz_done" BOOLEAN NOT NULL,"requestee_finished" BOOLEAN NOT NULL,"requestor_finished" BOOLEAN NOT NULL,"finished_date" TIMESTAMP);
create table "organizations" ("id" SERIAL NOT NULL PRIMARY KEY,"name" TEXT NOT NULL,"creation_Date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "secure_social_tokens" ("uuid" TEXT NOT NULL PRIMARY KEY,"email" TEXT NOT NULL,"creation_time" TIMESTAMP NOT NULL,"expiration_time" TIMESTAMP NOT NULL,"is_sign_up" BOOLEAN NOT NULL);
create table "secure_social_users" ("id" SERIAL NOT NULL PRIMARY KEY,"user_id" TEXT NOT NULL,"provider_id" TEXT NOT NULL,"first_name" TEXT NOT NULL,"last_name" TEXT NOT NULL,"full_name" TEXT NOT NULL,"email" TEXT,"avatar_url" TEXT,"auth_Method" TEXT NOT NULL,"token" TEXT,"secret" TEXT,"access_token" TEXT,"token_type" TEXT,"expires_in" INTEGER,"refresh_token" TEXT,"hasher" TEXT,"password" TEXT,"salt" TEXT,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "users_2_courses" ("user_id" BIGINT NOT NULL,"course_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_2_courses" add constraint "users_2_courses_pk" primary key("user_id","course_id");
create table "users_2_derivative_answers" ("user_id" BIGINT NOT NULL,"answer_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_2_derivative_answers" add constraint "users_2_derivative_answers_pk" primary key("user_id","answer_id");
create table "users_2_derivative_quizzes" ("user_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_2_derivative_quizzes" add constraint "users_2_derivative_quizzes_pk" primary key("user_id","quiz_id");
alter table "application_user_settings" add constraint "application_user_settings_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "courses" add constraint "courses_owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "courses" add constraint "courses_organization_fk" foreign key("organization") references "organizations"("id") on update NO ACTION on delete CASCADE;
alter table "courses_2_derivative_quizzes" add constraint "courses_2_derivative_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "courses_2_derivative_quizzes" add constraint "courses_2_derivative_quizzes_course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answer_times" add constraint "derivative_answer_times_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answer_times" add constraint "derivative_answer_times_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers_owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_questions" add constraint "derivative_questions_owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizzes" add constraint "derivative_quizzes_owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizzes_2_questions" add constraint "derivative_quizzes_2_questions_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizzes_2_questions" add constraint "derivative_quizzes_2_questions_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games_requestor_fk" foreign key("requestor") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games_requestee_fk" foreign key("requestee") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games_course_fk" foreign key("course") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games_requestor_quiz_fk" foreign key("requestor_quiz") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games_requestee_quiz_fk" foreign key("requestee_quiz") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_courses" add constraint "users_2_courses_course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_courses" add constraint "users_2_courses_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_answers" add constraint "users_2_derivative_answers_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_answers" add constraint "users_2_derivative_answers_question_fk" foreign key("answer_id") references "derivative_answers"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_quizzes" add constraint "users_2_derivative_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_quizzes" add constraint "users_2_derivative_quizzes_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "application_user_settings" drop constraint "application_user_settings_user_fk";
alter table "courses" drop constraint "courses_owner_fk";
alter table "courses" drop constraint "courses_organization_fk";
alter table "courses_2_derivative_quizzes" drop constraint "courses_2_derivative_quizzes_quiz_fk";
alter table "courses_2_derivative_quizzes" drop constraint "courses_2_derivative_quizzes_course_fk";
alter table "derivative_answer_times" drop constraint "derivative_answer_times_user_fk";
alter table "derivative_answer_times" drop constraint "derivative_answer_times_question_fk";
alter table "derivative_answers" drop constraint "derivative_answers_owner_fk";
alter table "derivative_answers" drop constraint "derivative_answers_fk";
alter table "derivative_questions" drop constraint "derivative_questions_owner_fk";
alter table "derivative_quizzes" drop constraint "derivative_quizzes_owner_fk";
alter table "derivative_quizzes_2_questions" drop constraint "derivative_quizzes_2_questions_quiz_fk";
alter table "derivative_quizzes_2_questions" drop constraint "derivative_quizzes_2_questions_question_fk";
alter table "games" drop constraint "games_requestor_fk";
alter table "games" drop constraint "games_requestee_fk";
alter table "games" drop constraint "games_course_fk";
alter table "games" drop constraint "games_requestor_quiz_fk";
alter table "games" drop constraint "games_requestee_quiz_fk";
alter table "users_2_courses" drop constraint "users_2_courses_course_fk";
alter table "users_2_courses" drop constraint "users_2_courses_user_fk";
alter table "users_2_derivative_answers" drop constraint "users_2_derivative_answers_user_fk";
alter table "users_2_derivative_answers" drop constraint "users_2_derivative_answers_question_fk";
alter table "users_2_derivative_quizzes" drop constraint "users_2_derivative_quizzes_quiz_fk";
alter table "users_2_derivative_quizzes" drop constraint "users_2_derivative_quizzes_user_fk";
drop table "application_user_settings";
drop table "courses";
alter table "courses_2_derivative_quizzes" drop constraint "courses_2_derivative_quizzes_pk";
drop table "courses_2_derivative_quizzes";
alter table "derivative_answer_times" drop constraint "derivative_answer_times_pk";
drop table "derivative_answer_times";
drop table "derivative_answers";
drop table "derivative_questions";
drop table "derivative_quizzes";
alter table "derivative_quizzes_2_questions" drop constraint "derivative_quizzes_2_questions_pk";
drop table "derivative_quizzes_2_questions";
drop table "equations";
drop table "games";
drop table "organizations";
drop table "secure_social_tokens";
drop table "secure_social_users";
alter table "users_2_courses" drop constraint "users_2_courses_pk";
drop table "users_2_courses";
alter table "users_2_derivative_answers" drop constraint "users_2_derivative_answers_pk";
drop table "users_2_derivative_answers";
alter table "users_2_derivative_quizzes" drop constraint "users_2_derivative_quizzes_pk";
drop table "users_2_derivative_quizzes";

