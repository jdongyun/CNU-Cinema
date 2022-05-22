create table Customer
(
    username   varchar(20)  not null
        primary key,
    name       varchar(10)  not null,
    password   varchar(500) not null,
    email      varchar(30)  not null,
    birth_date date         not null,
    sex        char         not null
);

create table Authority
(
    username       varchar(20) null,
    authority_name varchar(20) null,
    constraint authority_unique
        unique (username, authority_name),
    constraint Authority_ibfk_1
        foreign key (username) references Customer (username)
);

create table Movie
(
    mid      int auto_increment primary key,
    title    varchar(30) not null,
    open_day date        not null,
    director varchar(20) not null,
    rating   varchar(3)  null,
    length   int         not null,
    constraint rating
        check (`rating` in ('ALL', '_12', '_15', '_18'))
);

create table Actor
(
    mid  int         not null,
    name varchar(30) not null,
    constraint actor_unique
        unique (mid, name),
    constraint Actor_ibfk_1
        foreign key (mid) references Movie (mid)
);

create table Theater
(
    tname varchar(20) not null
        primary key,
    seats int         not null
);

create table Schedule
(
    sid     int auto_increment primary key,
    mid     int         not null,
    tname   varchar(20) not null,
    show_at datetime    null,
    constraint Schedule_ibfk_1
        foreign key (mid) references Movie (mid),
    constraint Schedule_ibfk_2
        foreign key (tname) references Theater (tname)
);

create index mid
    on Schedule (mid);

create index tname
    on Schedule (tname);

create table Ticketing (
    id int auto_increment primary key,
    username varchar(20) not null,
    sid int  not null,
    rc_at timestamp default current_timestamp() not null,
    seats int not null,
    status char  null,
    constraint Ticketing_ibfk_1 foreign key (username) references Customer (username),
    constraint Ticketing_ibfk_2 foreign key (sid) references Schedule (sid),
    constraint status check (`status` in ('R', 'C'))
);

create index sid
    on Ticketing (sid);

create index username
    on Ticketing (username);

