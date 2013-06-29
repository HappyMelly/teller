# --- !Ups

create table login_identity (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(254) NOT NULL,
  provider_id VARCHAR(254) NOT NULL,
  first_name VARCHAR(254) NOT NULL,
  last_name VARCHAR(254) NOT NULL,
  full_name VARCHAR(254) NOT NULL,
  email VARCHAR(254),
  avatar_url VARCHAR(254),
  auth_method VARCHAR(254) NOT NULL,
  token VARCHAR(254),
  secret VARCHAR(254),
  access_token VARCHAR(254),
  token_type VARCHAR(254),
  expires_in INTEGER,
  refresh_token VARCHAR(254));

# --- !Downs

drop table login_identity;
