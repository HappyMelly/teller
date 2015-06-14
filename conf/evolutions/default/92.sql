# --- !Ups
alter table ORGANISATION add column ABOUT text after CUSTOMER_ID;
alter table ORGANISATION add column LOGO tinyint(1) default 0 after ABOUT;

# --- !Downs
alter table ORGANISATION drop column ABOUT;
alter table ORGANISATION drop column LOGO;