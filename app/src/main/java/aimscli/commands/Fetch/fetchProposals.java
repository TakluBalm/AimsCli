package aimscli.commands.Fetch;

import aimscli.commands.Base.Cmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Course;
import aimscli.pgManager.ProposalTable;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;

import java.sql.ResultSet;


@Command(
	name="proposals",
	description = "Fetch Course proposals from database",
	mixinStandardHelpOptions = true
)
public class fetchProposals extends Cmd{

	public fetchProposals(){
		super(user);
	}

	@Override
	public Cmd clone() {
		return null;
	}

	@Override
	public Integer call() throws Exception {

		Course p = new Course();

		try{
			ProposalTable pt = new ProposalTable();
			ResultSet res = pt.query(user, p);

			int k = 1;
			Login.out.println("\t(course_id, course_type, name, credits, ltp, dept)\n");
			while(res.next()){
				p = p.parse(res);
				Login.out.printf(
					"%d. ('%s', '%s', '%s', %d, '%s', '%s')\n",
					k++, p.course_id, p.course_type,
					p.name, p.credits, p.ltp, p.dept
				);
			}
			Login.out.println();

			return 0;
		}catch(Exception e){
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
