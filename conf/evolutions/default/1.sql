# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table contributor (
  id                        bigint auto_increment not null,
  user_login                varchar(255),
  project_id                bigint,
  role                      integer,
  constraint ck_contributor_role check (role in (0,1,2)),
  constraint pk_contributor primary key (id))
;

create table history_event (
  id                        bigint auto_increment not null,
  comment                   TEXT,
  change_to                 integer,
  user_login                varchar(255),
  task_id                   bigint,
  date                      timestamp,
  constraint ck_history_event_change_to check (change_to in (0,1)),
  constraint pk_history_event primary key (id))
;

create table mile_stone (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               TEXT,
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
  task_number               bigint,
  name                      varchar(255),
  description               TEXT,
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

create table appUser (
  login                     varchar(255) not null,
  password                  varbinary(255),
  constraint pk_appUser primary key (login))
;

create table work_report (
  id                        bigint auto_increment not null,
  contributor_id            bigint,
  task_id                   bigint,
  date                      timestamp,
  hours_count               integer,
  constraint pk_work_report primary key (id))
;

create sequence project_seq;

create sequence appUser_seq;

alter table contributor add constraint fk_contributor_user_1 foreign key (user_login) references appUser (login) on delete restrict on update restrict;
create index ix_contributor_user_1 on contributor (user_login);
alter table contributor add constraint fk_contributor_project_2 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_contributor_project_2 on contributor (project_id);
alter table history_event add constraint fk_history_event_user_3 foreign key (user_login) references appUser (login) on delete restrict on update restrict;
create index ix_history_event_user_3 on history_event (user_login);
alter table history_event add constraint fk_history_event_task_4 foreign key (task_id) references task (id) on delete restrict on update restrict;
create index ix_history_event_task_4 on history_event (task_id);
alter table mile_stone add constraint fk_mile_stone_project_5 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_mile_stone_project_5 on mile_stone (project_id);
alter table project add constraint fk_project_owner_6 foreign key (owner_login) references appUser (login) on delete restrict on update restrict;
create index ix_project_owner_6 on project (owner_login);
alter table task add constraint fk_task_creator_7 foreign key (creator_login) references appUser (login) on delete restrict on update restrict;
create index ix_task_creator_7 on task (creator_login);
alter table task add constraint fk_task_assignee_8 foreign key (assignee_login) references appUser (login) on delete restrict on update restrict;
create index ix_task_assignee_8 on task (assignee_login);
alter table task add constraint fk_task_mileStone_9 foreign key (mile_stone_id) references mile_stone (id) on delete restrict on update restrict;
create index ix_task_mileStone_9 on task (mile_stone_id);
alter table task add constraint fk_task_project_10 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_task_project_10 on task (project_id);
alter table work_report add constraint fk_work_report_contributor_11 foreign key (contributor_id) references contributor (id) on delete restrict on update restrict;
create index ix_work_report_contributor_11 on work_report (contributor_id);
alter table work_report add constraint fk_work_report_task_12 foreign key (task_id) references task (id) on delete restrict on update restrict;
create index ix_work_report_task_12 on work_report (task_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists contributor;

drop table if exists history_event;

drop table if exists mile_stone;

drop table if exists project;

drop table if exists task;

drop table if exists appUser;

drop table if exists work_report;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists project_seq;

drop sequence if exists appUser_seq;

