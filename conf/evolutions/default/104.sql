# --- !Ups
alter table EVALUATION change column QUESTION_1 REASON_TO_REGISTER text not null;
alter table EVALUATION change column QUESTION_2 ACTION_ITEMS text not null;
alter table EVALUATION change column QUESTION_3 CHANGES_TO_CONTENT text not null;
alter table EVALUATION change column QUESTION_4 FACILITATOR_REVIEW text not null;
alter table EVALUATION change column QUESTION_5 CHANGES_TO_HOST text not null;
alter table EVALUATION change column QUESTION_6 FACILITATOR_IMPRESSION tinyint not null;
alter table EVALUATION change column QUESTION_7 RECOMMENDATION_SCORE tinyint not null;
alter table EVALUATION change column QUESTION_8 CHANGES_TO_EVENT text not null;
alter table EVALUATION add column CONTENT_IMPRESSION tinyint;
alter table EVALUATION add column HOST_IMPRESSION tinyint;

# --- !Downs
alter table EVALUATION change column REASON_TO_REGISTER QUESTION_1 text not null;
alter table EVALUATION change column ACTION_ITEMS QUESTION_2 text not null;
alter table EVALUATION change column CHANGES_TO_CONTENT QUESTION_3 text not null;
alter table EVALUATION change column FACILITATOR_REVIEW QUESTION_4 text not null;
alter table EVALUATION change column CHANGES_TO_HOST QUESTION_5 text not null;
alter table EVALUATION change column FACILITATOR_IMPRESSION QUESTION_6 tinyint not null;
alter table EVALUATION change column RECOMMENDATION_SCORE QUESTION_7 tinyint not null;
alter table EVALUATION change column CHANGES_TO_EVENT QUESTION_8 text not null;
alter table EVALUATION drop column CONTENT_IMPRESSION;
alter table EVALUATION drop column HOST_IMPRESSION;