package aimscli.commands.Fetch;

import java.sql.ResultSet;

import aimscli.commands.Login;
import aimscli.commands.Base.SubCmd;
import aimscli.dataObjects.Enrollment;
import aimscli.pgManager.EnrollmentTable;
import aimscli.pgManager.pgManager;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
	name = "grade",
	description = "Fetch grade of students",
	mixinStandardHelpOptions = true
)
public class fetchGrade extends SubCmd{

	@Parameters(index = "0")
		String course_id;
	@Option(names={"--id"}, description = "Provide Student's ID")
		String student_id;
	@Option(names={"--year"}, description = "Year the course was floated in")
		String year;
	@Option(names={"--sem"}, description = "Semester the course was floated in")
		String sem;

	@Override
	public Integer call() throws Exception {
		try{	
			if(user.privilege == Privilege.STU){
				student_id = user.user_id;
			}
			Enrollment e = new Enrollment();
			e.course_id = course_id;
			e.student_id = student_id;
			if(year == null || sem == null || !validBatch(year) || !validSem(sem)){
				e.session_id = pgManager.currentSession();
			}else{
				e.session_id = pgManager.getSessionID(year, sem);
			}

			EnrollmentTable et = new EnrollmentTable();
			ResultSet r = et.query(user, e);

			if(!r.next()){
				throw new Exception("Invalid Request. No student is enrolled to such a course");
			}

			e = e.parse(r);
			if(e.grade == null){
				Login.out.println("The course has not yet been graded");
			}else{
				Login.out.println(e.student_id + " recieved " + e.grade + " grade");
				while(r.next()){
					e = e.parse(r);
					Login.out.println(e.student_id + " recieved " + e.grade + " grade");
				}
			}
			pgManager.commit();
			return 0;
		}catch(Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}

	}
	
}
