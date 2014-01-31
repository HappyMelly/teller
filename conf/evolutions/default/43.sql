# T107 Balance accounts

# --- !Ups

alter table BOOKING_ENTRY modify column BRAND_ID bigint;

# --- !Downs

delete from ACTIVITY where PREDICATE = 'balance';
delete from BOOKING_ENTRY where BRAND_ID is null;
alter table BOOKING_ENTRY modify column BRAND_ID bigint not null;
