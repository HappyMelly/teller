# T91 Booking entry details (add created)

# --- !Ups

alter table BOOKING_ENTRY add column CREATED date NOT NULL,

# --- !Downs

alter table BOOKING_ENTRY drop column CREATED;
