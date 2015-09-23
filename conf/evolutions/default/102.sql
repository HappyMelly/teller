# --- !Ups
alter table EVENT add column FOLLOW_UP tinyint(1) default 0 after FREE;

# --- !Downs
alter table EVENT drop column FOLLOW_UP;
