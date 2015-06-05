# --- !Ups
alter table SOCIAL_PROFILE add column CONTACT_FORM varchar(254) after PHONE;
create table BRAND_LINK(
  ID bigint not null auto_increment primary key,
  BRAND_ID bigint not null,
  LINK_TYPE varchar(10) not null,
  LINK varchar(254) not null
);
alter table BRAND_LINK add constraint BRAND_LINK_FK foreign key(BRAND_ID) references BRAND(ID) on update NO ACTION on delete CASCADE;
create table BRAND_TESTIMONIAL(
  ID bigint not null auto_increment primary key,
  BRAND_ID bigint not null,
  CONTENT text not null,
  NAME varchar(254) not null,
  COMPANY varchar(254)
);
alter table BRAND_TESTIMONIAL add constraint BRAND_TESTIMONIAL_FK foreign key(BRAND_ID) references BRAND(ID) on update NO ACTION on delete CASCADE;

# --- !Downs
alter table SOCIAL_PROFILE drop column CONTACT_FORM;
drop table BRAND_LINK;
drop table BRAND_TESTIMONIAL;
