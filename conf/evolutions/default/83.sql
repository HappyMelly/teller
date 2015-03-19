# --- !Ups
alter table ORGANISATION drop column CATEGORY;
alter table PERSON drop column ROLE;
# --- !Downs
alter table ORGANISATION add column CATEGORY VARCHAR(32);
alter table PERSON add column ROLE tinyint NOT NULL DEFAULT 0 after INTERESTS;
