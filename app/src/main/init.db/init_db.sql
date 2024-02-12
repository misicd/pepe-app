CREATE SCHEMA IF NOT EXISTS pepe_schema;
USE pepe_schema; -- mysql
-- SET SCHEMA pepe_schema -- oracle
-- SET search_path TO pepe_schema; -- postgres

CREATE TABLE IF NOT EXISTS person (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    address VARCHAR(300) NOT NULL,
    CONSTRAINT person_pk_first_name_last_name UNIQUE (first_name, last_name)
);

CREATE TABLE IF NOT EXISTS pet   (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL
);

CREATE TABLE IF NOT EXISTS person_pet (
    pet_id BIGINT PRIMARY KEY NOT NULL,
    person_id BIGINT NOT NULL,
    CONSTRAINT person_pet_fk_person_id FOREIGN KEY (person_id) REFERENCES person(id),
    CONSTRAINT person_pet_fk_pet_id FOREIGN KEY (pet_id) REFERENCES pet(id)
);

COMMIT;