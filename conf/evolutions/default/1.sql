# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "application_user_settings" ("user_id" BIGINT NOT NULL PRIMARY KEY,"consented" BOOLEAN NOT NULL,"name" VARCHAR NOT NULL,"allow_auto_match" BOOLEAN NOT NULL,"seen_help" BOOLEAN NOT NULL,"email_game_updates" BOOLEAN NOT NULL);
create unique index "application_user_settings" on "application_user_settings" ("name");
create table "assignment_groups" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"section_id" BIGINT NOT NULL,"assignment_id" BIGINT NOT NULL,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "assignment_groups_2_derivative_quizzes" ("group_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL);
alter table "assignment_groups_2_derivative_quizzes" add constraint "assignment_groups_2_derivative_quizzes_pk" primary key("group_id","quiz_id");
create table "assignments" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"course_id" BIGINT NOT NULL,"owner" BIGINT NOT NULL,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL,"start_date" TIMESTAMP,"end_date" TIMESTAMP);
create table "courses" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"owner" BIGINT NOT NULL,"edit_code" VARCHAR NOT NULL,"view_code" VARCHAR NOT NULL,"creation_Date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "courses_2_derivative_quizzes" ("course_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL);
alter table "courses_2_derivative_quizzes" add constraint "courses_2_derivative_quizzes_pk" primary key("course_id","quiz_id");
create table "derivative_answer_times" ("user_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"time" TIMESTAMP NOT NULL);
alter table "derivative_answer_times" add constraint "derivative_answer_times_pk" primary key("user_id","question_id");
create table "derivative_answers" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"mathml" VARCHAR NOT NULL,"rawstr" VARCHAR NOT NULL,"correct" BOOLEAN NOT NULL,"creation_date" TIMESTAMP NOT NULL);
create table "derivative_questions" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"mathml" VARCHAR NOT NULL,"rawstr" VARCHAR NOT NULL,"creation_date" TIMESTAMP NOT NULL);
create table "derivative_quizzes" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"name" VARCHAR NOT NULL,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "derivative_quizzes_2_questions" ("quiz_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL);
alter table "derivative_quizzes_2_questions" add constraint "derivative_quizzes_2_questions_pk" primary key("question_id","quiz_id");
create table "equations" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"equation" VARCHAR NOT NULL);
create table "group_question_2_user" ("group_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"user_id" BIGINT NOT NULL);
alter table "group_question_2_user" add constraint "group_question_2_user_pk" primary key("group_id","question_id","user_id");
create table "section_derivative_quizzes" ("course_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL);
alter table "section_derivative_quizzes" add constraint "section_derivative_quizzes_pk" primary key("course_id","quiz_id");
create table "sections" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"course_id" BIGINT NOT NULL,"owner" BIGINT NOT NULL,"edit_code" VARCHAR NOT NULL,"view_code" VARCHAR NOT NULL,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "secure_social_tokens" ("uuid" VARCHAR NOT NULL PRIMARY KEY,"email" VARCHAR NOT NULL,"creation_time" TIMESTAMP NOT NULL,"expiration_time" TIMESTAMP NOT NULL,"is_sign_up" BOOLEAN NOT NULL);
create table "secure_social_users" ("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"user_id" VARCHAR NOT NULL,"provider_id" VARCHAR NOT NULL,"first_name" VARCHAR NOT NULL,"last_name" VARCHAR NOT NULL,"full_name" VARCHAR NOT NULL,"email" VARCHAR,"avatar_url" VARCHAR,"auth_Method" VARCHAR NOT NULL,"token" VARCHAR,"secret" VARCHAR,"access_token" VARCHAR,"token_type" VARCHAR,"expires_in" INTEGER,"refresh_token" VARCHAR,"hasher" VARCHAR,"password" VARCHAR,"salt" VARCHAR,"creation_date" TIMESTAMP NOT NULL,"update_date" TIMESTAMP NOT NULL);
create table "users_2_assignment_groups" ("user_id" BIGINT NOT NULL,"assignment_group_id" BIGINT NOT NULL);
alter table "users_2_assignment_groups" add constraint "users_2_assignment_groups_pk" primary key("user_id","assignment_group_id");
create table "users_2_courses" ("user_id" BIGINT NOT NULL,"course_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_2_courses" add constraint "users_2_courses_pk" primary key("user_id","course_id");
create table "users_2_derivative_answers" ("user_id" BIGINT NOT NULL,"answer_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_2_derivative_answers" add constraint "users_2_derivative_answers_pk" primary key("user_id","answer_id");
create table "users_2_derivative_quizzes" ("user_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_2_derivative_quizzes" add constraint "users_2_derivative_quizzes_pk" primary key("user_id","quiz_id");
create table "users_2_sections" ("user_id" BIGINT NOT NULL,"section_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_2_sections" add constraint "users_2_sections_pk" primary key("user_id","section_id");
alter table "application_user_settings" add constraint "application_user_settings_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "assignment_groups" add constraint "assignment_groups_section_fk" foreign key("section_id") references "sections"("id") on update NO ACTION on delete CASCADE;
alter table "assignment_groups" add constraint "assignment_groups_assignment_fk" foreign key("assignment_id") references "assignments"("id") on update NO ACTION on delete CASCADE;
alter table "assignment_groups_2_derivative_quizzes" add constraint "assignment_groups_2_derivative_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "assignment_groups_2_derivative_quizzes" add constraint "assignment_groups_2_derivative_quizzes_course_fk" foreign key("group_id") references "assignment_groups"("id") on update NO ACTION on delete CASCADE;
alter table "assignments" add constraint "assignments_owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "assignments" add constraint "assignments_courses_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "courses" add constraint "courses_owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
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
alter table "group_question_2_user" add constraint "group_question_2_user_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "group_question_2_user" add constraint "group_question_2_user_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "group_question_2_user" add constraint "group_question_2_user_group_fk" foreign key("group_id") references "assignment_groups"("id") on update NO ACTION on delete CASCADE;
alter table "section_derivative_quizzes" add constraint "section_derivative_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "section_derivative_quizzes" add constraint "section_derivative_quizzes_section_fk" foreign key("course_id") references "sections"("id") on update NO ACTION on delete CASCADE;
alter table "sections" add constraint "sections_owner_fk" foreign key("owner") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "sections" add constraint "sections_courses_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_assignment_groups" add constraint "users_2_assignment_groups_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_assignment_groups" add constraint "users_2_assignment_groups_assignment_group_fk" foreign key("assignment_group_id") references "assignment_groups"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_courses" add constraint "users_2_courses_course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_courses" add constraint "users_2_courses_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_answers" add constraint "users_2_derivative_answers_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_answers" add constraint "users_2_derivative_answers_question_fk" foreign key("answer_id") references "derivative_answers"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_quizzes" add constraint "users_2_derivative_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_derivative_quizzes" add constraint "users_2_derivative_quizzes_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_sections" add constraint "users_2_sections_section_fk" foreign key("section_id") references "sections"("id") on update NO ACTION on delete CASCADE;
alter table "users_2_sections" add constraint "users_2_sections_user_fk" foreign key("user_id") references "secure_social_users"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "application_user_settings" drop constraint "application_user_settings_user_fk";
alter table "assignment_groups" drop constraint "assignment_groups_section_fk";
alter table "assignment_groups" drop constraint "assignment_groups_assignment_fk";
alter table "assignment_groups_2_derivative_quizzes" drop constraint "assignment_groups_2_derivative_quizzes_quiz_fk";
alter table "assignment_groups_2_derivative_quizzes" drop constraint "assignment_groups_2_derivative_quizzes_course_fk";
alter table "assignments" drop constraint "assignments_owner_fk";
alter table "assignments" drop constraint "assignments_courses_fk";
alter table "courses" drop constraint "courses_owner_fk";
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
alter table "group_question_2_user" drop constraint "group_question_2_user_question_fk";
alter table "group_question_2_user" drop constraint "group_question_2_user_user_fk";
alter table "group_question_2_user" drop constraint "group_question_2_user_group_fk";
alter table "section_derivative_quizzes" drop constraint "section_derivative_quizzes_quiz_fk";
alter table "section_derivative_quizzes" drop constraint "section_derivative_quizzes_section_fk";
alter table "sections" drop constraint "sections_owner_fk";
alter table "sections" drop constraint "sections_courses_fk";
alter table "users_2_assignment_groups" drop constraint "users_2_assignment_groups_user_fk";
alter table "users_2_assignment_groups" drop constraint "users_2_assignment_groups_assignment_group_fk";
alter table "users_2_courses" drop constraint "users_2_courses_course_fk";
alter table "users_2_courses" drop constraint "users_2_courses_user_fk";
alter table "users_2_derivative_answers" drop constraint "users_2_derivative_answers_user_fk";
alter table "users_2_derivative_answers" drop constraint "users_2_derivative_answers_question_fk";
alter table "users_2_derivative_quizzes" drop constraint "users_2_derivative_quizzes_quiz_fk";
alter table "users_2_derivative_quizzes" drop constraint "users_2_derivative_quizzes_user_fk";
alter table "users_2_sections" drop constraint "users_2_sections_section_fk";
alter table "users_2_sections" drop constraint "users_2_sections_user_fk";
drop table "application_user_settings";
drop table "assignment_groups";
alter table "assignment_groups_2_derivative_quizzes" drop constraint "assignment_groups_2_derivative_quizzes_pk";
drop table "assignment_groups_2_derivative_quizzes";
drop table "assignments";
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
alter table "group_question_2_user" drop constraint "group_question_2_user_pk";
drop table "group_question_2_user";
alter table "section_derivative_quizzes" drop constraint "section_derivative_quizzes_pk";
drop table "section_derivative_quizzes";
drop table "sections";
drop table "secure_social_tokens";
drop table "secure_social_users";
alter table "users_2_assignment_groups" drop constraint "users_2_assignment_groups_pk";
drop table "users_2_assignment_groups";
alter table "users_2_courses" drop constraint "users_2_courses_pk";
drop table "users_2_courses";
alter table "users_2_derivative_answers" drop constraint "users_2_derivative_answers_pk";
drop table "users_2_derivative_answers";
alter table "users_2_derivative_quizzes" drop constraint "users_2_derivative_quizzes_pk";
drop table "users_2_derivative_quizzes";
alter table "users_2_sections" drop constraint "users_2_sections_pk";
drop table "users_2_sections";

