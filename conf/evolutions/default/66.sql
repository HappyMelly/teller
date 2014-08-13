# --- !Ups

create table if not exists EVALUATION_QUESTION (
  LANGUAGE char(2) not null primary key,
  QUESTION_1 varchar(254),
  QUESTION_2 varchar(254),
  QUESTION_3 varchar(254),
  QUESTION_4 varchar(254),
  QUESTION_5 varchar(254),
  QUESTION_6 varchar(254),
  QUESTION_7 varchar(254),
  QUESTION_8 varchar(254));

insert into EVALUATION_QUESTION values ("EN", "What triggered you to register for this event?",
    "Which action items did you take away from the event?", "What do you suggest to add or change to the program?",
    "What were specific qualities of the facilitator?", "What can be improved about the event location?",
    "General impression", "How likely are you to recommend this event to others?",
    "How could the trainer/facilitator have made the event better?");

create table if not exists EVALUATION_RECOMMENDATION (
  LANGUAGE char(2) not null primary key,
  SCORE_0 varchar(254),
  SCORE_1 varchar(254),
  SCORE_2 varchar(254),
  SCORE_3 varchar(254),
  SCORE_4 varchar(254),
  SCORE_5 varchar(254),
  SCORE_6 varchar(254),
  SCORE_7 varchar(254),
  SCORE_8 varchar(254),
  SCORE_9 varchar(254),
  SCORE_10 varchar(254));

insert into EVALUATION_RECOMMENDATION values ("EN", "Certainly not", "Highly unlikely", "Unlikely", "Quite unlikely",
    "Possibly not", "Maybe", "Yes, possibly", "Quite possibly", "Likely", "Highly likely", "Certainly");


create table if not exists EVALUATION_IMPRESSION (
  LANGUAGE char(2) not null primary key,
  SCORE_0 varchar(254),
  SCORE_1 varchar(254),
  SCORE_2 varchar(254),
  SCORE_3 varchar(254),
  SCORE_4 varchar(254),
  SCORE_5 varchar(254),
  SCORE_6 varchar(254),
  SCORE_7 varchar(254),
  SCORE_8 varchar(254),
  SCORE_9 varchar(254),
  SCORE_10 varchar(254));

insert into EVALUATION_IMPRESSION values ("EN", "Terrible", "Very bad", "Bad", "Disappointing",
    "Below average", "Average", "Above average", "Fine", "Good", "Very good", "Excellent");

# --- !Downs
drop table if exists EVALUATION_QUESTION;
drop table if exists EVALUATION_IMPRESSION;
drop table if exists EVALUATION_RECOMMENDATION;