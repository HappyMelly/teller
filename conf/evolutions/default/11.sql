# --- !Ups

alter table ORGANISATION add column UPDATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table ORGANISATION add column UPDATED_BY VARCHAR(254) NOT NULL DEFAULT 'Peter Hilton';

# --- !Downs

alter table ORGANISATION drop column UPDATED;
alter table ORGANISATION drop column UPDATED_BY;
