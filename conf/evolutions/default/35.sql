# T111 Booking entry transaction type

# --- !Ups

alter table BOOKING_ENTRY add column TRANSACTION_TYPE_ID bigint;
alter table BOOKING_ENTRY add constraint TRANSACTION_TYPE_FK foreign key(TRANSACTION_TYPE_ID) references TRANSACTION_TYPE(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table BOOKING_ENTRY drop foreign key TRANSACTION_TYPE_FK;
alter table BOOKING_ENTRY drop column TRANSACTION_TYPE_ID;
