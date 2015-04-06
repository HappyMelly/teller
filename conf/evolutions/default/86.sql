# --- !Ups
alter table CERTIFICATE_TEMPLATE add column BRAND_ID bigint not null default 0 after BRAND_CODE;
update CERTIFICATE_TEMPLATE t, BRAND set t.BRAND_ID = BRAND.ID where t.BRAND_CODE = BRAND.CODE;
delete from CERTIFICATE_TEMPLATE where BRAND_ID = 0;
alter table CERTIFICATE_TEMPLATE drop column BRAND_CODE;

# --- !Downs
alter table CERTIFICATE_TEMPLATE add column BRAND_CODE char(5) not null default "" after BRAND_ID;
update CERTIFICATE_TEMPLATE t, BRAND set t.BRAND_CODE = BRAND.CODE where t.BRAND_ID = BRAND.ID;
alter table CERTIFICATE_TEMPLATE drop column BRAND_ID;