CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    customer_full_name VARCHAR(255) NOT NULL,
    customer_address VARCHAR(500),
    customer_phone VARCHAR(50)
);

ALTER TABLE orders ADD COLUMN customer_id BIGINT;


DO $$
DECLARE
    order_row RECORD;
    new_customer_id BIGINT;
BEGIN
    FOR order_row IN
        SELECT id, customer_full_name, customer_address, customer_phone FROM orders
    LOOP
        INSERT INTO customers (customer_full_name, customer_address, customer_phone)
        VALUES (order_row.customer_full_name, order_row.customer_address, order_row.customer_phone)
        RETURNING id INTO new_customer_id;

        UPDATE orders SET customer_id = new_customer_id WHERE id = order_row.id;
    END LOOP;
END $$;

ALTER TABLE orders
    ALTER COLUMN customer_id SET NOT NULL;

ALTER TABLE orders
    ADD CONSTRAINT orders_customer_id_fkey
        FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE orders
    DROP COLUMN customer_address,
    DROP COLUMN customer_phone;

ALTER TABLE order_items ADD COLUMN product_id BIGINT;

UPDATE order_items oi
SET product_id = p.id
FROM products p
WHERE p.name = oi.product_name;

ALTER TABLE order_items
    ALTER COLUMN product_id SET NOT NULL;

ALTER TABLE order_items
    ADD CONSTRAINT order_items_product_id_fkey
        FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE order_items
    DROP COLUMN product_name,
    DROP COLUMN product_price;
