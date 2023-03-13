package aimscli.pgManager;

import aimscli.dataObjects.Course;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;

public class CourseTable extends Table{

	public CourseTable(){
		super("courses");
	}

	@Override
	public boolean insertPrivilege(User u) {
		if(u.privilege != Privilege.ADM)	return false;
		return true;
	}

	@Override
	public boolean updatePrivilege(User u) {
		return insertPrivilege(u);
	}

	@Override
	public boolean queryPrivilege(User u) {
		return true;
	}

	@Override
	public boolean deletePrivilege(User u) {
		return insertPrivilege(u);
	}
	
	public void insert(User u, Course c) throws Exception{
		super.insert(u, c);
	}

	public void update(User u, Course old, Course updated) throws Exception{
		super.update(u, old, updated);
	}

	public ResultSet query(User u, Course reference) throws Exception{
		return super.query(u, reference);
	}
}
