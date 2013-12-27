# T98 Booking entry attachments

# --- !Ups

alter table `BOOKING_ENTRY` add column `ATTACHMENT_KEY` VARCHAR(254) UNIQUE DEFAULT NULL;

# --- !Downs

alter table `BOOKING_ENTRY` drop column `ATTACHMENT_KEY`;
