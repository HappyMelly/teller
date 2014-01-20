# T104 Booking entry activity

# --- !Ups

create table BOOKING_ENTRY_ACTIVITY (
  BOOKING_ENTRY_ID BIGINT NOT NULL,
  ACTIVITY_ID BIGINT NOT NULL,
  PRIMARY KEY (BOOKING_ENTRY_ID, ACTIVITY_ID)
);

alter table BOOKING_ENTRY_ACTIVITY add constraint ACTIVITY_FK foreign key(ACTIVITY_ID) references ACTIVITY(ID) on update NO ACTION on delete NO ACTION;

alter table BOOKING_ENTRY_ACTIVITY add constraint BOOKING_ENTRY_FK foreign key(BOOKING_ENTRY_ID) references BOOKING_ENTRY(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table BOOKING_ENTRY_ACTIVITY drop foreign key BOOKING_ENTRY_FK;
alter table BOOKING_ENTRY_ACTIVITY drop foreign key ACTIVITY_FK;
drop table BOOKING_ENTRY_ACTIVITY;

