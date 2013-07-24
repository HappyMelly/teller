# --- !Ups


alter table PERSON modify column CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

alter table PERSON add column UPDATED TIMESTAMP;
update PERSON set UPDATED = CREATED;
alter table PERSON modify column UPDATED TIMESTAMP NOT NULL;

alter table PERSON add column UPDATED_BY VARCHAR(254) NOT NULL DEFAULT 'Peter Hilton';


create table ADDRESS (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  STREET_1 VARCHAR(254),
  STREET_2 VARCHAR(254),
  CITY VARCHAR(254),
  PROVINCE VARCHAR(254),
  POST_CODE VARCHAR(254),
  COUNTRY_CODE VARCHAR(254) NOT NULL);

alter table PERSON add column ADDRESS_ID BIGINT NOT NULL;
update PERSON set ADDRESS_ID = ID;
insert into ADDRESS (ID, STREET_1, STREET_2, CITY, PROVINCE, POST_CODE, COUNTRY_CODE) select ID, STREET_1, STREET_2, CITY, PROVINCE, POST_CODE, COUNTRY_CODE from PERSON;
alter table PERSON add constraint ADDRESS_FK foreign key(ADDRESS_ID) references ADDRESS(ID) on update NO ACTION on delete NO ACTION;


alter table PERSON drop column STREET_1;
alter table PERSON drop column STREET_2;
alter table PERSON drop column CITY;
alter table PERSON drop column PROVINCE;
alter table PERSON drop column POST_CODE;
alter table PERSON drop column COUNTRY_CODE;


# --- !Downs

alter table PERSON add column STREET_1 VARCHAR(254);
alter table PERSON add column STREET_2 VARCHAR(254);
alter table PERSON add column CITY VARCHAR(254);
alter table PERSON add column PROVINCE VARCHAR(254);
alter table PERSON add column POST_CODE VARCHAR(254);
alter table PERSON add column COUNTRY_CODE VARCHAR(254);

alter table PERSON drop foreign key ADDRESS_FK;
alter table PERSON drop column ADDRESS_ID;
drop table ADDRESS;

alter table PERSON drop column UPDATED;
alter table PERSON drop column UPDATED_BY;
