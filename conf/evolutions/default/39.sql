# T104 Booking date created

# --- !Ups
alter table BOOKING_ENTRY modify column CREATED timestamp not null;

# --- !Downs


