# --- !Ups

create table PERSON (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  FIRST_NAME VARCHAR(254) NOT NULL,
  LAST_NAME VARCHAR(254) NOT NULL,
  COUNTRY_CODE VARCHAR(254) NOT NULL,
  ACTIVE BOOLEAN NOT NULL DEFAULT 1);

-- Test data from http://en.wikipedia.org/wiki/List_of_largest_companies_by_revenue
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (1, 'Rex W.', 'Tillerson', 'US');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (2, 'Peter', 'Voser', 'NL');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (3, 'Michael', 'Duke', 'US');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (4, 'Su', 'Shulin', 'CN');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (5, 'Bob', 'Dudley', 'GB');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (6, 'Jiang', 'Jiemin', 'CN');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (7, 'Khalid A.', 'Al-Falih', 'SA');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (8, 'Ian', 'Taylor', 'NL');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (9, 'Liu', 'Zhenya', 'CN');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (10, 'John', 'Watson', 'US');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (11, 'Ryan M.', 'Lance', 'US');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (12, 'Akio', 'Toyoda', 'JP');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (13, 'Christophe', 'de Margerie', 'FR');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (14, 'Martin', 'Winterkorn', 'DE');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (15, 'Jiro', 'Saito', 'JP');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (16, 'Lee', 'Kun-hee', 'KR');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (17, 'Ivan', 'Glasenberg', 'CH');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (18, 'Alexei', 'Miller', 'RU');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (19, 'Tim', 'Cook', 'US');
insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE) values (20, 'Daniel', 'Akerson', 'US');

insert into PERSON (ID, FIRST_NAME, LAST_NAME, COUNTRY_CODE, ACTIVE) values (21, 'Greg', 'Whally', 'US', 0);

# --- !Downs

drop table PERSON;

