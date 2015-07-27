# --- !Ups
create table EXPERIENCE(
  ID bigint not null auto_increment primary key,
  PERSON_ID bigint not null,
  LINK_TYPE varchar(10) not null,
  LINK varchar(254) not null
);
alter table EXPERIENCE add constraint PERSON_EXPERIENCE foreign key(PERSON_ID) references PERSON(ID) on update NO ACTION on delete CASCADE;

create table ENDORSEMENT(
  ID bigint not null auto_increment primary key,
  PERSON_ID bigint not null,
  CONTENT text not null,
  NAME varchar(254) not null,
  COMPANY varchar(254)
);
alter table ENDORSEMENT add constraint ENDORSEMENT_FK foreign key(PERSON_ID) references PERSON(ID) on update NO ACTION on delete CASCADE;

# --- !Downs
drop table EXPERIENCE;
drop table ENDORSEMENT;
