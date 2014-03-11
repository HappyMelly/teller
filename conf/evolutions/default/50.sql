# --- !Ups

alter table PRODUCT add column PICTURE VARCHAR(254) after URL;

# --- !Downs

alter table PRODUCT drop column PICTURE;