# T80 Add web site and blog to people

# --- !Ups

alter table PERSON add column WEB_SITE VARCHAR(254);
alter table PERSON add column BLOG VARCHAR(254);

# --- !Downs

alter table PERSON drop column WEB_SITE;
alter table PERSON drop column BLOG;