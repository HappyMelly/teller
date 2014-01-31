# T107 Balance accounts

# --- !Ups

alter table BOOKING_ENTRY modify column BRAND_ID bigint;

# --- !Downs

alter table BOOKING_ENTRY modify column BRAND_ID bigint not null;
