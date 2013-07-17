# --- !Ups
update PERSON set EMAIL_ADDRESS = concat(lower(FIRST_NAME), '.', lower(LAST_NAME), '@happymelly.com') where EMAIL_ADDRESS is null;

insert into PERSON (FIRST_NAME, LAST_NAME, EMAIL_ADDRESS, STREET_1, CITY, POST_CODE, COUNTRY_CODE, BIO, INTERESTS, TWITTER_HANDLE, FACEBOOK_URL, LINKEDIN_URL, GOOGLE_PLUS_URL, BOARD_MEMBER, STAKEHOLDER, CREATED, CREATED_BY)
values ('Jurgen', 'Appelo', 'jurgen@noop.nl', 'Palais Royal', 'Brussels', '1000', 'BE', 'Jurgen Appelo is a writer, speaker, trainer, entrepreneur, illustrator, developer, manager, blogger, reader, dreamer, leader, freethinker, and… _Dutch guy_.

Since 2008 Jurgen writes a popular blog at [www.noop.nl](http://www.noop.nl/), that covers topics including Agile management, software engineering, business improvement, personal development, and complexity theory. He is the author of the book Management 3.0: Leading Agile Developers, Developing Agile Leaders, which describes the role of the manager in Agile organizations. And he wrote the little book How to Change the World, which describes his new supermodel for change management. He is also a speaker who is regularly invited to talk at business seminars and conferences around the world.', 'After studying Software Engineering at the Delft University of Technology, and earning his Master’s degree in 1994, Jurgen Appelo has busied himself starting up and leading a variety of Dutch businesses, always in the position of team leader, manager, or executive. Jurgen has experience in leading a horde of 100 software developers, development managers, project managers, business consultants, quality managers, service managers, and kangaroos, some of which he hired accidentally.', 'jurgenappelo', 'https://www.facebook.com/jurgenappelo', 'https://www.linkedin.com/in/jurgenappelo', 'https://plus.google.com/113426236640644815069', 1, 0, current_timestamp, 'Peter Hilton');

# --- !Downs

delete from person where EMAIL_ADDRESS = 'jurgen@noop.nl';
