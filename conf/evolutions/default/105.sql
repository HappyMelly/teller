# --- !Ups
create table if not exists MGT30_OLD_EVALUATION(
  EVENT_ID bigint not null,
  NOT_PUBLIC tinyint(1) not null,
  IMPRESSION tinyint not null,
  FACILITATOR_ID bigint not null
);

alter table FACILITATOR add column YEARS_OF_EXPERIENCE tinyint default 0 after BRAND_ID;
alter table FACILITATOR add column NUMBER_OF_EVENTS smallint default 0 after YEARS_OF_EXPERIENCE;

# --- !Downs
drop table if exists MGT30_OLD_EVALUATION;
alter table FACILITATOR drop column YEARS_OF_EXPERIENCE;
alter table FACILITATOR drop column NUMBER_OF_EVENTS;