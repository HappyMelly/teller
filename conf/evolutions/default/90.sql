# --- !Ups
alter table PRODUCT add column ACTIVE tinyint(1) default 1 after PARENT_ID;
alter table BRAND add column ACTIVE tinyint(1) default 1 after EVALUATION_HOOK_URL;

# --- !Downs
alter table PRODUCT drop column ACTIVE;
alter table BRAND drop column ACTIVE;

