create table events (
  id                 serial primary key,
  held_on            date not null,
  name               varchar(512) not null,
  number_of_seats    integer not null,
  status             varchar(16) not null default 'open',
  CONSTRAINT events_uk UNIQUE (held_on, name)
);