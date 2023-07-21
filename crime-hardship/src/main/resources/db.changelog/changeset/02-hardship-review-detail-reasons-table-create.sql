--liquibase formatted sql
--changeset muthus:02-hardship-review-detail-reasons-table-create
CREATE TABLE IF NOT EXISTS crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS
(
    ID INTEGER,
    REASON VARCHAR(200) NOT NULL,
    HRDT_TYPE VARCHAR(15) NOT NULL,
    FORCE_NOTE VARCHAR(1),
    ACCEPTED VARCHAR(1),
    UNIQUE (HRDT_TYPE, REASON),
    CONSTRAINT hrdr_pk PRIMARY KEY (ID)
);