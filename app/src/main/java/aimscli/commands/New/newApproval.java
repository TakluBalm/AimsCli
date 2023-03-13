package aimscli.commands.New;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Course;
import aimscli.pgManager.CourseTable;
import aimscli.pgManager.ProposalTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.sql.ResultSet;

@Command(
	name = "approval",
	description = "Approve an existing Course proposal",
	mixinStandardHelpOptions = true
)
public class newApproval extends SubCmd{

	@Parameters(index = "0")	String course_id;

	@Override
	public Integer call() throws Exception {
		Course p = new Course();

		if(!validUid(course_id)){
			course_id = fetchLine("Course ID: ", "Invalid Input", this::validUid);
		}
		p.course_id = course_id;

		try{
			ProposalTable pt = new ProposalTable();
			CourseTable ct = new CourseTable();

			Course row = new Course();
			boolean insert = true;
			ResultSet res = ct.query(user, p);
			if(res.next()){
				insert = false;
				row = row.parse(res);
			}

			res = pt.query(user, p);
			if(!res.next()){
				res.close();
				throw new Exception("No proposal with supplied values");
			}
			p = p.parse(res);

			pt.delete(user, p);			
			if(insert)	ct.insert(user, p);
			else		ct.update(user, row, p);

			pgManager.commit();

			return 0;
		}catch (Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
