# --- !Ups

alter table LICENSE add column VERSION VARCHAR(254) NOT NULL;
alter table LICENSE add column SIGNED DATE NOT NULL;
alter table LICENSE add column CONFIRMED BOOLEAN NOT NULL;
alter table LICENSE add column FEE_CURRENCY VARCHAR(254) NOT NULL;
alter table LICENSE add column FEE_AMOUNT DECIMAL(13,3) NOT NULL;
alter table LICENSE add column FEE_PAID_CURRENCY VARCHAR(254);
alter table LICENSE add column FEE_PAID_AMOUNT DECIMAL(13,3);

# --- !Downs

alter table LICENSE drop column VERSION;
alter table LICENSE drop column SIGNED;
alter table LICENSE drop column CONFIRMED;
alter table LICENSE drop column FEE_CURRENCY;
alter table LICENSE drop column FEE_AMOUNT;
alter table LICENSE drop column FEE_PAID_CURRENCY;
alter table LICENSE drop column FEE_PAID_AMOUNT;
