# T140 Facebook login

# --- !Ups

# Allow Twitter handle to be null, and add the alternative Facebook URL.
alter table LOGIN_IDENTITY modify column TWITTER_HANDLE varchar(16);
alter table LOGIN_IDENTITY add column FACEBOOK_URL varchar(254);
alter table USER_ACCOUNT modify column TWITTER_HANDLE varchar(16);
alter table USER_ACCOUNT add column FACEBOOK_URL varchar(254);

# Set Facebook URLs for existing accounts
update USER_ACCOUNT a set FACEBOOK_URL = (select FACEBOOK_URL from PERSON where ID=a.PERSON_ID) where FACEBOOK_URL is null;

# --- !Downs

alter table USER_ACCOUNT modify column TWITTER_HANDLE varchar(254) not null;
alter table USER_ACCOUNT drop column FACEBOOK_URL;
alter table LOGIN_IDENTITY drop column FACEBOOK_URL;
