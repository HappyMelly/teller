# --- !Ups

alter table ORGANISATION add column CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table ORGANISATION add column CREATED_BY VARCHAR(254) NOT NULL DEFAULT 'Peter Hilton';

# --- !Downs

alter table ORGANISATION drop column CREATED;
alter table ORGANISATION drop column CREATED_BY;
