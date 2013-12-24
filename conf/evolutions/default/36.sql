# T102 Delete booking entry

# --- !Ups

alter table BOOKING_ENTRY add column DELETED BOOLEAN NOT NULL DEFAULT 0;

# --- !Downs

alter table BOOKING_ENTRY drop column DELETED;
