# T140 Facebook login

# --- !Ups

alter table LOGIN_IDENTITY add column LINKEDIN_URL varchar(254);
alter table USER_ACCOUNT add column LINKEDIN_URL varchar(254);

# Set URLs for existing accounts
update USER_ACCOUNT a set LINKEDIN_URL = (select LINKEDIN_URL from PERSON where ID=a.PERSON_ID) where LINKEDIN_URL is null;

create unique index IDX_FACEBOOK_URL on USER_ACCOUNT (FACEBOOK_URL);
create unique index IDX_LINKEDIN_URL on USER_ACCOUNT (LINKEDIN_URL);

# --- !Downs

drop index IDX_FACEBOOK_URL on USER_ACCOUNT;
drop index IDX_LINKEDIN_URL on USER_ACCOUNT;

alter table USER_ACCOUNT drop column FACEBOOK_URL;
alter table LOGIN_IDENTITY drop column FACEBOOK_URL;
