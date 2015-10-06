# --- !Ups
alter table FACILITATOR change column RATING PUBLIC_RATING float(6,2) default 0.0;
alter table FACILITATOR add column PRIVATE_RATING float(6,2) default 0.0;
alter table FACILITATOR add column PUBLIC_MEDIAN float(6,2) default 0.0;
alter table FACILITATOR add column PRIVATE_MEDIAN float(6,2) default 0.0;
alter table FACILITATOR add column PUBLIC_NPS float(6,2) default 0.0;
alter table FACILITATOR add column PRIVATE_NPS float(6,2) default 0.0;
alter table FACILITATOR add column NUMBER_OF_PUBLIC_EVALUATIONS int default 0;
alter table FACILITATOR add column NUMBER_OF_PRIVATE_EVALUATIONS int default 0;
alter table MGT30_OLD_EVALUATION drop column EVENT_ID;
# --- !Downs
alter table FACILITATOR change column PUBLIC_RATING RATING float(6,2) default 0.0;
alter table FACILITATOR drop column PRIVATE_RATING;
alter table FACILITATOR drop column PUBLIC_MEDIAN;
alter table FACILITATOR drop column PRIVATE_MEDIAN;
alter table FACILITATOR drop column PUBLIC_NPS;
alter table FACILITATOR drop column PRIVATE_NPS;
alter table FACILITATOR drop column NUMBER_OF_PUBLIC_EVALUATIONS;
alter table FACILITATOR drop column NUMBER_OF_PRIVATE_EVALUATIONS;
alter table MGT30_OLD_EVALUATION add column EVENT_ID bigint not null;
