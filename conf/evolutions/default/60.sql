# --- !Ups

alter table PERSON add column SIGNATURE tinyint(1) not null default 0 after PHOTO;

# --- !Downs
alter table PERSON drop column SIGNATURE;
