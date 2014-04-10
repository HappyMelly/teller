# --- !Ups
alter table BRAND add column DESCRIPTION text after CODE;
alter table BRAND add column STATUS varchar(254) default "accepted" after DESCRIPTION;
alter table BRAND add column PICTURE varchar(254) after STATUS;

# --- !Downs
alter table BRAND drop column DESCRIPTION;
alter table BRAND drop column STATUS;
alter table BRAND drop column PICTURE;
