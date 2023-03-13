package aimscli.commands.Fetch;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Offering;
import aimscli.pgManager.OfferingTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.sql.ResultSet;

@Command(
	name = "offerings",
	description = "Fetch the Offered courses for the semester",
	mixinStandardHelpOptions = true
)
public class fetchOfferings extends SubCmd{

	public fetchOfferings(){}

	@Option(names={"--instructor"}, description = "Fetch Offered courses taught by instructor")
		String instructor_id;
	@Option(names={"--course"}, description = "Fetch Course with this ID")
		String course_id;
	@Option(names={"--session"}, description = "Session to fetch from (defualt: current session)", arity = "2")
		String[] session;
	@Option(names={"-a"}, description = "Search in all the sessions (Superseded by --session)")
		boolean all;

	@Override
	public Integer call() throws Exception{
		try{
			Offering of = new Offering();
			if(instructor_id != null){
				if(validUid(instructor_id))
					of.instructor_id = instructor_id;
				else
					Login.err.println(ansi.Err("Invalid instructor ID. Proceding without it"));
			}
			if(course_id != null){
				if(validUid(course_id))
					of.course_id = course_id;
				else
					Login.err.println(ansi.Err("Invalid course ID. Proceding without it"));
			}
			if(session != null){
				if(validBatch(session[0]) && validSem(session[1])){
					Integer temp = pgManager.getSessionID(session[0], session[1]);
					if(temp == null){
						Login.err.println(ansi.Err("Invalid Year/Sem pair. Proceding without it"));
					}else{
						of.session_id = temp;
					}
				}
			}else if(!all){
				of.session_id = pgManager.currentSession();
			}

			OfferingTable ot = new OfferingTable();
			
			ResultSet res = ot.query(user, of);
			int k = 1;
			Login.out.println("0. (course_id, instructor_id, minCG, completed)");
			while(res.next()){
				of = of.parse(res);
				Login.out.printf(
					"%d. (%s, %s, %f, %s)",
					k++, of.course_id, of.instructor_id,
					of.minCG, of.completed
				);
			}
			res.close();

			return 0;
		}catch(Exception e){
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
