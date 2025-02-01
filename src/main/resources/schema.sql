CREATE TABLE IF NOT EXISTS countries
(
    iso2_code    CHAR(2) PRIMARY KEY,
    country_name TEXT NOT NULL,
    CONSTRAINT chk_iso2_code_format CHECK (iso2_code ~ '^[A-Z]{2}$'),
    CONSTRAINT chk_country_name_uppercase CHECK (country_name ~ '^[A-Z]+$')
);

CREATE TABLE IF NOT EXISTS locations
(
    location_id  SERIAL PRIMARY KEY,
    address_line TEXT    NOT NULL,
    iso2_code    CHAR(2) NOT NULL REFERENCES countries (iso2_code) ON DELETE CASCADE
);

-- CREATE INDEX idx_locations_countries ON locations (iso2_code);

CREATE TABLE IF NOT EXISTS headquarters
(
    swift_code  VARCHAR(11) PRIMARY KEY,
    name        TEXT NOT NULL,
    location_id INT  NOT NULL REFERENCES locations (location_id) ON DELETE CASCADE,
    CONSTRAINT chk_hq_swift_code_format CHECK (swift_code ~ '^[A-Z0-9]{11}$' AND swift_code LIKE '%XXX')
);

-- CREATE INDEX idx_headquarters_locations ON headquarters (location_id);

CREATE TABLE IF NOT EXISTS branches
(
    swift_code    VARCHAR(11) PRIMARY KEY,
    name          TEXT        NOT NULL,
    hq_swift_code VARCHAR(11) NOT NULL REFERENCES headquarters (swift_code) ON DELETE CASCADE,
    location_id   INT         NOT NULL REFERENCES locations (location_id) ON DELETE CASCADE,
    CONSTRAINT chk_branch_swift_code_format CHECK (swift_code ~ '^[A-Z0-9]{11}$' AND swift_code NOT LIKE '%XXX'),
    CONSTRAINT chk_branch_swift_code_prefix CHECK (LEFT(swift_code, 8) = LEFT(hq_swift_code, 8))
);

-- CREATE INDEX idx_branches_locations ON branches (location_id);
-- CREATE INDEX idx_branches_headquarters ON branches (hq_swift_code);
