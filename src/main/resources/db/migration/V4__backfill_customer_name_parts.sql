UPDATE customers
SET first_name = CASE
        WHEN strpos(trim(customer_full_name), ' ') > 0
            THEN split_part(trim(customer_full_name), ' ', 1)
        ELSE trim(customer_full_name)
    END,
    last_name = CASE
        WHEN strpos(trim(customer_full_name), ' ') > 0
            THEN substr(
                trim(customer_full_name),
                strpos(trim(customer_full_name), ' ') + 1
            )
        ELSE ''
    END
WHERE first_name IS NULL OR last_name IS NULL;

ALTER TABLE customers
    ALTER COLUMN first_name SET NOT NULL,
    ALTER COLUMN last_name SET NOT NULL;
