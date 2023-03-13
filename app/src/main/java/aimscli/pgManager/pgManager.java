package aimscli.pgManager;

import java.sql.*;

public class pgManager {
	private static Connection c;
	protected static Statement stmt;

	public static void init(boolean first_run){
		try {
			c = DriverManager.getConnection("jdbc:postgresql://localhost/test", "test", "test123");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			if(first_run){
			stmt.execute(
					"create table acadSession( "+
					"id serial, "+
					"year varchar(4), "+
					"sem char, "+
					"current boolean default true, "+
					"primary key(id) "+
					"); "+
					"create or replace function session_insert_trigger() "+
					"returns trigger "+
					"language plpgsql "+
					"as $$ "+
					"declare "+
					"cnt int; "+
					"begin "+
					"select count(*) into cnt from acadSession where year=new.year and sem=new.sem; "+
					"if cnt = 0 then "+
					"return new; "+
					"end if; "+
					"raise exception 'Session already present'; "+
					"end "+
					"$$ "+
					"; "+
					"create table batch( "+
					"name varchar(4) primary key "+
					"); "+
					"create table programme( "+
					"name varchar primary key "+
					"); "+
					"create table dept( "+
					"name varchar primary key "+
					"); "+
					"create table curriculum( "+
					"batch varchar, "+
					"programme varchar, "+
					"pc int, "+
					"pe int, "+
					"cp int, "+
					"foreign key(batch) references batch(name), "+
					"foreign key(programme) references programme(name) "+
					"); "+
					"create table auth( "+
					"user_id varchar primary key, "+
					"passwd varchar not null, "+
					"role char not null, "+
					"dept varchar, "+
					"foreign key (dept) references dept(name) "+
					"); "+
					"create or replace function auth_insert_trigger() "+
					"returns trigger "+
					"language plpgsql "+
					"as $$ "+
					"declare "+
					"cnt int = 0; "+
					"begin "+
					"select count(*) from auth where user_id = new.user_id into cnt; "+
					"if cnt = 0 then "+
					"return new; "+
					"else "+
					"raise exception 'User with user_id ''%'' already exists', new.user_id; "+
					"end if; "+
					"end "+
					"$$; "+
					"create trigger auth_insert_trigger before insert on auth for each row execute procedure auth_insert_trigger(); "+
					"create table  student ( "+
					"auth_id varchar primary key, "+
					"name varchar not null, "+
					"surname varchar, "+
					"batch varchar(4) not null, "+
					"programme varchar not null, "+
					"foreign key (auth_id) references auth(user_id), "+
					"foreign key (batch) references batch(name), "+
					"foreign key (programme) references programme(name) "+
					"); "+
					"create or replace function student_insert_trigger() "+
					"returns TRIGGER "+
					"language plpgsql "+
					"as $FUNC$ "+
					"declare "+
					"r char; "+
					"begin "+
					"select role from auth where user_id=new.auth_id into r; "+
					"if r <> 'S' then "+
					"raise exception 'Not a student'; "+
					"end if; "+
					"return new; "+
					"end $FUNC$ "+
					"; "+
					"create or replace function student_afterInsert_trigger() "+
					"returns TRIGGER "+
					"language PLPGSQL "+
					"as $$ "+
					"begin "+
					"insert into credits(student_id) values (new.auth_id); "+
					"return null; "+
					"end "+
					"$$ "+
					"; "+
					"create trigger student_insert_trigger before insert on student for each row execute procedure student_insert_trigger(); "+
					"create trigger student_afterInsert_trigger after insert on student for each row execute procedure student_afterInsert_trigger(); "+
					"create table  credits( "+
					"student_id varchar primary key, "+
					"prev_credits int[2] default ARRAY[19, 19], "+
					"current int default 0, "+
					"total int default 0, "+
					"pc int default 0, "+
					"pe int default 0, "+
					"cp int default 0 "+
					"); "+
					"create table  faculty( "+
					"auth_id varchar primary key, "+
					"name varchar not null, "+
					"surname varchar, "+
					"foreign key (auth_id) references auth(user_id) "+
					"); "+
					"create or replace function faculty_insert_trigger() "+
					"returns TRIGGER "+
					"language plpgsql "+
					"as $FUNC$ "+
					"declare "+
					"r char; "+
					"begin "+
					"select role from auth where user_id=new.auth_id into r; "+
					"if r <> 'F' then "+
					"raise exception 'Not a student'; "+
					"end if; "+
					"return new; "+
					"end $FUNC$ "+
					"; "+
					"create trigger faculty_insert_trigger before insert on faculty for each row execute procedure faculty_insert_trigger(); "+
					"create table  courses( "+
					"course_id varchar primary key, "+
					"name varchar not null, "+
					"description varchar, "+
					"prereq varchar[], "+
					"dept varchar not null, "+
					"ltp varchar(10) not null, "+
					"credits int not null, "+
					"course_type varchar(2) not null, "+
					"foreign key(dept) references dept(name) "+
					"); "+
					"create or replace function courses_insert_trigger() "+
					"returns TRIGGER "+
					"language plpgsql "+
					"as $$ "+
					"declare "+
					"pr varchar; "+
					"cnt int; "+
					"begin "+
					"if new.prereq = NULL then "+
					"return new; "+
					"end if; "+
					"for pr in select unnest(new.prereq) loop "+
					"select count(*) from courses where course_id = pr into cnt; "+
					"if cnt = 0 then "+
					"raise exception 'No course corresponding to %', pr; "+
					"return old; "+
					"end if; "+
					"end loop; "+
					"return new; "+
					"end "+
					"$$ "+
					"; "+
					"create trigger courses_insert_trigger before insert on courses for each row execute procedure courses_insert_trigger(); "+
					"create or replace function courses_update_trigger() "+
					"returns TRIGGER "+
					"language plpgsql "+
					"as $$ "+
					"declare "+
					"pr varchar; "+
					"cnt int; "+
					"begin "+
					"if new.prereq = old.prereq then "+
					"return new; "+
					"end if; "+
					"for pr in select unnest(new.prereq) loop "+
					"select count(*) from courses where course_id = pr into cnt; "+
					"if cnt = 0 then "+
					"raise exception 'No course corresponding to %', pr; "+
					"return old; "+
					"end if; "+
					"end loop; "+
					"return new; "+
					"end "+
					"$$ "+
					"; "+
					"create trigger courses_update_trigger before update on courses for each row execute procedure courses_update_trigger(); "+
					"create table  offerings( "+
					"course_id varchar, "+
					"instructor_id varchar not null, "+
					"session_id int, "+
					"minCG real, "+
					"completed boolean default false, "+
					"foreign key(course_id) references courses(course_id), "+
					"foreign key(session_id) references acadSession(id), "+
					"foreign key(instructor_id) references faculty(auth_id), "+
					"primary key(course_id, session_id) "+
					"); "+
					"create table  enrollment( "+
					"student_id varchar, "+
					"course_id varchar, "+
					"session_id int, "+
					"grade varchar(2), "+
					"primary key(student_id, course_id, session_id), "+
					"foreign key(student_id) references auth(user_id), "+
					"foreign key(course_id, session_id) references offerings(course_id, session_id) "+
					"); "+
					"create table  proposal( "+
					"course_id varchar primary key, "+
					"name varchar not null, "+
					"description varchar, "+
					"prereq varchar[], "+
					"dept varchar not null, "+
					"ltp varchar(10) not null, "+
					"credits int not null, "+
					"course_type varchar(2) not null, "+
					"foreign key(dept) references dept(name) "+
					"); "+
					"create trigger proposal_insert_trigger before insert on proposal for each row execute procedure courses_insert_trigger(); "+
					"create trigger proposal_update_trigger before update on proposal for each row execute procedure courses_update_trigger(); "+
					"insert into auth(user_id, passwd, role) values ('admin', 'iitropar', 'A'); "
			);
			c.commit();
			}
		} catch (SQLException e) {
			System.out.println(e);
			System.exit(1);
		}
	}

	public static class User{
		public String user_id;
		public final Privilege privilege;
		public String dept;

		private User(Privilege p){
			this.privilege = p;
		}

		public User(){
			this.privilege = Privilege.GST;
		}
	}

	public static void startSession(User u, String year, char sem) throws Exception{
		if(u.privilege != Privilege.ADM)	throw new Exception("Permission Denied");

		ResultSet res = stmt.executeQuery("SELECT * FROM acadSession WHERE current=true;");
		if(res.next()){
			res.close();
			throw new Exception("Cannot start another session. Close ongoing session first");
		}
		res.close();

		stmt.execute(String.format("INSERT INTO acadSession(year, sem) VALUES ('%s', '%s');", year, sem));
		pgManager.commit();
	}
	
	public static void endSession(User u) throws Exception{
		if(u.privilege != Privilege.ADM)	throw new Exception("Permission Denied");
		
		ResultSet res = stmt.executeQuery("select * from enrollment where grade=null");
		if(res.next()){
			res.close();
			throw new Exception("Grades have not been uploaded yet");
		}
		res.close();
		
		stmt.execute("UPDATE credits SET prev_credits[1]=prev_credits[2], prev_credits[2]=current, current=0;");
		stmt.execute("UPDATE acadSession SET current=false WHERE current=true");
		pgManager.commit();
		session_id = null;
	}

	public static String currentYear() throws Exception{
		ResultSet res = stmt.executeQuery("SELECT year FROM acadSession WHERE current=true");
		if(!res.next()){
			throw new Exception("No session in progress");
		}
		return res.getString(1);
	}

	public static Character currentSem() throws Exception{
		ResultSet res = stmt.executeQuery("SELECT sem FROM acadSession WHERE current=true");
		if(!res.next()){
			throw new Exception("No session in progress");
		}
		return res.getString(1).charAt(0);
	}

	static Integer session_id = null;

	public static Integer currentSession() throws Exception{
		if(session_id != null)	return session_id;

		ResultSet res = stmt.executeQuery("SELECT id FROM acadSession WHERE current=true");
		if(!res.next()){
			throw new Exception("No session in progress");
		}
		return session_id = res.getInt(1);
	}

	public static Integer getSessionID(String year, String sem) throws Exception{
		ResultSet res = stmt.executeQuery(String.format("SELECT id FROM acadSession WHERE year='%s' and sem='%s'", year, sem));
		if(!res.next())	return null;
		return res.getInt(1);
	}

	public enum Privilege{
		STU,
		FAC,
		ADM,
		GST
	}

	public static Statement getStmt(){
		return stmt;
	}

	public static User authenticate(String user_id, String passwd) throws Exception{
		String query = String.format(
			"SELECT role,dept FROM auth WHERE user_id = '%s' and passwd = '%s';",
			user_id,
			passwd
		);
		ResultSet res = stmt.executeQuery(query);
		if(!res.next()){
			return null;
		}		
		String dept = res.getString(2);
		char r = res.getString(1).charAt(0);
		res.close();
	
		User u;
		switch(r){
			case 'S':
				u = new User(Privilege.STU);
				break;
			case 'F':
				u = new User(Privilege.FAC);
				break;
			case 'A':
				u = new User(Privilege.ADM);
				break;
			default:
				return null;
		}
		u.user_id = user_id;
		u.dept = dept;
		return u;
	}

	public static void commit() throws Exception{
		c.commit();
	}

	public static void rollback() throws Exception{
		c.rollback();
	}
}
