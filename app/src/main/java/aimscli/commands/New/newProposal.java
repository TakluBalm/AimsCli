package aimscli.commands.New;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Course;
import aimscli.pgManager.ProposalTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
	name = "proposal",
	description = "Propose a new Course to be added to catalog",
	mixinStandardHelpOptions = true
)
public class newProposal extends SubCmd{

	@Option(names={"-i", "--id"}, description = "Course ID")
		String course_id;
	@Option(names={"-c", "--creds"}, description = "Course credits")
		Integer credits;
	@Option(names={"-n", "--name"}, description = "Course name")
		String arg_name;
	@Option(names={"-p", "--prereq"}, description = "Prerequisites for the Course")
		String arg_prereq;
	@Option(names={"--ltp"}, description = "LTP structure of Course")
		String arg_ltp;
	@Option(names={"-d", "--dept"}, description = "Department that is proposing the Course (Ignored if Faculty)")
		String dept;
	@Option(names={"-t", "--type"}, description = "Course Type(PC/PE/CP)")
		String ctype;
	@Option(names={"--desc"}, description = "Directs the Command to prompt for Course Description")
		boolean desc;

	@Override
	public Integer call() throws Exception {
		Course p = new Course();

		
		if(course_id == null || !validUid(course_id)){
			course_id = fetchLine("Course ID: ", "Invalid Input", this::validUid);
		}
		p.course_id = course_id;

		if(arg_name == null || !validDept(arg_name)){
			arg_name = fetchLine("Name: ", "Invalid Name", this::validDept);
		}
		p.name = arg_name;

		if(credits == null){
			credits = Integer.parseInt(fetchLine("Credits: ", "Invalid Input", this::validNum));
		}
		p.credits = credits;

		if(desc){
			p.description = fetchMultiLine("Description (Empty line marks the end): ");
		}
		
		if(arg_ltp == null || !validLTP(arg_ltp)){
			arg_ltp = fetchLine("LTP: ", "Invalid Input", this::validLTP);
		}
		p.ltp = arg_ltp;

		if(arg_prereq != null && !validArray(arg_prereq)){
			arg_prereq = fetchLine("Prerequistes(comma-seperated): ", "Invalid format", this::validArray);
			p.prereq = extractPrereq(arg_prereq.split(","));
		}else{
			p.prereq = ((arg_prereq == null)?null:extractPrereq(arg_prereq.split(",")));
		}

		if(user.dept != null){
			dept = user.dept;
		}else if(dept == null || !validDept(dept)){
			dept = fetchLine("Department: ", "Invalid Department name", this::validDept);
		}
		p.dept = dept;

		if(ctype == null || !validCType(ctype)){
			ctype = fetchLine("Course Type: ", "Invalid Type", this::validCType);
		}
		p.course_type = ctype;

		try{
			ProposalTable pt = new ProposalTable();
			pt.insert(user, p);
			pgManager.commit();
		}catch(Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}

		return 0;
	}	
}
