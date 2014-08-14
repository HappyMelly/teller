# --- !Ups
create table if not exists CERTIFICATE_TEMPLATE(
    ID bigint not null auto_increment primary key,
    BRAND_CODE varchar(5) not null,
    LANGUAGE char(2) not null,
    ONE_FACILITATOR longblob,
    TWO_FACILITATORS longblob
);

# --- !Downs
drop table if exists CERTIFICATE_TEMPLATE;