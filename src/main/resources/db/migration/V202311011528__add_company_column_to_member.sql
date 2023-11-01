ALTER TABLE member
    ADD COLUMN company_role VARCHAR(100) NULL AFTER history,
    ADD COLUMN company_name VARCHAR(100) NULL AFTER company_role;