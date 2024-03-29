--liquibase formatted sql
--changeset muthus:03-hardship-review-detail-reasons-table-data-insert
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (7,'Evidence Supplied','EXPENDITURE','N','Y');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (8,'Allowable Expense','EXPENDITURE','N','Y');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (9,'Essential - need for work','EXPENDITURE','N','Y');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (10,'Essential Item','EXPENDITURE','N','Y');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (11,'Arrangement in place','EXPENDITURE','N','Y');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (11010331,'No evidence supplied','EXPENDITURE',null,'N');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (11010332,'Insufficient evidence supplied','EXPENDITURE',null,'N');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (11010333,'Non-essential item/expense','EXPENDITURE',null,'N');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (11010334,'Covered by living expense','EXPENDITURE',null,'N');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (11010335,'Not allowable (diff from non-essential)','EXPENDITURE',null,'N');
Insert into crime_hardship.HARDSHIP_REVIEW_DETAIL_REASONS (ID,REASON,HRDT_TYPE,FORCE_NOTE,ACCEPTED) values (11010336,'Not in computation period','EXPENDITURE',null,'N');