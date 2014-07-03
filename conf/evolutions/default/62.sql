# --- !Ups

alter table EVALUATION modify PARTICIPANT_ID BIGINT NOT NULL;

# --- !Downs
alter table EVALUATION modify PARTICIPANT_ID BIGINT NULL;
