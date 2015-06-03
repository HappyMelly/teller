# --- !Ups
alter table PRODUCT add column ACTIVE tinyint(1) default 1 after PARENT_ID;

# --- !Downs
alter table PRODUCT drop column ACTIVE;
