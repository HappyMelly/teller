# T127 Missing accounts

# --- !Ups

insert into ACCOUNT (PERSON_ID)
select ID from PERSON where ID not in (select PERSON_ID from ACCOUNT where PERSON_ID is not null);

insert into ACCOUNT (ORGANISATION_ID)
select ID from ORGANISATION where ID not in (select ORGANISATION_ID from ACCOUNT where ORGANISATION_ID is not null);

# --- !Downs

