# --- !Ups

alter table BRAND add column CREATED TIMESTAMP NOT NULL;
alter table BRAND add column CREATED_BY VARCHAR(254) NOT NULL;
alter table BRAND add column UPDATED TIMESTAMP NOT NULL;
alter table BRAND add column UPDATED_BY VARCHAR(254) NOT NULL;
alter table BRAND modify CODE VARCHAR(5) ;
update BRAND set CODE = upper(CODE), CREATED = CURRENT_TIMESTAMP, UPDATED = CURRENT_TIMESTAMP;

insert into BRAND (CODE, NAME, COORDINATOR_ID, CREATED_BY, UPDATED_BY)
  select 'DARE', 'DARE', id, 'Jurgen Appelo', 'Jurgen Appelo'
  from PERSON where EMAIL_ADDRESS = 'jurgen@noop.nl' LIMIT 1;

insert into BRAND (CODE, NAME, COORDINATOR_ID, CREATED_BY, UPDATED_BY)
  select 'HMEX', 'Happy Melly Express', id, 'Jurgen Appelo', 'Jurgen Appelo'
  from PERSON where EMAIL_ADDRESS = 'jurgen@noop.nl' LIMIT 1;

insert into BRAND (CODE, NAME, COORDINATOR_ID, CREATED_BY, UPDATED_BY)
  select 'COSUP', 'Collaboration Superpowers', id, 'Jurgen Appelo', 'Jurgen Appelo'
  from PERSON where EMAIL_ADDRESS = 'jurgen@noop.nl' LIMIT 1;

insert into BRAND (CODE, NAME, COORDINATOR_ID, CREATED_BY, UPDATED_BY)
  select 'BXLXS', 'Business XL/XS', id, 'Jurgen Appelo', 'Jurgen Appelo'
  from PERSON where EMAIL_ADDRESS = 'jurgen@noop.nl' LIMIT 1;

create unique index IDX_CODE on BRAND(CODE);

# --- !Downs

alter table BRAND drop column CREATED;
alter table BRAND drop column CREATED_BY;
alter table BRAND drop column UPDATED;
alter table BRAND drop column UPDATED_BY;
alter table BRAND modify CODE VARCHAR(254) ;
drop index IDX_CODE on BRAND;
