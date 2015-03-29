# --- !Ups
alter table EVENT_TYPE add column MAX_HOURS int default 16;

# --- !Downs
alter table EVENT_TYPE drop column MAX_HOURS;