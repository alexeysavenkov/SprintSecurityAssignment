insert into user (username, password, enabled) values ('user', '123', true);
insert into user (username, password, enabled) values ('admin', '123', true);


insert into user_role (username, authority) values ('admin', 'ROLE_ADMIN');