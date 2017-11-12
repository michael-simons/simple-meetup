create table registrations (
  event_id           integer not null,
  email              varchar(1024) not null,
  name               varchar(512) not null,
  CONSTRAINT registrations_events_fk FOREIGN KEY (event_id) REFERENCES events(id),
  CONSTRAINT registrations_uk UNIQUE (event_id, email)
);