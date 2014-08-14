# --- !Ups
alter table BRAND drop column STATUS;

# change SOCIAL_PROFILE table
alter table SOCIAL_PROFILE change PERSON_ID OBJECT_ID bigint not null;
alter table SOCIAL_PROFILE add column OBJECT_TYPE tinyint not null default 0 after OBJECT_ID;
alter table SOCIAL_PROFILE add column SKYPE varchar(254);
alter table SOCIAL_PROFILE add column PHONE varchar(254);
alter table SOCIAL_PROFILE add column EMAIL varchar(254);
update PERSON, SOCIAL_PROFILE set EMAIL = PERSON.EMAIL_ADDRESS where SOCIAL_PROFILE.OBJECT_ID = PERSON.ID;
alter table SOCIAL_PROFILE drop PRIMARY KEY, add PRIMARY KEY (OBJECT_ID, OBJECT_TYPE);

# remove EMAIL_ADDRESS as we use SOCIAL_PROFILE now
alter table PERSON drop column EMAIL_ADDRESS;

# add SOCIAL_PROFILE records for brands
insert into SOCIAL_PROFILE (OBJECT_ID, OBJECT_TYPE, EMAIL) select ID, 1, 'skotlov@gmail.com' from BRAND;

# --- !Downs
alter table BRAND add column STATUS varchar(254) default "accepted" after DESCRIPTION;

#remove SOCIAL_PROFILE records for brands
delete from SOCIAL_PROFILE where OBJECT_TYPE = 1;

alter table SOCIAL_PROFILE drop PRIMARY KEY;
alter table SOCIAL_PROFILE change OBJECT_ID PERSON_ID bigint not null PRIMARY KEY;
alter table SOCIAL_PROFILE drop column OBJECT_TYPE;
alter table SOCIAL_PROFILE drop column SKYPE;
alter table SOCIAL_PROFILE drop column PHONE;
alter table PERSON add column EMAIL_ADDRESS varchar(254) not null default "";
update PERSON, SOCIAL_PROFILE set EMAIL_ADDRESS = SOCIAL_PROFILE.EMAIL where SOCIAL_PROFILE.PERSON_ID = PERSON.ID;
alter table SOCIAL_PROFILE drop column EMAIL;

