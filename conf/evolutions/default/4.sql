# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "user_questions" ("user_id" BIGINT NOT NULL,"question_id" BIGINT NOT NULL);
alter table "user_questions" add constraint "user_questions_pk" primary key("user_id","question_id");
alter table "user_questions" add constraint "user_questions_question_fk" foreign key("question_id") references "derivative_questions"("id") on update NO ACTION on delete CASCADE;
alter table "user_questions" add constraint "user_questions_user_fk" foreign key("user_id") references "user"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "user_questions" drop constraint "user_questions_question_fk";
alter table "user_questions" drop constraint "user_questions_user_fk";
alter table "user_questions" drop constraint "user_questions_pk";
drop table "user_questions";

