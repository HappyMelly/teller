# --- !Ups

alter table EVENT add column TOTAL_HOURS SMALLINT NOT NULL after HOURS_PER_DAY;

# --- !Downs

alter table EVENT drop column TOTAL_HOURS;
