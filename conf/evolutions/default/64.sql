# --- !Ups
alter table EVENT_PARTICIPANT add column ORGANISATION varchar(254) default "";
alter table EVENT_PARTICIPANT add column COMMENT text;

# --- !Downs
alter table EVENT_PARTICIPANT drop column ORGANISATION;
alter table EVENT_PARTICIPANT drop column COMMENT;
