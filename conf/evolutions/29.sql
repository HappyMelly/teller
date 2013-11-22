# T92 Exchange rates

# --- !Ups

create table `Exchange_Rate` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`base` VARCHAR(254) NOT NULL,
`counter` VARCHAR(254) NOT NULL,
`rate` DECIMAL(21,2) NOT NULL,
`timestamp` TIMESTAMP NOT NULL);

# --- !Downs

drop table `ExchangeRate`;
