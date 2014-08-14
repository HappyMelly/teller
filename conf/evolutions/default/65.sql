# --- !Ups
update EVENT set SPOKEN_LANGUAGE = "EN" where SPOKEN_LANGUAGE = "English";
update EVENT set MATERIALS_LANGUAGE = "EN" where MATERIALS_LANGUAGE = "English";
update EVENT set SPOKEN_LANGUAGE = 'RU' where SPOKEN_LANGUAGE = 'Russian';
update EVENT set MATERIALS_LANGUAGE = 'RU' where MATERIALS_LANGUAGE = 'Russian';

# --- !Downs
update EVENT set SPOKEN_LANGUAGE = 'English' where SPOKEN_LANGUAGE = 'EN';
update EVENT set MATERIALS_LANGUAGE = 'English' where MATERIALS_LANGUAGE = 'EN';
update EVENT set SPOKEN_LANGUAGE = 'Russian' where SPOKEN_LANGUAGE = 'RU';
update EVENT set MATERIALS_LANGUAGE = 'Russian' where MATERIALS_LANGUAGE = 'RU';
