CREATE DATABASE IF NOT EXISTS mobile_number_portability;
USE mobile_number_portability;

CREATE TABLE operators (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    organization VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO operators (name, organization)
VALUES
    ('Vodafone', 'vodafone'),
    ('Orange', 'orange'),
    ('Etisalat', 'etisalat');


CREATE TABLE persons (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    national_id VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE mobile_numbers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    phone_number VARCHAR(11) NOT NULL UNIQUE,

    person_id BIGINT UNSIGNED NOT NULL,

    current_operator_id BIGINT UNSIGNED NOT NULL,

    operator_since DATE NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_mobile_number_person
        FOREIGN KEY (person_id)
        REFERENCES persons(id),

    CONSTRAINT fk_mobile_number_operator
        FOREIGN KEY (current_operator_id)
        REFERENCES operators(id)
);

CREATE TABLE porting_requests (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    mobile_number_id BIGINT UNSIGNED NOT NULL,

    donor_operator_id BIGINT UNSIGNED NOT NULL,

    recipient_operator_id BIGINT UNSIGNED NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    resolved_at TIMESTAMP NULL,

    CONSTRAINT fk_porting_request_mobile_number
        FOREIGN KEY (mobile_number_id)
        REFERENCES mobile_numbers(id),

    CONSTRAINT fk_porting_request_donor
        FOREIGN KEY (donor_operator_id)
        REFERENCES operators(id),

    CONSTRAINT fk_porting_request_recipient
        FOREIGN KEY (recipient_operator_id)
        REFERENCES operators(id),

    CONSTRAINT chk_porting_request_status
        CHECK (status IN (
            'PENDING',
            'ACCEPTED',
            'REJECTED',
            'CANCELED'
        )),

    CONSTRAINT chk_donor_recipient_different
        CHECK (donor_operator_id <> recipient_operator_id)
);


-- Index used to efficiently check whether a mobile number
-- already has a pending porting request.
CREATE INDEX idx_porting_request_mobile_status
    ON porting_requests (mobile_number_id, status);


    INSERT INTO operators (name, organization)
VALUES
    ('Vodafone', 'vodafone'),
    ('Orange', 'orange'),
    ('Etisalat', 'etisalat');
    