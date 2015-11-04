# --- !Ups
alter table USER_ACCOUNT add column ADMIN tinyint(1) default 0 after FACILITATOR;
alter table USER_ACCOUNT add column MEMBER tinyint(1) default 0 after ADMIN;
alter table USER_ACCOUNT add column REGISTERED tinyint(1) default 0 after MEMBER;

update USER_ACCOUNT set REGISTERED = 1 where COORDINATOR = 1;
update USER_ACCOUNT set REGISTERED = 1 where FACILITATOR = 1;
update USER_ACCOUNT a, MEMBER m set a.MEMBER = 1, a.REGISTERED = 1 where a.PERSON_ID = m.OBJECT_ID and m.PERSON = 1;
# --- !Downs
alter table USER_ACCOUNT drop column ADMIN;
alter table USER_ACCOUNT drop column MEMBER;
alter table USER_ACCOUNT drop column REGISTERED;