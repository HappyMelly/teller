# --- !Ups
alter table EVENT_CANCELLATION add column FREE tinyint(1) default 0 after END_DATE;

# --- !Downs
alter table EVENT_CANCELLATION drop column FREE;
