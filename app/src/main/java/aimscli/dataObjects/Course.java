package aimscli.dataObjects;

import java.sql.Array;
import java.sql.ResultSet;

public class Course implements DataObject{

	public String course_id, name, description, ltp, dept, course_type;
	public Integer credits;
	public String[] prereq;

	public Course(){}

	private int getCnt(){
		int cnt = 0;

		if(course_id != null)	cnt++;
		if(name != null)		cnt++;
		if(description != null)	cnt++;
		if(ltp != null)			cnt++;
		if(dept != null)		cnt++;
		if(credits != null)		cnt++;
		if(course_type != null)	cnt++;
		if(prereq != null && prereq.length != 0)	cnt++;

		return cnt;
	}

	@Override
	public String[] getAttr() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] attr = new String[cnt];

		int k = 0;
		if(course_id != null)	attr[k++] = "course_id";
		if(name != null)	attr[k++] = "name";
		if(ltp != null)	attr[k++] = "ltp";
		if(credits != null)	attr[k++] = "credits";
		if(dept != null)	attr[k++] = "dept";
		if(description != null)	attr[k++] = "description";
		if(course_type != null)	attr[k++] = "course_type";
		if(prereq != null && prereq.length != 0)		attr[k++] = "prereq";

		return attr;
	}

	@Override
	public String[] getVals() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] vals = new String[cnt];

		int k = 0;
		if(course_id != null)	vals[k++] = "'" + course_id + "'";
		if(name != null)	vals[k++] = "'" + name + "'";
		if(ltp != null)	vals[k++] = "'" + ltp + "'";
		if(credits != null)	vals[k++] = "'" + credits + "'";
		if(dept != null)	vals[k++] = "'" + dept + "'";
		if(description != null)	vals[k++] = "$$" + description + "$$";
		if(course_type != null)	vals[k++] = "'"+course_type+"'";
		if(prereq != null && prereq.length != 0){
			String list = "'" + prereq[0] + "'";
			for(int i = 1; i < prereq.length; i++){
				list += ", '" + prereq[1] + "'";
			}
			vals[k++] = String.format("ARRAY[%s]", list);
		}

		return vals;
	}

	@Override
	public String[] primaryVals() {
		if(course_id == null)	return null;

		String[] pattr = new String[1];

		pattr[0] = String.format("course_id='%s'", course_id);

		return pattr;
	}

	@Override
	public Course parse(ResultSet res) throws Exception {
		Course p = new Course();

		p.course_id = res.getString("course_id");
		p.name = res.getString("name");
		p.description = res.getString("description");
		p.ltp = res.getString("ltp");
		p.credits = res.getInt("credits");
		p.course_type = res.getString("course_type");
		p.dept = res.getString("dept");

		Array temp = res.getArray("prereq");
		if(temp != null)	p.prereq = (String[])(temp.getArray());
		
		return p;
	}

	@Override
	public boolean isValid() {
		if(course_id != null && name != null && ltp != null && credits != null && dept != null && course_type != null)	return true;
		return false;
	}

	public Course getCourse(){
		Course c = new Course();
		c.course_id = course_id;
		c.credits = credits;
		c.description = description;
		c.ltp = ltp;
		c.name = name;
		c.prereq = prereq;
		return c;
	}
}