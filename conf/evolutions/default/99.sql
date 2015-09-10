# --- !Ups
alter table ENDORSEMENT add column POSITION int default 0 after COMPANY;
update ENDORSEMENT set POSITION = ID;

# --- !Downs
alter table ENDORSEMENT drop column POSITION;
