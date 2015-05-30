# --- !Ups
create table if not exists PROFILE_COMPLETION(
  ID bigint not null auto_increment primary key,
  OBJECT_ID bigint not null,
  ORG tinyint(1) not null default 0,
  STEPS text
);

# --- !Downs
drop table if exists PROFILE_COMPLETION;