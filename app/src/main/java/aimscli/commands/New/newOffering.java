package aimscli.commands.New;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Offering;
import aimscli.pgManager.OfferingTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
	name = "offering",
	description = "Put up a course as being offered for a particular Semester",
	mixinStandardHelpOptions = true
)
public class newOffering extends SubCmd{

	@Option(names={"-c", "--course"}, description = "ID of the course")
		String course_id;
	@Option(names={"-i", "--instructor"}, description = "ID of instructor")
		String instructor_id;
	@Option(names={"--mincg"}, description = "CG constraint")
		Float mincg;

	@Override
	public Integer call() throws Exception {

		if(course_id == null || !validUid(course_id))	course_id = fetchLine("Course ID: ", "Invalid ID", this::validUid);

		if(instructor_id == null || !validUid(instructor_id)) instructor_id = fetchLine("Instructor ID: ", "Invalid ID", this::validUid);
		
		try{
			Offering of = new Offering();
			of.course_id = course_id;
			of.instructor_id = instructor_id;
			of.minCG = mincg;
			of.session_id = pgManager.currentSession();

			OfferingTable ot = new OfferingTable();
			ot.insert(user, of);
			pgManager.commit();
		}catch(Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}

		return 0;
	}
		
}
