# --- !Ups
alter table EVENT_PARTICIPANT add column ROLE varchar(254);

# --- !Downs
alter table EVENT_PARTICIPANT drop column ROLE;
