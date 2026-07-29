INSERT INTO customers (id, name, email, created_at) VALUES
                                                        (222, 'Customer A', 'customer.a@email.com', CURRENT_TIMESTAMP),
                                                        (333, 'Customer B', 'customer.b@email.com', CURRENT_TIMESTAMP);

INSERT INTO accounts (id, account_number, customer_id, created_at) VALUES
                                                                       (1, '8872838283', 222, CURRENT_TIMESTAMP),
                                                                       (2, '8872838299', 222, CURRENT_TIMESTAMP),
                                                                       (3, '6872838260', 333, CURRENT_TIMESTAMP);

INSERT INTO app_user (id, username, password, role, created_at) VALUES
    (1, 'admin', '$2b$10$5blBX.VvCvqCmSSvf6ZhwO/DgRq4.lqbaPISDxXqqkkE88Qbq3zOS', 'ADMIN', CURRENT_TIMESTAMP);