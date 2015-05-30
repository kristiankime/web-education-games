# STOP AUTOGEN
# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "answer_id" ("id" SERIAL NOT NULL PRIMARY KEY,"dummy" SMALLINT NOT NULL);
create table "application_users" ("user_id" BIGINT NOT NULL PRIMARY KEY,"consented" BOOLEAN NOT NULL,"name" TEXT NOT NULL,"allow_auto_match" BOOLEAN NOT NULL,"seen_help" BOOLEAN NOT NULL,"email_game_updates" BOOLEAN NOT NULL);
create index "application_users__name_index" on "application_users" ("name");
create table "courses" ("id" SERIAL NOT NULL PRIMARY KEY,"name" TEXT NOT NULL,"organization" BIGINT NOT NULL,"owner" BIGINT NOT NULL,"edit_code" TEXT NOT NULL,"view_code" TEXT,"creation_Date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "courses_2_quizzes" ("course_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL,"start_date" TIMESTAMP,"end_date" TIMESTAMP);
alter table "courses_2_quizzes" add constraint "courses_2_quizzes__pk" primary key("course_id","quiz_id");
create table "derivative_answers" ("id" BIGINT NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"mathml" TEXT NOT NULL,"rawstr" TEXT NOT NULL,"correct" SMALLINT NOT NULL,"creation_date" TIMESTAMP NOT NULL);
create table "derivative_graph_answers" ("id" BIGINT NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"derivative_order" TEXT NOT NULL,"correct" SMALLINT NOT NULL,"creation_date" TIMESTAMP NOT NULL);
create table "derivative_graph_questions" ("id" BIGINT NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"mathml" TEXT NOT NULL,"rawstr" TEXT NOT NULL,"creation_date" TIMESTAMP NOT NULL,"derivativeOrder" TEXT NOT NULL,"at_creation_difficulty" DOUBLE PRECISION NOT NULL,"quiz_id" BIGINT,"order" INTEGER NOT NULL);
create table "derivative_questions" ("id" BIGINT NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"mathml" TEXT NOT NULL,"rawstr" TEXT NOT NULL,"creation_date" TIMESTAMP NOT NULL,"at_creation_difficulty" DOUBLE PRECISION NOT NULL,"quiz_id" BIGINT,"order" INTEGER NOT NULL);
create table "games" ("id" SERIAL NOT NULL PRIMARY KEY,"request_date" TIMESTAMP NOT NULL,"requestor" BIGINT NOT NULL,"requestor_skill" DOUBLE PRECISION NOT NULL,"requestee" BIGINT NOT NULL,"requestee_skill" DOUBLE PRECISION NOT NULL,"response" SMALLINT NOT NULL,"course" BIGINT,"requestor_quiz" BIGINT,"requestor_quiz_done" BOOLEAN NOT NULL,"requestee_quiz" BIGINT,"requestee_quiz_done" BOOLEAN NOT NULL,"requestee_finished" BOOLEAN NOT NULL,"requestor_finished" BOOLEAN NOT NULL,"requestee_student_points" DOUBLE PRECISION,"requestee_teacher_points" DOUBLE PRECISION,"requestor_student_points" DOUBLE PRECISION,"requestor_teacher_points" DOUBLE PRECISION,"finished_date" TIMESTAMP);
create table "organizations" ("id" SERIAL NOT NULL PRIMARY KEY,"name" TEXT NOT NULL,"creation_Date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "question_id" ("id" SERIAL NOT NULL PRIMARY KEY,"dummy" SMALLINT NOT NULL);
create table "quizzes" ("id" SERIAL NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"name" TEXT NOT NULL,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "secure_social_logins" ("id" SERIAL NOT NULL PRIMARY KEY,"user_id" TEXT NOT NULL,"provider_id" TEXT NOT NULL,"first_name" TEXT NOT NULL,"last_name" TEXT NOT NULL,"full_name" TEXT NOT NULL,"email" TEXT,"avatar_url" TEXT,"auth_Method" TEXT NOT NULL,"token" TEXT,"secret" TEXT,"access_token" TEXT,"token_type" TEXT,"expires_in" INTEGER,"refresh_token" TEXT,"hasher" TEXT,"password" TEXT,"salt" TEXT,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "secure_social_tokens" ("uuid" TEXT NOT NULL PRIMARY KEY,"email" TEXT NOT NULL,"creation_time" TIMESTAMP NOT NULL,"expiration_time" TIMESTAMP NOT NULL,"is_sign_up" BOOLEAN NOT NULL);
create table "tangent_answers" ("id" BIGINT NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"slope_mathml" TEXT NOT NULL,"slope_rawstr" TEXT NOT NULL,"intercept_mathml" TEXT NOT NULL,"intercept_rawstr" TEXT NOT NULL,"correct" SMALLINT NOT NULL,"creation_date" TIMESTAMP NOT NULL);
create table "tangent_questions" ("id" BIGINT NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"function" TEXT NOT NULL,"function_str" TEXT NOT NULL,"at_point_x" TEXT NOT NULL,"at_point_x_str" TEXT NOT NULL,"creation_date" TIMESTAMP NOT NULL,"at_creation_difficulty" DOUBLE PRECISION NOT NULL,"quiz_id" BIGINT,"order" INTEGER NOT NULL);
create table "users_2_courses" ("user_id" BIGINT NOT NULL,"course_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL,"section" INTEGER NOT NULL);
alter table "users_2_courses" add constraint "users_2_courses_pk" primary key("user_id","course_id");
create table "users_2_quizzes" ("user_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_2_quizzes" add constraint "users_2_quizzes__pk" primary key("user_id","quiz_id");
alter table "application_users" add constraint "application_users__user_fk" foreign key("user_id") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "courses" add constraint "courses__organization_fk" foreign key("organization") references "organizations"("id") on update NO ACTION on delete CASCADE;
alter table "courses" add constraint "courses__owner_fk" foreign key("owner") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "courses_2_quizzes" add constraint "courses_2_quizzes__course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "courses_2_quizzes" add constraint "courses_2_quizzes__quiz_fk" foreign key("quiz_id") references "quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers__owner_fk" foreign key("owner") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers__id_fk" foreign key("id") references "answer_id"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers__question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_graph_answers" add constraint "derivative_graph_answers__owner_fk" foreign key("owner") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_graph_answers" add constraint "derivative_graph_answers__id_fk" foreign key("id") references "answer_id"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_graph_answers" add constraint "derivative_graph_answers__question_fk" foreign key("question_id") references "derivative_graph_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_graph_questions" add constraint "derivative_graph_questions__quiz_fk" foreign key("quiz_id") references "quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_graph_questions" add constraint "derivative_graph_questions__owner_fk" foreign key("owner") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_graph_questions" add constraint "derivative_graph_questions__id_fk" foreign key("id") references "question_id"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_questions" add constraint "derivative_questions__quiz_fk" foreign key("quiz_id") references "quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_questions" add constraint "derivative_questions__owner_fk" foreign key("owner") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_questions" add constraint "derivative_questions__id_fk" foreign key("id") references "question_id"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__requestor_fk" foreign key("requestor") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__requestee_fk" foreign key("requestee") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__course_fk" foreign key("course") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__requestor_quiz_fk" foreign key("requestor_quiz") references "quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "games" add constraint "games__requestee_quiz_fk" foreign key("requestee_quiz") references "quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "quizzes" add constraint "quizzes__owner_fk" foreign key("owner") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_answers" add constraint "tangent_answers__owner_fk" foreign key("owner") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_answers" add constraint "tangent_answers__id_fk" foreign key("id") references "answer_id"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_answers" add constraint "tangent_answers__question_fk" foreign key("question_id") references "tangent_questions"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_questions" add constraint "tangent_questions__quiz_fk" foreign key("quiz_id") references "quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_questions" add constraint "tangent_questions__owner_fk" foreign key("owner") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "tangent_questions" add constraint "tangent_questions__id_fk" foreign key("id") references "question_id"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_courses" add constraint "users_2_courses__user_fk" foreign key("user_id") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_courses" add constraint "users_2_courses__course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_quizzes" add constraint "users_2_quizzes__user_fk" foreign key("user_id") references "secure_social_logins"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_quizzes" add constraint "users_2_quizzes__quiz_fk" foreign key("quiz_id") references "quizzes"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "application_users" drop constraint "application_users__user_fk";
alter table "courses" drop constraint "courses__organization_fk";
alter table "courses" drop constraint "courses__owner_fk";
alter table "courses_2_quizzes" drop constraint "courses_2_quizzes__course_fk";
alter table "courses_2_quizzes" drop constraint "courses_2_quizzes__quiz_fk";
alter table "derivative_answers" drop constraint "derivative_answers__owner_fk";
alter table "derivative_answers" drop constraint "derivative_answers__id_fk";
alter table "derivative_answers" drop constraint "derivative_answers__question_fk";
alter table "derivative_graph_answers" drop constraint "derivative_graph_answers__owner_fk";
alter table "derivative_graph_answers" drop constraint "derivative_graph_answers__id_fk";
alter table "derivative_graph_answers" drop constraint "derivative_graph_answers__question_fk";
alter table "derivative_graph_questions" drop constraint "derivative_graph_questions__quiz_fk";
alter table "derivative_graph_questions" drop constraint "derivative_graph_questions__owner_fk";
alter table "derivative_graph_questions" drop constraint "derivative_graph_questions__id_fk";
alter table "derivative_questions" drop constraint "derivative_questions__quiz_fk";
alter table "derivative_questions" drop constraint "derivative_questions__owner_fk";
alter table "derivative_questions" drop constraint "derivative_questions__id_fk";
alter table "games" drop constraint "games__requestor_fk";
alter table "games" drop constraint "games__requestee_fk";
alter table "games" drop constraint "games__course_fk";
alter table "games" drop constraint "games__requestor_quiz_fk";
alter table "games" drop constraint "games__requestee_quiz_fk";
alter table "quizzes" drop constraint "quizzes__owner_fk";
alter table "tangent_answers" drop constraint "tangent_answers__owner_fk";
alter table "tangent_answers" drop constraint "tangent_answers__id_fk";
alter table "tangent_answers" drop constraint "tangent_answers__question_fk";
alter table "tangent_questions" drop constraint "tangent_questions__quiz_fk";
alter table "tangent_questions" drop constraint "tangent_questions__owner_fk";
alter table "tangent_questions" drop constraint "tangent_questions__id_fk";
alter table "users_2_courses" drop constraint "users_2_courses__user_fk";
alter table "users_2_courses" drop constraint "users_2_courses__course_fk";
alter table "users_2_quizzes" drop constraint "users_2_quizzes__user_fk";
alter table "users_2_quizzes" drop constraint "users_2_quizzes__quiz_fk";
drop table "answer_id";
drop table "application_users";
drop table "courses";
alter table "courses_2_quizzes" drop constraint "courses_2_quizzes__pk";
drop table "courses_2_quizzes";
drop table "derivative_answers";
drop table "derivative_graph_answers";
drop table "derivative_graph_questions";
drop table "derivative_questions";
drop table "games";
drop table "organizations";
drop table "question_id";
drop table "quizzes";
drop table "secure_social_logins";
drop table "secure_social_tokens";
drop table "tangent_answers";
drop table "tangent_questions";
alter table "users_2_courses" drop constraint "users_2_courses_pk";
drop table "users_2_courses";
alter table "users_2_quizzes" drop constraint "users_2_quizzes__pk";
drop table "users_2_quizzes";

