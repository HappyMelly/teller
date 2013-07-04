# --- !Ups

delete from BRAND;
alter table BRAND add column CODE VARCHAR(16) NOT NULL DEFAULT '';
insert into BRAND (ID, CODE, NAME, COORDINATOR_ID) values (1, 'mgt30', 'Management 3.0', 1);

create table LICENSE (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  LICENSEE_ID BIGINT NOT NULL,
  BRAND_ID BIGINT NOT NULL,
  START DATE NOT NULL,
  END DATE NOT NULL);

alter table LICENSE add constraint LICENSEE_FK foreign key(LICENSEE_ID) references PERSON(ID) on update NO ACTION on delete NO ACTION;
alter table LICENSE add constraint BRAND_FK foreign key(BRAND_ID) references BRAND(ID) on update NO ACTION on delete NO ACTION;

insert into LICENSE values (1, 1, 1, '2013-01-01', '2013-12-31');
insert into LICENSE values (2, 2, 1, '2013-01-01', '2013-12-31');
insert into LICENSE values (3, 3, 1, '2013-01-01', '2013-12-31');
insert into LICENSE values (4, 4, 1, '2013-01-01', '2013-12-31');

# --- !Downs

delete from BRAND;
insert into BRAND values (1, 'Command & Control 1.0', 1);
insert into BRAND values (2, 'Vintage Scientific Management', 2);
