# --- !Ups
alter table BRAND drop column STATUS;

# --- !Downs
alter table BRAND add column STATUS varchar(254) default "accepted" after DESCRIPTION;
