# --- !Ups
create table EVENT_PARTICIPANT (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  PERSON_ID BIGINT NOT NULL,
  EVENT_ID BIGINT NOT NULL);

alter table EVENT_PARTICIPANT add constraint PARTICIPANT_FK foreign key(EVENT_ID) references EVENT(ID) on update NO ACTION on delete NO ACTION;
alter table EVENT_PARTICIPANT add constraint PERSON_EVENT_FK foreign key(PERSON_ID) references PERSON(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs
alter table EVENT_PARTICIPANT drop foreign key PARTICIPANT_FK;
alter table EVENT_PARTICIPANT drop foreign key PERSON_EVENT_FK;
drop table EVENT_PARTICIPANT;
