# --- !Ups

create table EVENT_INVOICE (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  EVENT_ID BIGINT NOT NULL,
  INVOICE_TO BIGINT NOT NULL,
  INVOICE_BY BIGINT,
  NUMBER VARCHAR(254)
);

alter table EVENT_INVOICE add constraint EVENT_INVOICE_FK foreign key(EVENT_ID) references EVENT(ID) on update NO ACTION on delete NO ACTION;
alter table EVENT_INVOICE add constraint INVOICE_TO_FK foreign key(INVOICE_TO) references ORGANISATIONS(ID) on update NO ACTION on delete NO ACTION;
# it's just too difficult to update all records. No reason to waste my time because they exist only on a test server
alter table EVENT_FACILITATOR drop constraint EVENT_FK
truncate table EVENT;
alter table EVENT_FACILITATOR add constraint EVENT_FK foreign key(EVENT_ID) references EVENT(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table EVENT_INVOICE drop foreign key EVENT_INVOICE_FK;
alter table EVENT_INVOICE drop foreign key INVOICE_TO_FK;
