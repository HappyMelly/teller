# --- !Ups

create table EVENT_TYPE (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  BRAND_ID BIGINT NOT NULL,
  NAME VARCHAR(254) NOT NULL,
  DEFAULT_TITLE VARCHAR(254)
);

alter table EVENT_TYPE add constraint EVENT_BRAND_FK foreign key(BRAND_ID) references BRAND(ID) on update NO ACTION on delete NO ACTION;

alter table EVENT add column EVENT_TYPE_ID BIGINT NOT NULL after ID;

insert into EVENT_TYPE (BRAND_ID, NAME, DEFAULT_TITLE) values
  (1, 'Regular 2-Day course', 'Regular 2-Day Management 3.0 Course'),
  (1, '1-Day Workshop', 'Exclusive 1-Day Book Tour Workshop');
update EVENT set EVENT_TYPE_ID = 1;
alter table EVENT add constraint EVENT_TYPE_FK foreign key(EVENT_TYPE_ID) references EVENT_TYPE(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs
alter table EVENT drop foreign key EVENT_TYPE_FK;
alter table EVENT_TYPE drop foreign key EVENT_BRAND_FK;
drop table EVENT_TYPE;
alter table EVENT drop column EVENT_TYPE_ID;
