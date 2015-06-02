# --- !Ups
create table if not exists PROFILE_STRENGTH(
  ID bigint not null auto_increment primary key,
  OBJECT_ID bigint not null,
  ORG tinyint(1) not null default 0,
  STEPS text
);
alter table MEMBER add column REASON text after END;

# --- !Downs
drop table if exists PROFILE_STRENGTH;
alter table MEMBER drop column REASON;