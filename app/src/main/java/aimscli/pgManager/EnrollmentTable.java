package aimscli.pgManager;

import aimscli.dataObjects.Enrollment;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;

public class EnrollmentTable extends Table{

	public EnrollmentTable(){
		super("enrollment");
	}

	@Override
	public boolean insertPrivilege(User u) {
		if(u.privilege == Privilege.FAC)	return false;
		return true;
	}

	@Override
	public boolean updatePrivilege(User u) {
		if(u.privilege == Privilege.STU)	return false;
		return true;
	}

	@Override
	public boolean queryPrivilege(User u) {
		return true;
	}

	@Override
	public boolean deletePrivilege(User u) {
		return insertPrivilege(u);
	}
	
	public void insert(User u, Enrollment e) throws Exception{
		if(u.privilege == Privilege.STU){
			e.student_id = u.user_id;
		}
		super.insert(u, e);
	}

	public void update(User u, Enrollment old, Enrollment updates) throws Exception{
		if(u.privilege == Privilege.FAC){
			updates.student_id = updates.course_id = null;
			updates.session_id = null;
		}
		if(u.privilege == Privilege.STU){
			old.student_id = u.user_id;
			updates.grade = null;
		}
		super.update(u, old, updates);
	}
	
	public ResultSet query(User u, Enrollment reference) throws Exception{
		if(u.privilege == Privilege.STU){
			reference.student_id = u.user_id;
		}
		return super.query(u, reference);
	}

	public Integer queryCount(User u, Enrollment reference) throws Exception{
		String prev = select;
		select = "count(*)";
		ResultSet r =  query(u, reference);
		if(!r.next()){
			throw new Exception("Error occured");
		}
		int cnt = r.getInt(1);
		select = prev;
		return cnt;
	}

	public boolean delete(User u, Enrollment ref) throws Exception{
		return super.delete(u, ref);
	}

	public ResultSet dynamicQuery(User u, String where) throws Exception{
		if(u.privilege == Privilege.STU){
			where = String.format("(%s) and student_id='%s'", where, u.user_id);
		}
		return super.dynamicQuery(u, where);
	}
}
