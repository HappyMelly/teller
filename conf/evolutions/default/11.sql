# --- !Ups

-- Repair empty values for CREATED.
update ORGANISATION set CREATED = CURRENT_TIMESTAMP where CREATED = '0000-00-00 00:00:00';

alter table ORGANISATION add column UPDATED TIMESTAMP;
update ORGANISATION set UPDATED = CREATED;
alter table ORGANISATION modify column UPDATED TIMESTAMP NOT NULL;

alter table ORGANISATION add column UPDATED_BY VARCHAR(254) NOT NULL DEFAULT 'Peter Hilton';

# --- !Downs

alter table ORGANISATION drop column UPDATED;
alter table ORGANISATION drop column UPDATED_BY;
