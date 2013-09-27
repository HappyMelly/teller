# --- !Ups

insert into `ADDRESS` (`STREET_1`, `CITY`, `POST_CODE`, `COUNTRY_CODE`)
    values ('Palais Royal', 'Rotterdam', '1000', 'NL')
    ;

insert into `PERSON` (`FIRST_NAME`, `LAST_NAME`, `EMAIL_ADDRESS`, `BIO`,
                      `TWITTER_HANDLE`, `ADDRESS_ID`, `CREATED_BY`,
                      `UPDATED`)
    values ('Melly', 'Shum', 'melly@happymelly.com',
            'Looking for a job which makes me happy', 'happy_melly',
             LAST_INSERT_ID(), 'Peter Hilton', current_timestamp)
    ;

# --- !Downs
