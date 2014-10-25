# --- !Ups
alter table EVENT modify column UPDATED TIMESTAMP NOT NULL DEFAULT 0;
alter table EVALUATION modify column UPDATED TIMESTAMP NOT NULL DEFAULT 0;
alter table PRODUCT modify column UPDATED TIMESTAMP NOT NULL DEFAULT 0;

# --- !Downs
alter table EVENT modify column UPDATED TIMESTAMP NOT NULL;
alter table EVALUATION modify column UPDATED TIMESTAMP NOT NULL;
alter table PRODUCT modify column UPDATED TIMESTAMP NOT NULL;
