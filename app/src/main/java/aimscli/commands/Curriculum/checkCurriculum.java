package aimscli.commands.Curriculum;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Credits;
import aimscli.dataObjects.Curriculum;
import aimscli.dataObjects.Student;
import aimscli.pgManager.CreditsTable;
import aimscli.pgManager.CurriculumTable;
import aimscli.pgManager.StudentTable;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.sql.ResultSet;

@Command(
	name = "check",
	description = "Check if a student has completed his/her curriculum",
	mixinStandardHelpOptions = true
)
public class checkCurriculum extends SubCmd{
	@Option(names={"-s", "--student"}, description = "Student ID (Ignored if Student)")
		String student_id;

	@Override
	public Integer call() throws Exception {

		if(user.privilege != Privilege.STU){
			if(student_id == null || !validUid(student_id)){
				student_id = fetchLine("Student ID: ", "Invalid ID", this::validUid);
			}
		}else{
			student_id = user.user_id;
		}

		try{
			StudentTable st = new StudentTable();
			Student s = new Student();
			s.auth_id = student_id;

			ResultSet res = st.query(user, s);
			if(!res.next())	throw new Exception("No such student");
			s = s.parse(res);

			CurriculumTable ct = new CurriculumTable();
			Curriculum c = new Curriculum();
			c.batch = s.batch;
			c.programme = s.programme;

			res = ct.query(user, c);
			if(!res.next())	throw new Exception(String.format("No curriculum for (%s, %s) defined", s.batch, s.programme));
			c = c.parse(res);

			CreditsTable crt = new CreditsTable();
			Credits cr = new Credits();
			cr.student_id = student_id;

			res = crt.query(user, cr);
			res.next();
			cr = cr.parse(res);

			if(cr.cp >= c.cp && cr.pc >= c.pc && cr.pe >= c.pe){
				Login.out.println(student_id + "'s curriculum is complete");
				return 0;
			}
			Login.out.println(student_id + "'s curriculum is yet to complete. He/She needs:");
			if(cr.cp < c.cp)	Login.out.println((c.cp-cr.cp)+" Capstone Credits");
			if(cr.pc < c.pc)	Login.out.println((c.pc-cr.pc)+" Programme Core Credits");
			if(cr.pe < c.pe)	Login.out.println((c.pe-cr.pe)+" Programme Elective Credits");
			return 0;
		}catch(Exception e){
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
