# --- !Ups
alter table EXPERIMENT add column CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table EXPERIMENT add column CREATED_BY VARCHAR(254) NOT NULL DEFAULT 'Default value';
alter table EXPERIMENT add column UPDATED TIMESTAMP NOT NULL;
alter table EXPERIMENT add column UPDATED_BY VARCHAR(254) NOT NULL DEFAULT 'Default value';

update EXPERIMENT set UPDATED = CREATED;
# --- !Downs
alter table EXPERIMENT drop column CREATED;
alter table EXPERIMENT drop column CREATED_BY;
alter table EXPERIMENT drop column UPDATED;
alter table EXPERIMENT drop column UPDATED_BY;
