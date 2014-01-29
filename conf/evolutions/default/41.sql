# --- !Ups

alter table EVENT drop foreign key EVENT_BRAND_FK;
alter table EVENT change IS_PRIVATE NOT_PUBLIC BOOLEAN NOT NULL DEFAULT 0;
alter table EVENT change IS_ARCHIVED ARCHIVED BOOLEAN NOT NULL DEFAULT 0;
alter table EVENT modify BRAND_CODE VARCHAR(5) NOT NULL;
alter table EVENT modify COUNTRY_CODE CHAR(2) NOT NULL;
alter table EVENT add constraint EVENT_BRAND_FK foreign key(BRAND_CODE) references BRAND(CODE) on update NO ACTION on delete NO ACTION;

# --- !Downs
alter table EVENT drop foreign key EVENT_BRAND_FK;
alter table EVENT change NOT_PUBLIC IS_PRIVATE BOOLEAN NOT NULL DEFAULT 0;
alter table EVENT change ARCHIVED IS_ARCHIVED BOOLEAN NOT NULL DEFAULT 0;
alter table EVENT modify BRAND_CODE VARCHAR(254) NOT NULL;
alter table EVENT modify COUNTRY_CODE VARCHAR(254) NOT NULL;
alter table EVENT add constraint EVENT_BRAND_FK foreign key(BRAND_CODE) references BRAND(CODE) on update NO ACTION on delete NO ACTION;
