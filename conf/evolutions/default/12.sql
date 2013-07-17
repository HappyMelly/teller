# --- !Ups

alter table PERSON add column EMAIL_ADDRESS VARCHAR(254) NOT NULL;
alter table PERSON add column STREET_1 VARCHAR(254);
alter table PERSON add column STREET_2 VARCHAR(254);
alter table PERSON add column CITY VARCHAR(254);
alter table PERSON add column PROVINCE VARCHAR(254);
alter table PERSON add column POST_CODE VARCHAR(254);

alter table PERSON add column BIO TEXT;
alter table PERSON add column INTERESTS TEXT;
alter table PERSON add column TWITTER_HANDLE VARCHAR(254);
alter table PERSON add column FACEBOOK_URL VARCHAR(254);
alter table PERSON add column LINKEDIN_URL VARCHAR(254);
alter table PERSON add column GOOGLE_PLUS_URL VARCHAR(254);
alter table PERSON add column BOARD_MEMBER BOOLEAN NOT NULL DEFAULT 0;
alter table PERSON add column STAKEHOLDER BOOLEAN NOT NULL DEFAULT 1;

alter table PERSON add column CREATED TIMESTAMP NOT NULL;
update PERSON set CREATED = CURRENT_TIMESTAMP;
alter table PERSON add column CREATED_BY VARCHAR(254) NOT NULL;

# --- !Downs

alter table PERSON drop column EMAIL_ADDRESS;
alter table PERSON drop column STREET_1;
alter table PERSON drop column STREET_2;
alter table PERSON drop column CITY;
alter table PERSON drop column PROVINCE;
alter table PERSON drop column POST_CODE;

alter table PERSON drop column BIO;
alter table PERSON drop column INTERESTS;
alter table PERSON drop column TWITTER_HANDLE;
alter table PERSON drop column FACEBOOK_URL;
alter table PERSON drop column LINKEDIN_URL;
alter table PERSON drop column GOOGLE_PLUS_URL;
alter table PERSON drop column BOARD_MEMBER;
alter table PERSON drop column STAKEHOLDER;

alter table PERSON drop column CREATED;
alter table PERSON drop column CREATED_BY;
