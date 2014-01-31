# To start Slick DDL generation again remove this comment
# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "equations" ("id" SERIAL NOT NULL PRIMARY KEY,"equation" TEXT NOT NULL);
create table "derivative_courses_quizzes" ("course_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL);
alter table "derivative_courses_quizzes" add constraint "derivative_courses_quizzes_pk" primary key("course_id","quiz_id");
create table "courses" ("id" SERIAL NOT NULL PRIMARY KEY,"name" TEXT NOT NULL,"owner" BIGINT NOT NULL,"editCode" TEXT NOT NULL,"viewCode" TEXT NOT NULL,"creationDate" TIMESTAMP NOT NULL,"updateDate" TIMESTAMP NOT NULL);
create table "derivative_section_quizzes" ("course_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL);
alter table "derivative_section_quizzes" add constraint "derivative_section_quizzes_pk" primary key("course_id","quiz_id");
create table "sections" ("id" SERIAL NOT NULL PRIMARY KEY,"name" TEXT NOT NULL,"courseId" BIGINT NOT NULL,"owner" BIGINT NOT NULL,"editCode" TEXT NOT NULL,"viewCode" TEXT NOT NULL,"creationDate" TIMESTAMP NOT NULL,"updateDate" TIMESTAMP NOT NULL);
create table "users_courses" ("user_id" BIGINT NOT NULL,"course_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_courses" add constraint "users_courses_pk" primary key("user_id","course_id");
create table "users_sections" ("user_id" BIGINT NOT NULL,"section_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "users_sections" add constraint "users_sections_pk" primary key("user_id","section_id");
create table "derivative_answers" ("id" SERIAL NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"mathml" TEXT NOT NULL,"rawstr" TEXT NOT NULL,"synched" BOOLEAN NOT NULL,"correct" BOOLEAN NOT NULL,"creationDate" TIMESTAMP NOT NULL);
create table "derivative_questions" ("id" SERIAL NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"mathml" TEXT NOT NULL,"rawstr" TEXT NOT NULL,"synched" BOOLEAN NOT NULL,"creationDate" TIMESTAMP NOT NULL);
create table "derivative_quizzes_questions" ("quiz_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL);
alter table "derivative_quizzes_questions" add constraint "derivative_quizzes_question_pk" primary key("question_id","quiz_id");
create table "derivative_quizzes" ("id" SERIAL NOT NULL PRIMARY KEY,"owner" BIGINT NOT NULL,"name" TEXT NOT NULL,"creationDate" TIMESTAMP NOT NULL,"upadateDate" TIMESTAMP NOT NULL);
create table "derivative_users_answers" ("user_id" BIGINT NOT NULL,"answer_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "derivative_users_answers" add constraint "derivative_users_answers_pk" primary key("user_id","answer_id");
create table "derivative_users_questions" ("user_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "derivative_users_questions" add constraint "derivative_users_questions_pk" primary key("user_id","question_id");
create table "derivative_users_quizzes" ("user_id" BIGINT NOT NULL,"quiz_id" BIGINT NOT NULL,"access" SMALLINT NOT NULL);
alter table "derivative_users_quizzes" add constraint "derivative_users_quiz_pk" primary key("user_id","quiz_id");
create table "token" ("uuid" TEXT NOT NULL PRIMARY KEY,"email" TEXT NOT NULL,"creationTime" TIMESTAMP NOT NULL,"expirationTime" TIMESTAMP NOT NULL,"isSignUp" BOOLEAN NOT NULL);
create table "user" ("id" SERIAL NOT NULL PRIMARY KEY,"userId" TEXT NOT NULL,"providerId" TEXT NOT NULL,"firstName" TEXT NOT NULL,"lastName" TEXT NOT NULL,"fullName" TEXT NOT NULL,"email" TEXT,"avatarUrl" TEXT,"authMethod" TEXT NOT NULL,"token" TEXT,"secret" TEXT,"accessToken" TEXT,"tokenType" TEXT,"expiresIn" INTEGER,"refreshToken" TEXT,"hasher" TEXT,"password" TEXT,"salt" TEXT,"creationDate" TIMESTAMP NOT NULL,"updateDate" TIMESTAMP NOT NULL);
alter table "derivative_courses_quizzes" add constraint "derivative_courses_quizzes_course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_courses_quizzes" add constraint "derivative_courses_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "courses" add constraint "courses_owner_fk" foreign key("owner") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_section_quizzes" add constraint "derivative_section_quizzes_section_fk" foreign key("course_id") references "sections"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_section_quizzes" add constraint "derivative_section_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "sections" add constraint "sections_owner_fk" foreign key("owner") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "users_courses" add constraint "users_courses_course_fk" foreign key("course_id") references "courses"("id") on update NO ACTION on delete CASCADE;
alter table "users_courses" add constraint "users_courses_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "users_sections" add constraint "users_sections_section_fk" foreign key("section_id") references "sections"("id") on update NO ACTION on delete CASCADE;
alter table "users_sections" add constraint "users_sections_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers_owner_fk" foreign key("owner") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_answers" add constraint "derivative_answers_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_questions" add constraint "derivative_questions_owner_fk" foreign key("owner") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizzes_questions" add constraint "derivative_quizzes_question_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizzes_questions" add constraint "derivative_quizzes_question_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_quizzes" add constraint "derivative_quizzes_owner_fk" foreign key("owner") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_answers" add constraint "derivative_users_answers_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_answers" add constraint "derivative_users_answers_question_fk" foreign key("answer_id") references "derivative_answers"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_questions" add constraint "derivative_users_questions_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_questions" add constraint "derivative_users_questions_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_quizzes" add constraint "derivative_users_quizzes_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;
alter table "derivative_users_quizzes" add constraint "derivative_users_quizzes_quiz_fk" foreign key("quiz_id") references "derivative_quizzes"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "derivative_courses_quizzes" drop constraint "derivative_courses_quizzes_course_fk";
alter table "derivative_courses_quizzes" drop constraint "derivative_courses_quizzes_quiz_fk";
alter table "courses" drop constraint "courses_owner_fk";
alter table "derivative_section_quizzes" drop constraint "derivative_section_quizzes_section_fk";
alter table "derivative_section_quizzes" drop constraint "derivative_section_quizzes_quiz_fk";
alter table "sections" drop constraint "sections_owner_fk";
alter table "users_courses" drop constraint "users_courses_course_fk";
alter table "users_courses" drop constraint "users_courses_user_fk";
alter table "users_sections" drop constraint "users_sections_section_fk";
alter table "users_sections" drop constraint "users_sections_user_fk";
alter table "derivative_answers" drop constraint "derivative_answers_owner_fk";
alter table "derivative_answers" drop constraint "derivative_answers_fk";
alter table "derivative_questions" drop constraint "derivative_questions_owner_fk";
alter table "derivative_quizzes_questions" drop constraint "derivative_quizzes_question_question_fk";
alter table "derivative_quizzes_questions" drop constraint "derivative_quizzes_question_quiz_fk";
alter table "derivative_quizzes" drop constraint "derivative_quizzes_owner_fk";
alter table "derivative_users_answers" drop constraint "derivative_users_answers_user_fk";
alter table "derivative_users_answers" drop constraint "derivative_users_answers_question_fk";
alter table "derivative_users_questions" drop constraint "derivative_users_questions_question_fk";
alter table "derivative_users_questions" drop constraint "derivative_users_questions_user_fk";
alter table "derivative_users_quizzes" drop constraint "derivative_users_quizzes_user_fk";
alter table "derivative_users_quizzes" drop constraint "derivative_users_quizzes_quiz_fk";
drop table "equations";
alter table "derivative_courses_quizzes" drop constraint "derivative_courses_quizzes_pk";
drop table "derivative_courses_quizzes";
drop table "courses";
alter table "derivative_section_quizzes" drop constraint "derivative_section_quizzes_pk";
drop table "derivative_section_quizzes";
drop table "sections";
alter table "users_courses" drop constraint "users_courses_pk";
drop table "users_courses";
alter table "users_sections" drop constraint "users_sections_pk";
drop table "users_sections";
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

