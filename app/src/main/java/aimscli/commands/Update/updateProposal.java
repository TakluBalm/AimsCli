package aimscli.commands.Update;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Course;
import aimscli.pgManager.ProposalTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
	name = "proposal",
	description = "Update some fields of a proposed course",
	mixinStandardHelpOptions = true
)
public class updateProposal extends SubCmd{

	@Parameters(index = "0")
		String course_id;
	@Option(names={"-n", "--name"}, description = "Change the name field")
		String name;
	@Option(names={"--ltp"}, description = "Change the ltp structure")
		String ltp;
	@Option(names={"-c", "--credits"}, description = "Change number of credits")
		Integer credits;
	@Option(names={"-d", "--dept"}, description = "Change the parent department (Ignored when Faculty)")
		String dept;
	@Option(names={"-p", "--prereq"}, description = "Change the prerequisites")
		String arg_prereq;
	@Option(names={"-t", "--type"}, description = "Change the course type(PC/PE/CP)")
		String ctype;
	@Option(names={"--desc"}, description = "Change the Course description")
		boolean desc;

	@Override
	public Integer call() throws Exception {
		try{
			Course orig = new Course();
			Course updates = new Course();
			
			if(course_id == null || !validUid(course_id)){
				course_id = fetchLine("Course ID: ", "Invalid Input", this::validUid);
			}
			orig.course_id = course_id;
	
			if(name != null && !validDept(name)){
				name = fetchLine("Name: ", "Invalid Name", this::validDept);
			}
			updates.name = name;

			updates.credits = credits;
	
			if(desc){
				updates.description = fetchMultiLine("Description (Empty line marks the end): ");
			}
			
			if(ltp != null && !validLTP(ltp)){
				ltp = fetchLine("LTP: ", "Invalid Input", this::validLTP);
			}
			updates.ltp = ltp;
	
			if(arg_prereq != null){
				if(!validArray(arg_prereq))	arg_prereq = fetchLine("Prerequistes(comma-seperated): ", "Invalid format", this::validArray);
				else updates.prereq = extractPrereq(arg_prereq.split(","));
			}
	
			if(dept != null && !validDept(dept)){
				dept = fetchLine("Department: ", "Invalid Department name", this::validDept);
			}
			updates.dept = dept;
	
			if(ctype != null && !validCType(ctype)){
				ctype = fetchLine("Course Type: ", "Invalid Type", this::validCType);
			}
			updates.course_type = ctype;

			ProposalTable pt = new ProposalTable();
			pt.update(user, orig, updates);
			pgManager.commit();

			return 0;
		}catch (Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
