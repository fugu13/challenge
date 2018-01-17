insert into supplier (id, name, address, contact) values ('9db6cec0-fae2-11e7-8c3f-9a214cf093ae', 'Supplier 1 for Transactions', 'Addy 1', 'Contact 1');
insert into supplier (id, name, address, contact) values ('9db6d320-fae2-11e7-8c3f-9a214cf093ae', 'Supplier 2 for Transactions', 'Addy 2', 'Contact 2');

insert into transaction (id, supplier_id, content) values ('7529f296-fae4-11e7-8c3f-9a214cf093ae', '9db6cec0-fae2-11e7-8c3f-9a214cf093ae', 'Content 1');
insert into transaction (id, supplier_id, content) values ('7529f78c-fae4-11e7-8c3f-9a214cf093ae', '9db6d320-fae2-11e7-8c3f-9a214cf093ae', 'Content 2');
insert into transaction (id, supplier_id, content) values ('7529fa48-fae4-11e7-8c3f-9a214cf093ae', '9db6cec0-fae2-11e7-8c3f-9a214cf093ae', 'Content 3');