-- Schema and seed data for the Mobile Number Portability service.
-- Loaded automatically by the MySQL container on first startup

CREATE TABLE IF NOT EXISTS operators (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    organization VARCHAR(50)  NOT NULL,
    range_start  BIGINT       NOT NULL,
    range_end    BIGINT       NOT NULL,
    CONSTRAINT uq_operators_organization UNIQUE (organization),
    CONSTRAINT chk_operators_range CHECK (range_start <= range_end)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS persons (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    national_id VARCHAR(20)  NOT NULL,
    name        VARCHAR(150) NOT NULL,
    CONSTRAINT uq_persons_national_id UNIQUE (national_id)
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS mobile_numbers (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number        VARCHAR(11) NOT NULL,
    person_id           BIGINT,
    current_operator_id BIGINT      NOT NULL,
    operator_since      DATE,
    CONSTRAINT uq_mobile_numbers_phone_number UNIQUE (phone_number),
    CONSTRAINT fk_mobile_numbers_person FOREIGN KEY (person_id) REFERENCES persons (id),
    CONSTRAINT fk_mobile_numbers_operator FOREIGN KEY (current_operator_id) REFERENCES operators (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS porting_requests (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    mobile_number_id      BIGINT      NOT NULL,
    donor_operator_id     BIGINT      NOT NULL,
    recipient_operator_id BIGINT      NOT NULL,
    status                VARCHAR(20) NOT NULL,
    created_at            DATETIME    NOT NULL,
    updated_at            DATETIME    NOT NULL,
    resolved_at           DATETIME,
    CONSTRAINT fk_porting_requests_mobile_number FOREIGN KEY (mobile_number_id) REFERENCES mobile_numbers (id),
    CONSTRAINT fk_porting_requests_donor FOREIGN KEY (donor_operator_id) REFERENCES operators (id),
    CONSTRAINT fk_porting_requests_recipient FOREIGN KEY (recipient_operator_id) REFERENCES operators (id)
) ENGINE = InnoDB;

-- Enforces "reject requests for a number that already has a pending request" at the DB
-- level, not just in the service layer. The CASE expression evaluates to NULL for every
-- non-pending row, and MySQL doesn't enforce uniqueness across NULLs, so only PENDING
-- rows are ever compared against each other.
CREATE UNIQUE INDEX uq_porting_requests_pending_number
    ON porting_requests ((CASE WHEN status = 'PENDING' THEN mobile_number_id END));

-- Speeds up the background job that scans for expired pending requests.
CREATE INDEX idx_porting_requests_status_created_at ON porting_requests (status, created_at);


-- number ranges (leading zero dropped: 01000000000 -> 1000000000, same numeric value).
INSERT INTO operators (name, organization, range_start, range_end) VALUES
    ('Vodafone', 'vodafone', 1000000000, 1099999999),
    ('Etisalat', 'etisalat', 1100000000, 1199999999),
    ('Orange',   'orange',   1200000000, 1299999999);
