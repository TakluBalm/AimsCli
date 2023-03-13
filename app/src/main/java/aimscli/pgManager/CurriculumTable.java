package aimscli.pgManager;

import aimscli.dataObjects.Curriculum;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;

public class CurriculumTable extends Table{

	public CurriculumTable(){
		super("curriculum");
	}

	@Override
	public boolean insertPrivilege(User u) {
		if(u.privilege == Privilege.ADM)	return true;
		return false;
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

	public void insert(User u, Curriculum c) throws Exception{
		super.insert(u, c);
	}

	public void update(User u, Curriculum old, Curriculum updates) throws Exception{
		super.update(u, old, updates);
	}

	public ResultSet query(User u, Curriculum ref) throws Exception{
		return super.query(u, ref);
	}
	
}
