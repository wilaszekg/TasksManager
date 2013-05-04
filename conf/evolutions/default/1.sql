# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table contributor (
  id                        bigint auto_increment not null,
  user_login                varchar(255),
  project_id                bigint,
  role                      integer,
  constraint ck_contributor_role check (role in (0,1)),
  constraint pk_contributor primary key (id))
;

create table mile_stone (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  creation_date             timestamp,
  due_date                  timestamp,
  project_id                bigint,
  constraint pk_mile_stone primary key (id))
;

create table project (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  owner_login               varchar(255),
  constraint pk_project primary key (id))
;

create table task (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  priority                  integer,
  task_kind                 integer,
  creation_date             timestamp,
  due_date                  timestamp,
  creator_login             varchar(255),
  assignee_login            varchar(255),
  mile_stone_id             bigint,
  project_id                bigint,
  task_status               integer not null,
  constraint ck_task_priority check (priority in (0,1,2,3,4)),
  constraint ck_task_task_kind check (task_kind in (0,1,2,3,4,5)),
  constraint ck_task_task_status check (task_status in (0,1)),
  constraint pk_task primary key (id))
;

create table user (
  login                     varchar(255) not null,
  password                  varchar(255),
  constraint pk_user primary key (login))
;

create sequence project_seq;

create sequence user_seq;

alter table contributor add constraint fk_contributor_user_1 foreign key (user_login) references user (login) on delete restrict on update restrict;
create index ix_contributor_user_1 on contributor (user_login);
alter table contributor add constraint fk_contributor_project_2 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_contributor_project_2 on contributor (project_id);
alter table mile_stone add constraint fk_mile_stone_project_3 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_mile_stone_project_3 on mile_stone (project_id);
alter table project add constraint fk_project_owner_4 foreign key (owner_login) references user (login) on delete restrict on update restrict;
create index ix_project_owner_4 on project (owner_login);
alter table task add constraint fk_task_creator_5 foreign key (creator_login) references user (login) on delete restrict on update restrict;
create index ix_task_creator_5 on task (creator_login);
alter table task add constraint fk_task_assignee_6 foreign key (assignee_login) references user (login) on delete restrict on update restrict;
create index ix_task_assignee_6 on task (assignee_login);
alter table task add constraint fk_task_mileStone_7 foreign key (mile_stone_id) references mile_stone (id) on delete restrict on update restrict;
create index ix_task_mileStone_7 on task (mile_stone_id);
alter table task add constraint fk_task_project_8 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_task_project_8 on task (project_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists contributor;

drop table if exists mile_stone;

drop table if exists project;

drop table if exists task;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists project_seq;

drop sequence if exists user_seq;

