# --- !Ups
alter table EVENT_TYPE add column FREE tinyint(1) default 0;
alter table EVENT drop column CREATED;
alter table EVENT drop column CREATED_BY;
alter table EVENT drop column UPDATED;
alter table EVENT drop column UPDATED_BY;
alter table EVENT add column FREE tinyint(1) default 0 after CONFIRMED;
# --- !Downs
alter table EVENT_TYPE drop column FREE;
alter table EVENT add column CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table EVENT add column CREATED_BY VARCHAR(254) NOT NULL DEFAULT 'Sergey Kotlov';
alter table EVENT add column UPDATED TIMESTAMP;
alter table EVENT add column UPDATED_BY VARCHAR(254) DEFAULT 'Sergey Kotlov';
alter table EVENT drop column FREE;

