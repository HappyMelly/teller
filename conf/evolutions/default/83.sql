# --- !Ups
create table if not exists API_TOKEN(
  ID bigint not null auto_increment primary key,
  TOKEN varchar(254) not null unique,
  APP_NAME varchar(254) not null unique,
  APP_DESCRIPTION text not null,
  APP_WEBSITE varchar(254),
  WRITE_CALLS tinyint(1) not null default 0
);
alter table EVALUATION add column CONFIRMATION_ID char(64) after HANDLED;
alter table EVENT add column RATING float(6,2) default 0.0 after CONFIRMED;
alter table BRAND add column EVALUATION_HOOK_URL varchar(254) after BLOG;

# --- !Downs
drop table if exists API_TOKEN;
alter table EVALUATION drop column CONFIRMATION_ID;
alter table EVENT drop column RATING;
alter table BRAND drop columnd EVALUATION_HOOK_URL;