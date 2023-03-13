package aimscli.commands;

import aimscli.commands.Base.Cmd;
import aimscli.dataObjects.Enrollment;
import aimscli.dataObjects.Offering;
import aimscli.pgManager.EnrollmentTable;
import aimscli.pgManager.OfferingTable;
import aimscli.pgManager.pgManager;
import aimscli.pgManager.pgManager.User;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class UploadGrades extends Cmd{

	User u;

	public UploadGrades(User user){
		super(user);
		u = user;
	}

	private class Grade{
		public String sid, grade;

		public Grade(){}
	}

	@Override
	public Cmd clone() {
		return new UploadGrades(u);
	}

	@Parameters(index="0", arity = "1")
		String course_id;
	@Option(names={"-f", "--file"}, description = "Path to file containing grades")
		File file;

	@Override
	public Integer call() throws Exception {

		ArrayList<Grade> grades = new ArrayList<Grade>();

		if(file == null){
			do{
				file = new File(fetchLine("Filename(.txt): "));
			}while(!file.exists());
		}

		Offering of = new Offering();
		of.course_id = course_id;
		of.session_id = pgManager.currentSession();

		try{
			OfferingTable ot = new OfferingTable();
			ResultSet res = ot.query(user, of);

			//	CHECK IF COURSE OFFERED
			if(!res.next()){
				res.close();
				throw new Exception(course_id + " has not been offered in this session");
			}
			res.close();


			//	READ FROM FILE
			Scanner sc = new Scanner(file);

			while(sc.hasNext()){
				Grade g = new Grade();
				g.sid = sc.next();
				g.grade = sc.next();
				
				if(!validUid(g.sid) || !validGrade(g.grade)){
					sc.close();
					throw new Exception("Invalid File Contents");
				}

				grades.add(g);
			}

			sc.close();

			EnrollmentTable et = new EnrollmentTable();

			Enrollment orig = new Enrollment();
			orig.course_id = course_id;
			orig.session_id = of.session_id;

			Enrollment updates = new Enrollment();

			for(int i = 0; i < grades.size(); i++){
				Grade g = grades.get(i);
				orig.student_id = g.sid;
				updates.grade = g.grade;

				if(!isEnrolled(orig)){
					throw new Exception(orig.student_id + " is not enrolled in the course");
				}

				et.update(user, orig, updates);
			}

			orig.student_id = null;
			int cnt = et.queryCount(u, orig);

			if(cnt != grades.size())	throw new Exception("Provide grades for all students");

			pgManager.commit();
			return 0;
		}catch(Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}

	private boolean isEnrolled(Enrollment orig) throws Exception {
		ResultSet res = new EnrollmentTable().query(user, orig);
		if(!res.next())	return false;
		return true;
	}
}
