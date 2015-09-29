# --- !Ups
create table if not exists EVENT_REQUEST(
  ID BIGINT not null auto_increment primary key,
  BRAND_ID bigint not null,
  COUNTRY_CODE char(2) not null,
  CITY varchar(254),
  LANGUAGE varchar(254) not null,
  START_DATE DATE,
  END_DATE DATE,
  NUMBER_OF_PARTICIPANTS int default 1,
  COMMENT text,
  NAME varchar(254) not null,
  EMAIL varchar(254) not null,
  CREATED timestamp not null default current_timestamp,
  CREATED_BY varchar(254) not null,
  UPDATED timestamp not null,
  UPDATED_BY varchar(254) not null
);

# --- !Downs
drop table if exists EVENT_REQUEST;