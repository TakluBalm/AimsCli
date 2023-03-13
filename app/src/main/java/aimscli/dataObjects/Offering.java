package aimscli.dataObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Offering implements DataObject{
	public String course_id, instructor_id;
	public Integer session_id;
	public Float minCG;
	public Boolean completed;

	public Offering(){}

	private int getCnt(){
		int cnt = 0;

		if(course_id != null)	cnt++;
		if(instructor_id != null)	cnt++;
		if(session_id != null)	cnt++;
		if(minCG != null)		cnt++;
		if(completed != null)	cnt++;

		return cnt;
	}

	@Override
	public String[] getAttr() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] vals = new String[cnt];

		int k = 0;
		if(course_id != null)		vals[k++] = "course_id";
		if(session_id != null)			vals[k++] = "session_id";
		if(instructor_id != null)	vals[k++] = "instructor_id";
		if(minCG != null)			vals[k++] = "minCG";
		if(completed != null)		vals[k++] = "completed";

		return vals;
	}

	@Override
	public Offering parse(ResultSet res) throws Exception {
		try{
			Offering o = new Offering();

			o.course_id = res.getString("course_id");
			o.instructor_id = res.getString("instructor_id");
			o.session_id = res.getInt("session_id");
			o.minCG = res.getFloat("minCG");
			o.completed = res.getBoolean("completed");

			return o;
		}catch (SQLException e){
			throw new Exception("Invalid type");
		}
	}

	@Override
	public boolean isValid() {
		if(course_id == null || session_id == null || instructor_id == null)	return false;
		return true;
	}

	@Override
	public String[] primaryVals() {
		if(course_id == null || session_id == null
		// || instructor_id != null
		)	return null;

		String[] pattr = new String[2];
		pattr[0] = String.format("course_id='%s'", course_id);
		pattr[1] = String.format("session_id=%d", session_id);
		// pattr[2] = String.format("instructor_id='%s'", instructor_id);

		return pattr;
	}

	@Override
	public String[] getVals() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] attr = new String[cnt];

		int k = 0;
		if(course_id != null)	attr[k++] = String.format("'%s'", course_id);
		if(session_id != null)	attr[k++] = String.format("%d", session_id);
		if(instructor_id != null)	attr[k++] = String.format("'%s'",instructor_id);
		if(minCG != null)		attr[k++] = String.format("%f", minCG);
		if(completed != null)	attr[k++] = ((completed)?"true":"false");

		return attr;
	}
}
