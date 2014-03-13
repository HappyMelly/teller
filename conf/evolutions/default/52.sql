# --- !Ups

alter table PRODUCT add column DESCRIPTION TEXT after URL;
alter table PRODUCT add column CALL_TO_ACTION_URL VARCHAR(254) after DESCRIPTION;
alter table PRODUCT add column CALL_TO_ACTION_TEXT VARCHAR(254) after CALL_TO_ACTION_URL;
update PRODUCT set CATEGORY = 'apps' WHERE CATEGORY='software';

# --- !Downs
alter table PRODUCT drop column DESCRIPTION;
alter table PRODUCT drop column CALL_TO_ACTION_URL;
alter table PRODUCT drop column CALL_TO_ACTION_TEXT;
update PRODUCT set CATEGORY = 'software' WHERE CATEGORY='apps';
