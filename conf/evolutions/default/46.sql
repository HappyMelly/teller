# T142 Google login

# --- !Ups

alter table LOGIN_IDENTITY add column GOOGLE_PLUS_URL varchar(254);
alter table USER_ACCOUNT add column GOOGLE_PLUS_URL varchar(254);

# Set URLs for existing accounts
update USER_ACCOUNT a set GOOGLE_PLUS_URL = (select GOOGLE_PLUS_URL from PERSON where ID=a.PERSON_ID) where GOOGLE_PLUS_URL is null;

create unique index IDX_GOOGLE_PLUS_URL on USER_ACCOUNT (GOOGLE_PLUS_URL);

# --- !Downs

drop index GOOGLE_PLUS_URL on USER_ACCOUNT;
alter table USER_ACCOUNT drop column GOOGLE_PLUS_URL;
alter table LOGIN_IDENTITY drop column GOOGLE_PLUS_URL;
