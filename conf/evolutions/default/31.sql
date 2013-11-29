# T92 Exchange rates

# --- !Ups

create trigger EXCHANGE_RATE_UNIQUENESS_CHECK before insert on EXCHANGE_RATE
for each row
begin
  declare msg varchar(255);;
  if (select 1 from EXCHANGE_RATE where ((BASE=new.BASE and COUNTER=new.COUNTER) or (BASE=new.COUNTER and COUNTER=new.BASE)) and date(TIMESTAMP) = date(new.TIMESTAMP)) then
    set msg = 'Uniqueness check violation: exchange rate or inverse rate already exists for that date';;
    -- 45000 means ‘unhandled user-defined exception’ (13.6.7.4 SIGNAL Syntax)
    signal sqlstate '45000' set message_text = msg;;
  end if;;
end;

-- test

# --- !Downs

drop trigger EXCHANGE_RATE_UNIQUENESS_CHECK;
