package aimscli.commands.Fetch;

import aimscli.commands.Login;
import aimscli.commands.Base.SubCmd;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
	name = "cgpa",
	description = "Fetch the CGPA of a student",
	mixinStandardHelpOptions = true
)
public class fetchCGPA extends SubCmd{

	@Option(names={"-s", "--student"}, description = "Student whose grade is needed (Ignored when Student)")
		String student_id;

	@Override
	public Integer call() throws Exception {
		if(user.privilege != Privilege.STU){
			if(student_id == null || !validUid(student_id)){
				student_id = fetchLine("Student ID: ", "Invalid ID", this::validUid);
			}
		}

		try{
			float cgpa = CGPA(student_id);
			Login.out.println(student_id + " currently has a CGPA of: " + cgpa);

			return 0;
		}catch(Exception e){
			Login.out.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
