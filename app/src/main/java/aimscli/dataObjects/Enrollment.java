package aimscli.dataObjects;

import java.sql.ResultSet;

public class Enrollment implements DataObject{

	public String student_id, course_id, grade;
	public Integer session_id;

	public Enrollment(){};

	private int getCnt() {
		int cnt = 0;

		if(student_id != null)	cnt++;
		if(course_id != null)	cnt++;
		if(session_id != null)	cnt++;
		if(grade != null)	cnt++;

		return cnt;
	}

	@Override
	public String[] getAttr() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] attr = new String[cnt];

		int k = 0;
		if(student_id != null)	attr[k++] = "student_id";
		if(course_id != null)	attr[k++] = "course_id";
		if(session_id != null)	attr[k++] = "session_id";
		if(grade != null)	attr[k] = "grade";

		return attr;
	}

	@Override
	public String[] getVals() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] vals = new String[cnt];

		int k = 0;
		if(student_id != null)	vals[k++] = "'"+ student_id +"'";
		if(course_id != null)	vals[k++] = "'"+ course_id +"'";
		if(session_id != null)	vals[k++] = session_id + "";
		if(grade != null)	vals[k] = "'"+ grade +"'";

		return vals;
	}

	@Override
	public String[] primaryVals() {
		if(student_id == null || course_id == null || session_id == null)	return null;

		String[] pattr = new String[3];

		int k = 0;
		pattr[k++] = String.format("student_id='%s'", student_id);
		pattr[k++] = String.format("course_id='%s'", course_id);
		pattr[k++] = String.format("session_id='%s'", session_id);

		return pattr;
	}

	@Override
	public Enrollment parse(ResultSet res) throws Exception {
		Enrollment e = new Enrollment();

		e.student_id = res.getString("student_id");
		e.course_id = res.getString("course_id");
		e.session_id = res.getInt("session_id");
		e.grade = res.getString("grade");

		return e;
	}

	@Override
	public boolean isValid() {
		if(course_id == null || student_id == null || session_id == null)	return false;
		return true;
	}
	
}
