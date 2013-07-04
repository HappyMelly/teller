# --- !Ups

alter table LOGIN_IDENTITY add column API_TOKEN char(40);
update LOGIN_IDENTITY set API_TOKEN = 'a14ac0a6010a3f6f78b78155918b9758103f12a7' where twitter_handle='PeterHilton';
update LOGIN_IDENTITY set API_TOKEN = '18919c6f6e67d16034b8118e362ba6310db02409' where twitter_handle='jurgenappelo';
update LOGIN_IDENTITY set API_TOKEN = '163c19dfa1f2aa30ecb4c1f87f3728f5216d740f' where twitter_handle='skotlov';
update LOGIN_IDENTITY set API_TOKEN = 'c9f6b9138219d00ba9392202d0dc47004c22d32b' where twitter_handle='Targeter';

# --- !Downs

alter table LOGIN_IDENTITY drop column API_TOKEN;
