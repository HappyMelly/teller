# --- !Ups
alter table PERSON add column ROLE tinyint NOT NULL DEFAULT 0 after GOOGLE_PLUS_URL;
alter table PERSON add column BIRTHDAY DATE NULL after EMAIL_ADDRESS;

update PERSON set ROLE = 1 WHERE STAKEHOLDER = 1;
update PERSON set ROLE = 2 WHERE BOARD_MEMBER = 1;
alter table PERSON drop column STAKEHOLDER;
alter table PERSON drop column BOARD_MEMBER;

# --- !Downs
alter table PERSON add column STAKEHOLDER tinyint NOT NULL DEFAULT 1 after GOOGLE_PLUS_URL;
alter table PERSON add column BOARD_MEMBER tinyint NOT NULL DEFAULT 0 after STAKEHOLDER;
update PERSON set STAKEHOLDER = 0;
update PERSON set STAKEHOLDER = 1 where ROLE = 1;
update PERSON set BOARD_MEMBER = 1 where ROLE = 2;
alter table PERSON drop column ROLE;
alter table PERSON drop column BIRTHDAY;
