# --- !Ups

alter table EVENT_PARTICIPANT add column EVALUATION_ID bigint(20) null default 0;
update EVENT_PARTICIPANT as EP set EVALUATION_ID = (select ID from EVALUATION as EV where EV.EVENT_ID = EP.EVENT_ID and EV.PARTICIPANT_ID = EP.PERSON_ID);

# --- !Downs
alter table EVENT_PARTICIPANT drop column EVALUATION_ID;
