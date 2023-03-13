create table acadSession(
  id serial,
  year varchar(4),
  sem char,
  current boolean default true,
  primary key(id)
);

create or replace function session_insert_trigger()
returns trigger
language plpgsql
as $$
declare
  cnt int;
begin
  select count(*) into cnt from acadSession where year=new.year and sem=new.sem;
  if cnt = 0 then
  	return new;
  end if;
  raise exception 'Session already present';
end
$$
;

-- BATCH TABLE
create table batch(
	name varchar(4) primary key
);
-- \\ BATCH TABLE

-- PROGRAMME TABLE
create table programme(
	name varchar primary key
);
-- \\ PROGRAMME TABLE

-- DEPARTMENT TABLE
create table dept(
    name varchar primary key
);
-- \\ DEPARTMENT TABLE

create table curriculum(
  batch varchar,
  programme varchar,
  pc int,
  pe int,
  cp int,
  foreign key(batch) references batch(name),
  foreign key(programme) references programme(name)
);

-- AUTH TABLE
create table auth(
    user_id varchar primary key,
    passwd varchar not null,
    role char not null,
    dept varchar,
    foreign key (dept) references dept(name)
);


-- AUTH TRIGGER FOR INSERT
create or replace function auth_insert_trigger()
returns trigger
language plpgsql
as $$
declare
  cnt int = 0;
begin
  select count(*) from auth where user_id = new.user_id into cnt;

  if cnt = 0 then
    return new;
  else
    raise exception 'User with user_id ''%'' already exists', new.user_id;
  end if;

end
$$;

create trigger auth_insert_trigger before insert on auth for each row execute procedure auth_insert_trigger();

-- \ TRIGGER ADDED
-- \\ AUTH TABLE

-- STUDENT TABLE
create table  student (
  auth_id varchar primary key,
  name varchar not null,
  surname varchar,
  batch varchar(4) not null,
  programme varchar not null,
  foreign key (auth_id) references auth(user_id),
  foreign key (batch) references batch(name),
  foreign key (programme) references programme(name)
);

-- STUDENT TRIGGER FOR INSERT
create or replace function student_insert_trigger() 
returns TRIGGER
language plpgsql
as $FUNC$
declare
  r char;
begin
  select role from auth where user_id=new.auth_id into r;
  if r <> 'S' then
    raise exception 'Not a student';
  end if;
  return new;
end $FUNC$
;

create or replace function student_afterInsert_trigger()
returns TRIGGER
language PLPGSQL
as $$
begin
  insert into credits(student_id) values (new.auth_id);
  return null;
end
$$
;

create trigger student_insert_trigger before insert on student for each row execute procedure student_insert_trigger();

create trigger student_afterInsert_trigger after insert on student for each row execute procedure student_afterInsert_trigger();
-- \\ TRIGGER ADDED
-- \\ STUDENT TABLE

-- STU_CREDITS
create table  credits(
  student_id varchar primary key,
  prev_credits int[2] default ARRAY[19, 19],
  current int default 0,
  total int default 0,
  pc int default 0,
  pe int default 0,
  cp int default 0
);
-- \\ STU_CREDITS

-- FACULTY TABLE
create table  faculty(
  auth_id varchar primary key,
  name varchar not null,
  surname varchar, 
  foreign key (auth_id) references auth(user_id)
);

-- FACULTY TRIGGER FOR INSERT
create or replace function faculty_insert_trigger() 
returns TRIGGER
language plpgsql
as $FUNC$
declare
  r char;
begin
  select role from auth where user_id=new.auth_id into r;
  if r <> 'F' then
    raise exception 'Not a student';
  end if;
  return new;
end $FUNC$
;

create trigger faculty_insert_trigger before insert on faculty for each row execute procedure faculty_insert_trigger();
-- \\ TRIGGER ADDED
-- \\ FACULTY TABLE



-- COURSES TABLE
create table  courses(
  course_id varchar primary key,
  name varchar not null,
  description varchar,
  prereq varchar[],
  dept varchar not null,
  ltp varchar(10) not null,
  credits int not null,
  course_type varchar(2) not null,
  foreign key(dept) references dept(name)
);

-- COURSES INSERT TRIGGER
create or replace function courses_insert_trigger()
returns TRIGGER
language plpgsql
as $$
declare
  pr varchar;
  cnt int;
begin

  if new.prereq = NULL then
    return new;
  end if;

  for pr in select unnest(new.prereq) loop
    select count(*) from courses where course_id = pr into cnt;
    if cnt = 0 then
      raise exception 'No course corresponding to %', pr;
      return old;
    end if;
  end loop;

  return new;
end
$$
;

create trigger courses_insert_trigger before insert on courses for each row execute procedure courses_insert_trigger();

-- \ TRIGGER ADDED

-- COURSES UPDATE TRIGGER
create or replace function courses_update_trigger()
returns TRIGGER
language plpgsql
as $$
declare
  pr varchar;
  cnt int;
begin
  
  if new.prereq = old.prereq then
    return new;
  end if;
  
  for pr in select unnest(new.prereq) loop
    select count(*) from courses where course_id = pr into cnt;
    if cnt = 0 then
      raise exception 'No course corresponding to %', pr;
      return old;
    end if;
  end loop;
  
  return new;
end
$$
;

create trigger courses_update_trigger before update on courses for each row execute procedure courses_update_trigger();

-- \ TRIGGER ADDED
-- \\ COURSES TABLE

-- OFFERINGS TABLE
create table  offerings(
  course_id varchar,
  instructor_id varchar not null,
  session_id int,
  minCG real,
  completed boolean default false,
  foreign key(course_id) references courses(course_id),
  foreign key(session_id) references acadSession(id),
  foreign key(instructor_id) references faculty(auth_id),
  primary key(course_id, session_id)
);
-- \\ OFFERINGS TABLE

-- ENROLLMENTS TABLE
create table  enrollment(
  student_id varchar,
  course_id varchar,
  session_id int,
  grade varchar(2),
  primary key(student_id, course_id, session_id),
  foreign key(student_id) references auth(user_id),
  foreign key(course_id, session_id) references offerings(course_id, session_id)
);
-- \\ ENROLLMENTS TABLE

-- PROPOSAL TABLE
create table  proposal(
  course_id varchar primary key,
  name varchar not null,
  description varchar,
  prereq varchar[],
  dept varchar not null,
  ltp varchar(10) not null,
  credits int not null,
  course_type varchar(2) not null,
  foreign key(dept) references dept(name)
);

-- PROPOSAL INSERT TRIGGER
create trigger proposal_insert_trigger before insert on proposal for each row execute procedure courses_insert_trigger();
-- \\ TRIGGER ADDED FOR INSERT
-- PROPOSAL UPDATE TRIGGER
create trigger proposal_update_trigger before update on proposal for each row execute procedure courses_update_trigger();

insert into auth(user_id, passwd, role) values ('admin', 'iitropar', 'A');
-- \\ TRIGGER ADDED FOR UPDATE
-- \\ PROPOSAL TABLE

-- TEST
-- insert into dept values ('CSE'), ('EE');
-- insert into batch values('2020');
-- insert into programme values ('BTech');
-- insert into acadSession(year, sem) values ('2023', '1');

-- insert into auth(user_id, passwd, role, dept) values ('2020csb1082', '123', 'S', 'CSE');
-- insert into auth(user_id, passwd, role, dept) values ('2020eeb1148', '345', 'S', 'EE');
-- insert into auth(user_id, passwd, role, dept) values ('apurva.mudgal', 'lemma1', 'F', 'CSE');
-- insert into auth(user_id, passwd, role) values ('admin', 'iitropar', 'A');
-- insert into auth(user_id, passwd, role, dept) values ('anil.shukla', 'kamedi', 'F', 'CSE');

-- insert into student(auth_id, name, surname, batch, programme) values ('2020csb1082', 'Jugal', 'Chapatwala', '2020', 'BTech');

-- insert into faculty(auth_id, name, surname) values ('apurva.mudgal', 'Apurva', 'Mudgal');
-- insert into faculty(auth_id, name, surname) values ('anil.shukla', 'Anil', 'Shukla');

-- insert into courses(course_id, name, ltp, credits) values ('CS101', 'Discrete math', '2-3-4', '4');
-- insert into courses(course_id, name, ltp, credits, prereq) values ('CS301', 'TOC', '2-3-4', '4', array['CS101']);

-- insert into offerings(course_id, instructor_id, session_id) values ('CS101', 'apurva.mudgal', '1');
-- insert into offerings(course_id, instructor_id, session_id) values ('CS301', 'anil.shukla', '1');

-- insert into enrollment(student_id, course_id, session_id) values ('2020csb1082', 'CS101', '1');

-- select * from dept;
-- select * from student;
-- select * from credits;
-- select * from faculty;
-- select * from batch;
-- select * from programme;
-- select * from auth;
-- select * from courses;
-- select * from proposal;
-- select * from offerings;
-- select * from enrollment;
-- // TEST END