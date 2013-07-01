# --- !Ups

create table ORGANISATION (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(254) NOT NULL,
  STREET_1 VARCHAR(254),
  STREET_2 VARCHAR(254),
  CITY VARCHAR(254),
  PROVINCE VARCHAR(254),
  POST_CODE VARCHAR(254),
  COUNTRY_CODE VARCHAR(254) NOT NULL,
  VAT_NUMBER VARCHAR(254),
  REGISTRATION_NUMBER VARCHAR(254),
  LEGAL_ENTITY BOOLEAN NOT NULL DEFAULT 0);

insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (1, 'Exxon Mobil Corporation', 'Irving', 'Texas', 'US');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (2, 'Royal Dutch Shell', 'The Hague', null, 'NL');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (3, 'Walmart', 'Bentonville', 'Arkansas', 'US');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (4, 'Sinopec Group', 'Beijing', null, 'CN');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (5, 'BP', 'London', null, 'GB');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (6, 'China National Petroleum Corporation', 'Beijing', null, 'CN');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (7, 'Saudi Aramco', 'Dhahran', null, 'SA');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (8, 'Vitol', 'Rotterdam', null, 'NL');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (9, 'State Grid Corporation of China', 'Beijing', null, 'CN');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (10, 'Chevron', 'San Ramon', 'California', 'US');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (11, 'ConocoPhillips', 'Houston', 'Texas', 'US');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (12, 'Toyota', 'Toyota', 'Aichi', 'JP');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (13, 'Total', 'Courbevoie', null, 'FR');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (14, 'Volkswagen Group', 'Wolfsburg', null, 'DE');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (15, 'Japan Post Holdings', 'Tokyo', null, 'JP');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (16, 'Samsung Electronics', 'Seoul', null, 'KR');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (17, 'Glencore', 'Baar', null, 'CH');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (18, 'Gazprom', 'Moscow', null, 'RU');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (19, 'Apple', 'Cupertino', 'California', 'US');
insert into ORGANISATION (ID, NAME, CITY, PROVINCE, COUNTRY_CODE) values (20, 'General Motors', 'Detroit', 'Michigan', 'US');

# --- !Downs

drop table ORGANISATION;

