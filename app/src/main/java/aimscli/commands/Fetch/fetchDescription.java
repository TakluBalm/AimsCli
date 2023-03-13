package aimscli.commands.Fetch;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Course;
import aimscli.pgManager.CourseTable;
import aimscli.pgManager.ProposalTable;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.sql.ResultSet;

@Command(
	name = "description",
	description = "Fetch description of a Course",
	mixinStandardHelpOptions = true
)
public class fetchDescription extends SubCmd{

	@Parameters(index="0")	String course_id;
	@Option(names={"--check-proposals"}, description = "Check in the proposals table as well")
		boolean checkProposals;

	@Override
	public Integer call() throws Exception {
		try{
			Course c = new Course();
			c.course_id = course_id;

			int cnt = 0;

			CourseTable ct = new CourseTable();
			ResultSet res = ct.query(user, c);
			if(res.next()){
				cnt++;
				c = c.parse(res);
				if(!checkProposals)
					Login.out.println("Description: ");
				else
					Login.out.println("1. Description (Course Table): ");
				if(c.description != null){
					c.description = c.description.replaceAll("\n", "\n\t");
				}
				Login.out.println(c.description);
				return 0;
			}
			if(!checkProposals)	throw new Exception("No such course");

			ProposalTable pt = new ProposalTable();

			Course p = new Course();
			p.course_id = c.course_id;
			
			res = pt.query(user, p);
			if(res.next()){
				cnt++;
				p = p.parse(res);
				Login.out.printf("%d. Description:\n", cnt);
				if(p.description != null)	Login.out.println(p.description.replaceAll("\n", "\n\t"));
				else{
					Login.out.println("\tDoes not have a description");
				}
			}

			return 0;

		}catch(Exception e){
			Login.err.println(ansi.Err(e.getMessage()));;
			return 1;
		}
	}
		
}
