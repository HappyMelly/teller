# T22 Person-organisation assignment

# --- !Ups

alter table ORGANISATION_MEMBERSHIPS drop foreign key ORGANISATION_FK;
alter table ORGANISATION_MEMBERSHIPS add constraint ORGANISATION_FK foreign key(ORGANISATION_ID) references ORGANISATION(ID) on update NO ACTION on delete NO ACTION;

# --- !Downs
