# --- !Ups
CREATE TABLE Places(
  id long NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  description varchar(255) NOT NULL,
  street varchar(255) NOT NULL,
  streetNumber int NOT NULL,
  city varchar(255) NOT NULL,
  country varchar(255) NOT NULL,
  lat double NOT NULL,
  lng double NOT NULL,
  created long NOT NULL,
  updated long NOT NULL
);


# --- !Downs
