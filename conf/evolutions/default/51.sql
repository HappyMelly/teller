# --- !Ups

create table EVENT_INVOICE (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  EVENT_ID BIGINT NOT NULL,
  INVOICE_TO BIGINT NOT NULL,
  INVOICE_BY BIGINT,
  NUMBER VARCHAR(254)
);

# it's just too difficult to update all records. No reason to waste my time because they exist only on a test server
alter table EVENT_FACILITATOR drop foreign key EVENT_FK;
truncate table EVENT_FACILITATOR;
truncate table EVENT;

alter table EVENT_INVOICE add constraint EVENT_INVOICE_FK foreign key(EVENT_ID) references EVENT(ID) on update NO ACTION on delete NO ACTION;
alter table EVENT_FACILITATOR add constraint EVENT_FK foreign key(EVENT_ID) references EVENT(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table EVENT_INVOICE drop foreign key EVENT_INVOICE_FK;
drop table EVENT_INVOICE;