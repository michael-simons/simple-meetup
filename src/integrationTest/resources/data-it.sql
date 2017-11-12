INSERT INTO events (held_on, name, number_of_seats, status) VALUES('2017-09-21', 'Michaels Geburtstag', 40, 'open');

INSERT INTO events (held_on, name, number_of_seats, status) VALUES(current_date + 1, 'Open Event', 20, 'open');
INSERT INTO registrations(event_id, email, name)
    SELECT id, 'michael.simons@innoq.com', 'Michael'
    FROM   events
    WHERE  name = 'Open Event';

INSERT INTO events (held_on, name, number_of_seats, status) VALUES(current_date + 1, 'Closed Event', 20, 'closed');