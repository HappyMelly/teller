# --- !Ups
create table if not exists API_TOKEN(
  ID bigint not null auto_increment primary key,
  TOKEN varchar(254) not null unique,
  APP_NAME varchar(254) not null unique,
  APP_DESCRIPTION text not null,
  APP_WEBSITE varchar(254),
  WRITE_CALLS tinyint(1) not null default 0
);
# --- !Downs
drop table if exists API_TOKEN;