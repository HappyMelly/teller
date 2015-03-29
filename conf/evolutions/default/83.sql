# --- !Ups
create table if not exists API_TOKEN(
  ID bigint not null auto_increment primary key,
  TOKEN char(40) not null unique,
  APP_NAME varchar(254) not null unique,
  APP_DESCRIPTION text not null,
  APP_WEBSITE varchar(254),
  WRITE_CALLS tinyint(1) not null default 0
);
alter table EVALUATION add column CONFIRMATION_ID char(64) after HANDLED;
alter table EVENT add column RATING float(6,2) default 0.0 after CONFIRMED;
alter table BRAND add column EVALUATION_HOOK_URL varchar(254) after BLOG;
create table if not exists FACILITATOR(
  ID bigint not null auto_increment primary key,
  PERSON_ID bigint not null,
  BRAND_ID bigint not null,
  RATING float(6,2) default 0.0,
  unique key FACILITATOR_KEY(PERSON_ID, BRAND_ID)
);
insert into FACILITATOR (PERSON_ID, BRAND_ID) select LICENSE.LICENSEE_ID, LICENSE.BRAND_ID from LICENSE;

# --- update event rating
create table if not exists TMP_EVENT_RATING (
  EVENT_ID bigint,
  RATING float(6,2) default 0.0
);
insert into TMP_EVENT_RATING select EVENT_ID, avg(QUESTION_6) from EVALUATION where EVALUATION.STATUS = 2 group by event_id;
update EVENT e, TMP_EVENT_RATING t set e.RATING = t.RATING where e.ID = t.EVENT_ID;
drop table if exists TMP_EVENT_RATING;
# --- update facilitator rating (I simplify calculations here as we have only one brand with evaluations now
create table if not exists TMP_EVENT_DATA (
  EVENT_ID bigint,
  EVALUATION_ID bigint,
  FACILITATOR_ID bigint,
  IMPRESSION int
);
insert into TMP_EVENT_DATA (select e.EVENT_ID, e.ID, f.FACILITATOR_ID, e.QUESTION_6 from EVALUATION e left join EVENT_FACILITATOR f on e.EVENT_ID = f.EVENT_ID where e.STATUS = 2);
create table if not exists TMP_FAC_RATING (
  FACILITATOR_ID bigint,
  RATING float(6,2) default 0.0
);
insert into TMP_FAC_RATING select FACILITATOR_ID, avg(IMPRESSION) from TMP_EVENT_DATA group by FACILITATOR_ID;
update FACILITATOR f, TMP_FAC_RATING t set f.RATING = t.RATING where f.PERSON_ID = t.FACILITATOR_ID;
drop table if exists TMP_EVENT_DATA;
drop table if exists TMP_FAC_RATING;


# --- !Downs
drop table if exists API_TOKEN;
alter table EVALUATION drop column CONFIRMATION_ID;
alter table EVENT drop column RATING;
alter table BRAND drop column EVALUATION_HOOK_URL;
drop table if exists FACILITATOR;