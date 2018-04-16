create table user (
  username varchar(256),
  password varchar(256),
  enabled boolean
);

create table user_role (
  username varchar(256),
  authority varchar(256)
);