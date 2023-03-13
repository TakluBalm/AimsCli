package aimscli.commands.New;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Course;
import aimscli.dataObjects.Credits;
import aimscli.dataObjects.Enrollment;
import aimscli.dataObjects.Offering;
import aimscli.pgManager.*;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.sql.ResultSet;

@Command(
	name = "enrollment",
	description = "Enroll to a course",
	mixinStandardHelpOptions = true
)
public class newEnrollment extends SubCmd{

	public newEnrollment(){}

	@Parameters(index = "0")
		String course_id;
	@Option(names={"-s", "--stu"}, description = "Provide student ID (Ignored if Student)")
		String student_id;
	@Option(names={"--remove"}, description = "Remove the student if enrolled in the course")
		boolean remove;

	@Override
	public Integer call() throws Exception {
		try{
			if(user.privilege != Privilege.STU && (student_id == null || !validUid(student_id))){
				student_id = fetchLine("Student ID: ", "Invalid ID", this::validUid);
			}

			Offering of = isOffered();

			CourseTable ct = new CourseTable();
			CreditsTable crt = new CreditsTable();

			Course c = new Course();
			Credits cr = new Credits();

			c.course_id = course_id;
			cr.student_id = student_id;

			ResultSet res = ct.query(user, c);
			res.next();
			c = c.parse(res);
			res.close();

			res = crt.query(user, cr);
			res.next();
			cr = cr.parse(res);
			res.close();

			enoughCreds(c, cr);

			float cg = CGPA(student_id);
			if(cg < of.minCG){
				throw new Exception("Not enough credits to apply. Require atleast " + of.minCG);
			}

			prereq(c);

			EnrollmentTable et = new EnrollmentTable();
			Enrollment e = new Enrollment();
			e.course_id = course_id;
			e.session_id = pgManager.currentSession();
			e.student_id = student_id;
			
			if(!remove){
				et.insert(user, e);

				Credits updates = new Credits();
				updates.current = cr.current + c.credits;
				updates.total = cr.total + c.credits;
				if(c.course_type.equals("PC")){
					updates.pc = cr.pc + c.credits;
				}
				if(c.course_type.equals("PE")){
					updates.pe = cr.pe + c.credits;
				}
				if(c.course_type.equals("CP")){
					updates.cp = cr.cp + c.credits;
				}
				crt.update(user, cr, updates);
			}else{
				if(et.delete(user, e)){
					Credits updates = new Credits();
					updates.current = cr.current - c.credits;
					updates.total = cr.total - c.credits;
					if(c.course_type.equals("PC")){
						updates.pc = cr.pc - c.credits;
					}
					if(c.course_type.equals("PE")){
						updates.pe = cr.pe - c.credits;
					}
					if(c.course_type.equals("CP")){
						updates.cp = cr.cp - c.credits;
					}
					crt.update(user, cr, updates);
				}
			}

			pgManager.commit();

			return 0;
		}catch(Exception ex){
			pgManager.rollback();
			Login.err.println(ansi.Err(ex.getMessage()));
			return 1;
		}
	}
	
	Offering isOffered() throws Exception{
		OfferingTable ot = new OfferingTable();
		Offering o = new Offering();

		o.course_id = course_id;
		o.session_id = pgManager.currentSession();

		ResultSet res = ot.query(user, o);


		if(!res.next()){
			res.close();
			throw new Exception("Course has not been offered");
		}
		o = o.parse(res);
		return o;
	}

	void enoughCreds(Course c, Credits cr) throws Exception{
		if(((cr.prev_credits[0] + cr.prev_credits[1])*0.625 - cr.current) < (float)c.credits){
			throw new Exception("Not enough credits");
		}
	}

	void prereq(Course c) throws Exception{
		EnrollmentTable et = new EnrollmentTable();

		if(c.prereq == null)	return;

		
		for(int i = 0; i < c.prereq.length; i++){
			Enrollment itr = new Enrollment();
			itr.student_id = student_id;
			itr.course_id = c.prereq[i];
			ResultSet res = et.query(user, itr);
			if(res.next()){
				itr = itr.parse(res);
				res.close();

				if(itr.session_id < pgManager.currentSession())	continue;
			}
			throw new Exception("Unfulfilled prerequisites");
		}
	}
}
