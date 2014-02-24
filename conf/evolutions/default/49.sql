# --- !Ups

create table EVENT_TYPE (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  BRAND_ID BIGINT NOT NULL,
  NAME VARCHAR(254) NOT NULL,
  DEFAULT_TITLE VARCHAR(254)
);

alter table EVENT_TYPE add constraint EVENT_BRAND_FK foreign key(BRAND_ID) references BRAND(ID) on update NO ACTION on delete NO ACTION;

alter table EVENT add column EVENT_TYPE_ID BIGINT NOT NULL after ID;

alter table EVENT add constraint EVENT_TYPE_FK foreign key(EVENT_TYPE_ID) references EVENT_TYPE(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs
drop table EVENT_TYPE;
alter table EVENT drop column EVENT_TYPE_ID;

