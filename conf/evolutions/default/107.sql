# --- !Ups
alter table USER_ACCOUNT add column COORDINATOR tinyint(1) default 0;
alter table USER_ACCOUNT add column FACILITATOR tinyint(1) default 0;
alter table USER_ACCOUNT add column ACTIVE_ROLE tinyint(1) default 0;

update USER_ACCOUNT, BRAND_COORDINATOR set COORDINATOR = 1 where USER_ACCOUNT.PERSON_ID = BRAND_COORDINATOR.PERSON_ID;
update USER_ACCOUNT, LICENSE set FACILITATOR = 1 where USER_ACCOUNT.PERSON_ID = LICENSE.LICENSEE_ID;
update USER_ACCOUNT set ACTIVE_ROLE = 1 where COORDINATOR = 1;
# --- !Downs
alter table USER_ACCOUNT drop column COORDINATOR;
alter table USER_ACCOUNT drop column FACILITATOR;
alter table USER_ACCOUNT drop column ACTIVE_ROLE;