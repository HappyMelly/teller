# --- !Ups

create table EVENT_FACILITATOR (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  EVENT_ID BIGINT NOT NULL,
  FACILITATOR_ID BIGINT NOT NULL);

alter table EVENT_FACILITATOR add constraint FACILITATOR_FK foreign key(FACILITATOR_ID) references PERSON(ID) on update NO ACTION on delete NO ACTION;
alter table EVENT_FACILITATOR add constraint EVENT_FK foreign key(EVENT_ID) references EVENT(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs

drop table EVENT_FACILITATOR;

