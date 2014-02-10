# User photo

# --- !Ups

alter table PERSON add column PHOTO varchar(512) null after EMAIL_ADDRESS;

# --- !Downs

alter table PERSON drop column PHOTO;

