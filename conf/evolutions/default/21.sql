# --- !Ups

insert into `ADDRESS` (`STREET_1`, `CITY`, `POST_CODE`, `COUNTRY_CODE`)
    values ('Palais Royal', 'Rotterdam', '1000', 'NL');

insert into `PERSON` (`FIRST_NAME`, `LAST_NAME`, `EMAIL_ADDRESS`, `BIO`,
                      `INTERESTS`, `TWITTER_HANDLE`, `FACEBOOK_URL`,
                      `LINKEDIN_URL`, `GOOGLE_PLUS_URL`, `BOARD_MEMBER`,
                      `STAKEHOLDER`, `CREATED`, `CREATED_BY`, `ADDRESS_ID`)
values ('Melly', 'Shum', 'melly@happymelly.com',
        'Looking for a job which makes me happy', '', 'happy_melly',
         '', '', '', 1, 1, current_timestamp, 'Peter Hilton', LAST_INSERT_ID());

# --- !Downs
