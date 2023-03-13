package aimscli.commands.Fetch;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Course;
import aimscli.pgManager.CourseTable;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;

import java.sql.ResultSet;

@Command(
	name = "catalog",
	description = "Fetch the courses from the catalog",
	mixinStandardHelpOptions = true
)
public class fetchCatalog extends SubCmd{

	@Override
	public Integer call() throws Exception {
		try{
			CourseTable ct = new CourseTable();
			ResultSet res = ct.query(user, null);

			int k = 1;
			Course c = new Course();
			Login.out.println("\tcourse_id, name, credits, ltp");
			while(res.next()){
				c = c.parse(res);
				Login.out.printf(
					"%d. ('%s', '%s', %d, '%s')\n",
					k++, c.course_id, c.name, c.credits, c.ltp
				);
			}
		}catch(Exception e){
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}

		return 0;
	}
	
}
