# --- !Ups
alter table EVENT add column ORGANIZER_ID BIGINT NOT NULL DEFAULT 0 after SPECIAL_ATTENTION;

# --- !Downs
alter table EVENT drop column ORGANIZER_ID;