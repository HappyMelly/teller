# --- !Ups
alter table EVENT add column SECOND_SPOKEN_LANGUAGE VARCHAR(254) after SPOKEN_LANGUAGE;

# --- !Downs
alter table EVENT drop column SECOND_SPOKEN_LANGUAGE;