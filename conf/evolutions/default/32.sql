# T91 Entry details

# --- !Ups

-- Apply percentages to booking entries’ from and two amounts.

update BOOKING_ENTRY set FROM_AMOUNT = FROM_AMOUNT * SOURCE_PERCENTAGE / 100;
update BOOKING_ENTRY set TO_AMOUNT = TO_AMOUNT * SOURCE_PERCENTAGE / 100;


# --- !Downs

update BOOKING_ENTRY set FROM_AMOUNT = FROM_AMOUNT * 100 /  SOURCE_PERCENTAGE;
update BOOKING_ENTRY set TO_AMOUNT = TO_AMOUNT * 100 /  SOURCE_PERCENTAGE;
