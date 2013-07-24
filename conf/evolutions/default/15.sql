# T21 Brand delete

# --- !Ups

update BRAND set UPDATED = CURRENT_TIMESTAMP where UPDATED = '0000-00-00 00:00:00';

# --- !Downs
