package aimscli.commands.New;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Course;
import aimscli.pgManager.ProposalTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
	name = "rejection",
	description = "Reject a proposed Course",
	mixinStandardHelpOptions = true
)
public class newRejection extends SubCmd{

	@Parameters(index = "0")
		String course_id;

	@Override
	public Integer call() throws Exception {
		try{
			ProposalTable pt = new ProposalTable();
			Course p = new Course();
			
			if(!validUid(course_id)){
				course_id = fetchLine("Course ID: ", "Invalid ID", this::validUid);
			}
			p.course_id = course_id;

			pt.delete(user, p);
			pgManager.commit();

			return 0;
		}catch(Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
