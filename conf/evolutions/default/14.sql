# --- !Ups

alter table BRAND add column CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table BRAND add column CREATED_BY VARCHAR(254) NOT NULL DEFAULT 'Peter Hilton';
alter table BRAND add column UPDATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table BRAND add column UPDATED_BY VARCHAR(254) NOT NULL DEFAULT 'Peter Hilton';


# --- !Downs

alter table ORGANISATION drop column CREATED;
alter table ORGANISATION drop column CREATED_BY;
alter table ORGANISATION drop column UPDATED;
alter table ORGANISATION drop column UPDATED_BY;
