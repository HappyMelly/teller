# T81 Brand entities

# --- !Ups

alter table ORGANISATION add column CATEGORY VARCHAR(32);
update ORGANISATION set CATEGORY='legalentity' where LEGAL_ENTITY=1;
alter table ORGANISATION drop column LEGAL_ENTITY;
update ORGANISATION set category = 'brandentity' where name='Apple';

# --- !Downs

alter table ORGANISATION add column LEGAL_ENTITY BOOLEAN NOT NULL DEFAULT 0;
update ORGANISATION set LEGAL_ENTITY=1 where CATEGORY='legalentity';
alter table ORGANISATION drop column CATEGORY;