# --- !Ups

create table BRAND (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(254) NOT NULL,
  COORDINATOR_ID BIGINT NOT NULL);

alter table BRAND add constraint COORDINATOR_FK foreign key(COORDINATOR_ID) references PERSON(ID) on update NO ACTION on delete NO ACTION;

insert into BRAND values (1, 'Command & Control 1.0', 1);
insert into BRAND values (2, 'Vintage Scientific Management', 2);

# --- !Downs

drop table BRAND

