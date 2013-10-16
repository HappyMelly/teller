# T87 Booking entries overview

# --- !Ups

create table BOOKING_ENTRY (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  OWNER_ID BIGINT NOT NULL,
  BOOKING_DATE DATE NOT NULL,
  BOOKING_NUMBER INTEGER NOT NULL,
  SUMMARY VARCHAR(254) NOT NULL,
  SOURCE_CURRENCY CHAR(3) NOT NULL,
  SOURCE_AMOUNT DECIMAL(13,3) NOT NULL,
  SOURCE_PERCENTAGE INTEGER NOT NULL,
  FROM_ID BIGINT NOT NULL,
  FROM_CURRENCY CHAR(3) NOT NULL,
  FROM_AMOUNT DECIMAL(13,3) NOT NULL,
  TO_ID BIGINT NOT NULL,
  TO_CURRENCY CHAR(3) NOT NULL,
  TO_AMOUNT DECIMAL(13,3) NOT NULL,
  REFERENCE VARCHAR(254),
  REFERENCE_DATE DATE,
  BRAND_ID BIGINT NOT NULL,
  DESCRIPTION VARCHAR(254),
  URL VARCHAR(254)
);

alter table BOOKING_ENTRY add constraint BOOKING_OWNER_FK foreign key(OWNER_ID) references PERSON(ID) on update NO ACTION on delete NO ACTION;
alter table BOOKING_ENTRY add constraint BOOKING_BRAND_FK foreign key(BRAND_ID) references BRAND(ID) on update NO ACTION on delete NO ACTION;
alter table BOOKING_ENTRY add constraint BOOKING_FROM_FK foreign key(FROM_ID) references ACCOUNT(ID) on update NO ACTION on delete NO ACTION;
alter table BOOKING_ENTRY add constraint BOOKING_TO_FK foreign key(TO_ID) references ACCOUNT(ID) on update NO ACTION on delete NO ACTION;

insert into BOOKING_ENTRY (OWNER_ID, BOOKING_DATE, BOOKING_NUMBER, BRAND_ID, SUMMARY, DESCRIPTION,
  URL, REFERENCE, REFERENCE_DATE, SOURCE_CURRENCY, SOURCE_AMOUNT, SOURCE_PERCENTAGE,
  FROM_ID, FROM_CURRENCY, FROM_AMOUNT, TO_ID, TO_CURRENCY, TO_AMOUNT)
select COORDINATOR_ID, current_timestamp, 100000, ID, 'GoDaddy Inc.', 'domain name registration  management30.com.br',
  'http://www.godaddy.com/.....96496987.pdf', '96496987', '2013-08-27', 'USD', -100.00, 100, 1, 'EUR', 75.62, 1, 'BRL', 238.30
  from BRAND where NAME='Management 3.0';

update BOOKING_ENTRY set FROM_ID = (select a.id from account a left join organisation o on o.id=a.organisation_id where name='Happy Melly One BV') where id = LAST_INSERT_ID();
update BOOKING_ENTRY set TO_ID = (select a.id from account a left join organisation o on o.id=a.organisation_id where name='Happy Melly Brazil LTDA') where id = LAST_INSERT_ID();

insert into BOOKING_ENTRY (OWNER_ID, BOOKING_DATE, BOOKING_NUMBER, BRAND_ID, SUMMARY, DESCRIPTION,
  SOURCE_CURRENCY, SOURCE_AMOUNT, SOURCE_PERCENTAGE,
  FROM_ID, FROM_CURRENCY, FROM_AMOUNT, TO_ID, TO_CURRENCY, TO_AMOUNT)
select COORDINATOR_ID, current_timestamp, 100001, ID, 'DARE web site', 'Web site design, branding and implementation',
  'EUR', 2000.00, 50, 1, 'EUR', 2000.00, 1, 'EUR', 2000.00
  from BRAND where NAME='DARE';

update BOOKING_ENTRY set FROM_ID = (select a.id from account a left join organisation o on o.id=a.organisation_id where name='Happy Melly One BV') where id = LAST_INSERT_ID();
update BOOKING_ENTRY set TO_ID = (select a.id from account a left join organisation o on o.id=a.organisation_id where name='Bravebox') where id = LAST_INSERT_ID();


# --- !Downs

drop table BOOKING_ENTRY;