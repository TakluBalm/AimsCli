package aimscli.commands.Fetch;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Enrollment;
import aimscli.pgManager.EnrollmentTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.sql.ResultSet;

@Command(
	name = "enrollments",
	description = "Fetch enrollments in a particular course"
)
public class fetchEnrollments extends SubCmd{

	@Option(names={"-c", "--course"}, description = "Enrollment info from this course")
		String course_id;

	@Override
	public Integer call() throws Exception {
		try{
			EnrollmentTable et = new EnrollmentTable();
			Enrollment ref = new Enrollment();
			ref.session_id = pgManager.currentSession();

			if(course_id != null && !validUid(course_id)){
				course_id = fetchLine("Course ID: ", "Invalid ID", this::validUid);
			}
			ref.course_id = course_id;

			ResultSet res = et.query(user, ref);

			Login.out.println("\t(course_id, student_id, session_id, grade)\n");
			int k = 1;
			while(res.next()){
				ref = ref.parse(res);
				Login.out.printf(
					"%d. ('%s', '%s', %d, %s)\n",
					k++, ref.course_id, ref.student_id, ref.session_id, ref.grade
				);
			}
			return 0;
		}catch(Exception e){
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
