# --- !Ups

create table EVENT_FACILITATOR (
  EVENT_ID BIGINT NOT NULL,
  FACILITATOR_ID BIGINT NOT NULL,
  PRIMARY KEY (EVENT_ID, FACILITATOR_ID)
);

alter table EVENT_FACILITATOR add constraint FACILITATOR_FK foreign key(FACILITATOR_ID) references PERSON(ID) on update NO ACTION on delete NO ACTION;
alter table EVENT_FACILITATOR add constraint EVENT_FK foreign key(EVENT_ID) references EVENT(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs

drop table EVENT_FACILITATOR;