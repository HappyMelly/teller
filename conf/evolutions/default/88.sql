# --- !Ups
create table if not exists EVENT_CANCELLATION (
  ID bigint not null auto_increment primary key,
  BRAND_ID bigint not null,
  FACILITATOR_ID bigint not null,
  EVENT varchar(254) not null,
  EVENT_TYPE varchar(254) not null,
  CITY varchar(254) not null,
  COUNTRY_CODE char(2) not null,
  START_DATE date not null,
  END_DATE date not null,
  REASON text,
  PARTICIPANTS_NUMBER tinyint(3),
  PARTICIPANTS_INFO text
);
alter table EVENT_CANCELLATION add constraint EVENT_CANCELLATION_BRAND_FK foreign key(BRAND_ID) references BRAND(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs
drop table if exists EVENT_CANCELLATION;