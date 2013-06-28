# --- !Ups

create table ACTIVITY (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  SUBJECT VARCHAR(254) NOT NULL,
  PREDICATE VARCHAR(254) NOT NULL,
  OBJECT VARCHAR(254),
  CREATED TIMESTAMP NOT NULL);

-- Remove previous sign-ups so that they be logged in the activity stream
delete from LOGIN_IDENTITY;

# --- !Downs

drop table ACTIVITY;

