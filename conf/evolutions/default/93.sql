# --- !Ups
create table if not exists EXPERIMENT(
  ID bigint not null auto_increment primary key,
  MEMBER_ID bigint not null,
  NAME varchar(254) not null,
  PICTURE tinyint(1) default 0,
  DESCRIPTION text not null,
  URL varchar(254) default ""
);
alter table EXPERIMENT add constraint EXPERIMENT_MEMBER_FK foreign key(MEMBER_ID) references MEMBER(ID) on update NO ACTION on delete CASCADE;

# --- !Downs
drop table if exists EXPERIMENT