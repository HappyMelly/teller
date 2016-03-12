# --- !Ups
CREATE TABLE CREDIT_CARD(
  ID BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  CUSTOMER_ID BIGINT(20) NOT NULL,
  REMOTE_ID VARCHAR(32) NOT NULL,
  BRAND VARCHAR(10) NOT NULL,
  NUMBER CHAR(4) NOT NULL,
  EXP_MONTH TINYINT NOT NULL,
  EXP_YEAR SMALLINT NOT NULL,
  ACTIVE TINYINT(1) NOT NULL DEFAULT 1,
  CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE CREDIT_CARD ADD CONSTRAINT CREDIT_CARD_FK FOREIGN KEY (CUSTOMER_ID) references CUSTOMER(ID) on update NO ACTION on delete CASCADE;

ALTER TABLE EVENT_ATTENDEE ADD COLUMN OPT_OUT TINYINT(1) NOT NULL DEFAULT 0 AFTER ROLE;

# --- !Downs
DROP TABLE CREDIT_CARD;

ALTER TABLE EVENT_ATTENDEE DROP COLUMN OPT_OUT;