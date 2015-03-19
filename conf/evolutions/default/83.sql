# --- !Ups
alter table ORGANISATION drop column CATEGORY;

# --- !Downs
alter table ORGANISATION add column CATEGORY VARCHAR(32);
