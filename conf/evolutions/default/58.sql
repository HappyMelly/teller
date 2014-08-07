# --- !Ups
alter table BRAND add column GENERATE_CERT tinyint(1) not null default 0 after PICTURE;
update BRAND set GENERATE_CERT = 1 where CODE = "MGT30";

# --- !Downs
alter table BRAND drop column GENERATE_CERT;
