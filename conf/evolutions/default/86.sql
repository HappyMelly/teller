# --- !Ups
alter table CERTIFICATE_TEMPLATE add column BRAND_ID bigint not null default 0 after BRAND_CODE;
update CERTIFICATE_TEMPLATE t, BRAND set t.BRAND_ID = BRAND.ID where t.BRAND_CODE = BRAND.CODE;
delete from CERTIFICATE_TEMPLATE where BRAND_ID = 0;
alter table CERTIFICATE_TEMPLATE drop column BRAND_CODE;

alter table BRAND_FEE modify column BRAND varchar(5) not null;
alter table BRAND_FEE add column BRAND_ID bigint not null default 0 after BRAND;
update BRAND_FEE f, BRAND set f.BRAND_ID = BRAND.ID where f.BRAND = BRAND.CODE COLLATE utf8_unicode_ci;
delete from BRAND_FEE where BRAND_ID = 0;
alter table BRAND_FEE drop INDEX BRAND_COUNTRY, add unique key BRAND_COUNTRY(BRAND_ID, COUNTRY);
alter table BRAND_FEE drop column BRAND;

alter table EVENT add column BRAND_ID bigint not null default 0 after BRAND_CODE;
update EVENT t, BRAND set t.BRAND_ID = BRAND.ID where t.BRAND_CODE = BRAND.CODE COLLATE utf8_unicode_ci;
delete from EVENT where BRAND_ID = 0;
alter table EVENT drop column BRAND_CODE;

create table if not exists BRAND_COORDINATOR (
  ID bigint not null auto_increment primary key,
  BRAND_ID bigint not null,
  PERSON_ID bigint not null,
  EVENT tinyint(1) not null default 0,
  EVALUATION tinyint(1) not null default 0,
  CERTIFICATE tinyint(1) not null default 0,
  unique key BRAND_MEMBER_KEY(BRAND_ID, PERSON_ID)
);
alter table BRAND_COORDINATOR add constraint BRAND_TEAM_BRAND_FK foreign key(BRAND_ID) references BRAND(ID) on update NO ACTION on delete CASCADE;
alter table BRAND_COORDINATOR add constraint BRAND_TEAM_PERSON_FK foreign key(PERSON_ID) references PERSON(ID) on update NO ACTION on delete CASCADE;

insert into BRAND_COORDINATOR (BRAND_ID, PERSON_ID, EVENT, EVALUATION, CERTIFICATE) select BRAND.ID, BRAND.COORDINATOR_ID, 1, 1, 1 from BRAND;

# --- !Downs
alter table CERTIFICATE_TEMPLATE add column BRAND_CODE char(5) not null default "" after BRAND_ID;
update CERTIFICATE_TEMPLATE t, BRAND set t.BRAND_CODE = BRAND.CODE where t.BRAND_ID = BRAND.ID;
alter table CERTIFICATE_TEMPLATE drop column BRAND_ID;

alter table BRAND_FEE add column BRAND char(5) not null default "" after BRAND_ID;
update BRAND_FEE t, BRAND set t.BRAND = BRAND.CODE where t.BRAND_ID = BRAND.ID;
alter table BRAND_FEE drop column BRAND_ID;
alter table BRAND_FEE drop index BRAND_COUNTRY, add unique key BRAND_COUNTRY(BRAND, COUNTRY);

alter table EVENT add column BRAND_CODE char(5) not null default "" after BRAND_ID;
update EVENT t, BRAND set t.BRAND_CODE = BRAND.CODE where t.BRAND_ID = BRAND.ID;
alter table EVENT drop column BRAND_ID;

alter table BRAND_COORDINATOR drop foreign key BRAND_TEAM_BRAND_FK;
alter table BRAND_COORDINATOR drop foreign key BRAND_TEAM_PERSON_FK;
drop table if exists BRAND_COORDINATOR;