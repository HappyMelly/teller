# --- !Ups
alter table PERSON add column VIRTUAL tinyint(1) default 0;

# --- !Downs
alter table PERSON drop column VIRTUAL;
