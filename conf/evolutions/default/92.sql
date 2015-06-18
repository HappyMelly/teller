# --- !Ups
alter table ORGANISATION add column ABOUT text after CUSTOMER_ID;
alter table ORGANISATION add column LOGO tinyint(1) default 0 after ABOUT;
insert into SOCIAL_PROFILE (OBJECT_ID, OBJECT_TYPE, EMAIL) select ID, 2, "" from ORGANISATION;

# --- !Downs
alter table ORGANISATION drop column ABOUT;
alter table ORGANISATION drop column LOGO;
delete from SOCIAL_PROFILE where OBJECT_TYPE = 2;