# --- !Ups

create table USER_ACCOUNT (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  PERSON_ID BIGINT NOT NULL,
  TWITTER_HANDLE VARCHAR(254) NOT NULL,
  ROLE VARCHAR(254) NOT NULL);

create unique index IDX_PERSON_ID on USER_ACCOUNT (PERSON_ID);
create unique index IDX_TWITTER_HANDLE on USER_ACCOUNT (TWITTER_HANDLE);

-- alter table USER_ACCOUNT add constraint PERSON_FK foreign key(PERSON_ID) references PERSON(ID) on update NO ACTION on delete NO ACTION;

-- Developers must be inserted manually into the development database, so that the script doesn’t fail on the test server.
-- insert into address values();
-- insert into PERSON (FIRST_NAME, LAST_NAME, TWITTER_HANDLE, ADDRESS_ID, UPDATED) values ('Peter', 'Hilton', 'peterhilton', 32, now());
-- insert into PERSON (FIRST_NAME, LAST_NAME, TWITTER_HANDLE, ADDRESS_ID, UPDATED) values ('Sietse de', 'Kaper', 'targeter', 33, now());

insert into USER_ACCOUNT (PERSON_ID, TWITTER_HANDLE, ROLE) select ID, TWITTER_HANDLE, 'admin'  from PERSON where lower(TWITTER_HANDLE) = 'jurgenappelo';
insert into USER_ACCOUNT (PERSON_ID, TWITTER_HANDLE, ROLE) select ID, TWITTER_HANDLE, 'admin'  from PERSON where lower(TWITTER_HANDLE) = 'peterhilton';
insert into USER_ACCOUNT (PERSON_ID, TWITTER_HANDLE, ROLE) select ID, TWITTER_HANDLE, 'editor' from PERSON where lower(TWITTER_HANDLE) = 'targeter';
insert into USER_ACCOUNT (PERSON_ID, TWITTER_HANDLE, ROLE) select ID, TWITTER_HANDLE, 'editor' from PERSON where lower(TWITTER_HANDLE) = 'skotlov';
insert into USER_ACCOUNT (PERSON_ID, TWITTER_HANDLE, ROLE) select ID, TWITTER_HANDLE, 'viewer' from PERSON where lower(TWITTER_HANDLE) = 'maartenvolders';

# --- !Downs

drop table USER_ACCOUNT;
