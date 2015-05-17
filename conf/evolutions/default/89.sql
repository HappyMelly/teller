# --- !Ups
create table if not exists PROFILE_COMPLETION_PROGRESS(
  ID bigint not null auto_increment primary key,
  OBJECT_ID bigint not null,
  OBJECT_TYPE tinyint(1) not null default 0,
  STEPS text
);

# --- !Downs
drop table if not exists PROFILE_PROGRESSION;