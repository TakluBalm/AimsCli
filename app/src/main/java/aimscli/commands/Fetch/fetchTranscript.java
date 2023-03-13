package aimscli.commands.Fetch;

import java.sql.ResultSet;

import aimscli.commands.Login;
import aimscli.commands.Base.SubCmd;
import aimscli.dataObjects.Enrollment;
import aimscli.pgManager.EnrollmentTable;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
	name = "transcript",
	description = "Displays the transcript of a student",
	mixinStandardHelpOptions = true
)
public class fetchTranscript extends SubCmd{

	@Option(names ={"-s", "--student"}, description = "Student whose transcipt needs to be generated")
		String student_id;

	@Override
	public Integer call() throws Exception {
		if(user.privilege != Privilege.STU){
			if(student_id == null || !validUid(student_id)){
				student_id = fetchLine("Student ID: ", "Invalid ID", this::validUid);
			}
		}
		try{
			EnrollmentTable et = new EnrollmentTable();
			Enrollment e = new Enrollment();
			e.student_id = student_id;

			ResultSet r = et.query(user, e);

			int k = 1;
			Login.out.println("\tCourse_ID\tGrade\n");
			while(r.next()){
				e = e.parse(r);
				if(e.grade != null){
					Login.out.println(k+"\t"+e.course_id+"\t"+e.grade);
				}
			}

			float cg = CGPA(student_id);
			Login.out.println("\n\nThe student has CGPA of " + cg);

			return 0;
		}catch(Exception e){
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
