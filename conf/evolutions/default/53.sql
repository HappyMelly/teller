# --- !Ups
alter table EVENT add column CONFIRMED boolean not null default false after ARCHIVED;

# --- !Downs
alter table EVENT drop column CONFIRMED;
