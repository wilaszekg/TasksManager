# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table project (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  owner_login               varchar(255),
  constraint pk_project primary key (id))
;

create table user (
  login                     varchar(255) not null,
  password                  varchar(255),
  constraint pk_user primary key (login))
;


create table user_project (
  user_login                     varchar(255) not null,
  project_id                     bigint not null,
  constraint pk_user_project primary key (user_login, project_id))
;
create sequence project_seq;

create sequence user_seq;

alter table project add constraint fk_project_owner_1 foreign key (owner_login) references user (login) on delete restrict on update restrict;
create index ix_project_owner_1 on project (owner_login);



alter table user_project add constraint fk_user_project_user_01 foreign key (user_login) references user (login) on delete restrict on update restrict;

alter table user_project add constraint fk_user_project_project_02 foreign key (project_id) references project (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists project;

drop table if exists user_project;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists project_seq;

drop sequence if exists user_seq;

