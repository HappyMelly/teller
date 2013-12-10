# T110 Transaction types

# --- !Ups

create table TRANSACTION_TYPE (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(254) NOT NULL
);

create unique index IDX_NAME on TRANSACTION_TYPE(NAME);

insert into TRANSACTION_TYPE (NAME) values ('Management Fee');
insert into TRANSACTION_TYPE (NAME) values ('License Fee');
insert into TRANSACTION_TYPE (NAME) values ('Travel Expense');

# --- !Downs

drop table TRANSACTION_TYPE;
drop index IDX_NAME on TRANSACTION_TYPE;
