# --- !Ups
alter table CERTIFICATE_TEMPLATE add column BRAND_ID bigint not null default 0 after BRAND_CODE;
update CERTIFICATE_TEMPLATE t, BRAND set t.BRAND_ID = BRAND.ID where t.BRAND_CODE = BRAND.CODE;
delete from CERTIFICATE_TEMPLATE where BRAND_ID = 0;
alter table CERTIFICATE_TEMPLATE drop column BRAND_CODE;

create table BRAND_TEAM_MEMBER (
  ID bigint not null auto_increment primary key,
  BRAND_ID bigint not null,
  PERSON_ID bigint not null,
  unique key BRAND_MEMBER_KEY(BRAND_ID, PERSON_ID)
);
alter table BRAND_TEAM_MEMBER add constraint BRAND_TEAM_BRAND_FK foreign key(BRAND_ID) references BRAND(ID) on update NO ACTION on delete NO ACTION;
alter table BRAND_TEAM_MEMBER add constraint BRAND_TEAM_PERSON_FK foreign key(PERSON_ID) references PERSON(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs
alter table CERTIFICATE_TEMPLATE add column BRAND_CODE char(5) not null default "" after BRAND_ID;
update CERTIFICATE_TEMPLATE t, BRAND set t.BRAND_CODE = BRAND.CODE where t.BRAND_ID = BRAND.ID;
alter table CERTIFICATE_TEMPLATE drop column BRAND_ID;

alter table BRAND_TEAM_MEMBER drop foreign key BRAND_TEAM_BRAND_FK;
alter table BRAND_TEAM_MEMBER drop foreign key BRAND_TEAM_PERSON_FK;
drop table BRAND_TEAM_MEMBER;