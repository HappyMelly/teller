# --- !Ups
CREATE TABLE API_CONFIG(
  ID BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  BRAND_ID BIGINT NOT NULL,
  TOKEN CHAR(64) NOT NULL,
  WRITE_CALLS TINYINT(1) NOT NULL DEFAULT 0,
  ACTIVE TINYINT(1) NOT NULL DEFAULT 0,
  EVENT VARCHAR(254),
  FACILITATOR VARCHAR(254),
  GENERAL_EVALUATION_FORM VARCHAR(254),
  SPECIFIC_EVENT_EVALUATION_FORM VARCHAR(254)
);
ALTER TABLE API_CONFIG ADD CONSTRAINT API_CONFIG_FK FOREIGN KEY (BRAND_ID) references BRAND(ID) on update NO ACTION on delete CASCADE;

# custom calls for existing brands and their tokens
# m30
INSERT INTO API_CONFIG (BRAND_ID, TOKEN, WRITE_CALLS, ACTIVE, GENERAL_EVALUATION_FORM)
    SELECT b.ID, t.TOKEN, t.WRITE_CALLS, 1, b.EVALUATION_URL FROM API_TOKEN t, BRAND b WHERE b.ID = 1 AND t.ID = 1;
# hm
INSERT INTO API_CONFIG (BRAND_ID, TOKEN, WRITE_CALLS, ACTIVE, GENERAL_EVALUATION_FORM)
  SELECT b.ID, t.TOKEN, t.WRITE_CALLS, 1, b.EVALUATION_URL FROM API_TOKEN t, BRAND b WHERE b.ID = 8 AND t.ID = 2;
# lcm
INSERT INTO API_CONFIG (BRAND_ID, TOKEN, WRITE_CALLS, ACTIVE, GENERAL_EVALUATION_FORM)
  SELECT b.ID, t.TOKEN, t.WRITE_CALLS, 1, b.EVALUATION_URL FROM API_TOKEN t, BRAND b WHERE b.ID = 14 AND t.ID = 3;
# cs
INSERT INTO API_CONFIG (BRAND_ID, TOKEN, WRITE_CALLS, ACTIVE, GENERAL_EVALUATION_FORM)
  SELECT b.ID, t.TOKEN, t.WRITE_CALLS, 1, b.EVALUATION_URL FROM API_TOKEN t, BRAND b WHERE b.ID = 5 AND t.ID = 4;

#ALTER TABLE BRAND DROP COLUMN EVALUATION_HOOK_URL;
#ALTER TABLE BRAND DROP COLUMN EVALUATION_URL;
#DROP TABLE API_TOKEN

# --- !Downs
DROP TABLE API_CONFIG;