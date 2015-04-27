# --- !Ups
alter table EVENT_TYPE add column FREE tinyint(1) default 0;

# --- !Downs
alter table EVENT_TYPE drop column FREE;