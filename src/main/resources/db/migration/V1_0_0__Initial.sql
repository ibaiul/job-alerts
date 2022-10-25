CREATE TABLE job_site
(
    id                      SERIAL PRIMARY KEY NOT NULL,
    name                    VARCHAR UNIQUE NOT NULL,
    url                     VARCHAR NOT NULL,
    parsing_strategy_type   VARCHAR NOT NULL,
    parsing_strategy_steps  VARCHAR NOT NULL
);

CREATE TABLE job
(
    id                      SERIAL PRIMARY KEY NOT NULL,
    title                   VARCHAR UNIQUE NOT NULL,
    url                     VARCHAR,
    created_on              TIMESTAMP NOT NULL,
    updated_on              TIMESTAMP,
    enabled                 BOOLEAN DEFAULT TRUE,
    site_id                 INTEGER NOT NULL,
    FOREIGN KEY (site_id) REFERENCES job_site(id)
);
