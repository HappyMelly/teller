# --- !Ups
alter table ENDORSEMENT add column EVALUATION_ID bigint default 0;

# --- !Downs
alter table ENDORSEMENT drop column EVALUATION_ID;
