# --- !Ups
create table if not exists CERTIFICATE_TEMPLATE(
    ID bigint not null primary key,
    BRAND_CODE varchar(5) not null,
    LANGUAGE char(2) not null,
    TEMPLATE blob,
    TEMPLATE_NO_FACILITATOR blob
);

# --- !Downs
drop table if exists CERTIFICATE_TEMPLATE;