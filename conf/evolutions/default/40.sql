# Bug: wrong precision for persisted exchange rates.
# --- !Ups

-- Delete today’s rates, so they’ll be refetched, and invalid zero rates.
delete from EXCHANGE_RATE where date(timestamp) = current_date;
delete from EXCHANGE_RATE where rate=0;
alter table EXCHANGE_RATE modify column RATE decimal(17,6) not null;

# --- !Downs

alter table EXCHANGE_RATE modify column RATE decimal(21,2) not null;
