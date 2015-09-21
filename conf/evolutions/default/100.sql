# --- !Ups
alter table ENDORSEMENT add column EVALUATION_ID bigint default 0;
alter table ENDORSEMENT add column RATING int;
# --- !Downs
alter table ENDORSEMENT drop column EVALUATION_ID;
alter table ENDORSEMENT drop column RATING;
