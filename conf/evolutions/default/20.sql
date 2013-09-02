# --- !Ups

alter table `brand` change `CREATED` `CREATED` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table `activity` change `CREATED` `CREATED` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

# --- !Downs


