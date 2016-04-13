# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table publication (
  id                            bigint not null,
  authors                       varchar(255),
  title                         varchar(255),
  location                      varchar(255),
  start_page                    integer,
  end_page                      integer,
  month                         varchar(255),
  year                          integer,
  owner_id                      bigint,
  constraint pk_publication primary key (id)
);
create sequence publication_seq;

create table user (
  id                            bigint not null,
  first_name                    varchar(255),
  last_name                     varchar(255),
  email                         varchar(255),
  auth_token                    varchar(255),
  password                      varchar(255),
  constraint pk_user primary key (id)
);
create sequence user_seq;

alter table publication add constraint fk_publication_owner_id foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_publication_owner_id on publication (owner_id);


# --- !Downs

alter table publication drop constraint if exists fk_publication_owner_id;
drop index if exists ix_publication_owner_id;

drop table if exists publication;
drop sequence if exists publication_seq;

drop table if exists user;
drop sequence if exists user_seq;

