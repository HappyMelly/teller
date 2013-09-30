# T79 Add web site and blog to organisations

# --- !Ups

alter table ORGANISATION add column WEB_SITE VARCHAR(254);
alter table ORGANISATION add column BLOG VARCHAR(254);

# --- !Downs

alter table ORGANISATION drop column WEB_SITE;
alter table ORGANISATION drop column BLOG;