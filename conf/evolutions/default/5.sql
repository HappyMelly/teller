# --- !Ups

alter table LOGIN_IDENTITY add column TWITTER_HANDLE VARCHAR(254) NOT NULL;

# --- !Downs

alter table LOGIN_IDENTITY drop column TWITTER_HANDLE;

