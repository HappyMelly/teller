# --- !Ups
alter table ENDORSEMENT add column BRAND_ID bigint default 0 after PERSON_ID;
alter table EXPERIENCE add column BRAND_ID bigint default 0 after PERSON_ID;
rename table EXPERIENCE to MATERIAL;

# --- !Downs
alter table ENDORSEMENT drop column BRAND_ID;
rename table MATERIAL to EXPERIENCE;
alter table EXPERIENCE drop column BRAND_ID;
