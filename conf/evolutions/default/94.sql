# --- !Ups
alter table LOGIN_IDENTITY modify column FIRST_NAME VARCHAR(254);
alter table LOGIN_IDENTITY modify column LAST_NAME VARCHAR(254);
alter table LOGIN_IDENTITY modify column FULL_NAME VARCHAR(254);
