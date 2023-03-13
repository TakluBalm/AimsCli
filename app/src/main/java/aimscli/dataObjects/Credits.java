package aimscli.dataObjects;

import java.sql.ResultSet;

public class Credits implements DataObject{

	public String student_id;
	public Integer prev_credits[], current, total, pc, pe, cp;

	private int getCnt(){
		int cnt = 0;
		if(student_id != null)	cnt++;
		if(prev_credits != null && prev_credits.length != 0)	cnt++;
		if(current != null)		cnt++;
		if(total != null)		cnt++;
		if(pc != null)			cnt++;
		if(pe != null)			cnt++;
		if(cp != null)			cnt++;
		return cnt;
	}

	@Override
	public String[] getAttr() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String attr[] = new String[cnt];

		int k = 0;
		if(student_id != null)	attr[k++] = "student_id";
		if(current != null)	attr[k++] = "current";
		if(total != null)	attr[k++] = "total";
		if(pc != null)	attr[k++] = "pc";
		if(pe != null)	attr[k++] = "pe";
		if(cp != null)	attr[k++] = "cp";
		if(prev_credits != null && prev_credits.length != 0)	attr[k++] = "student_id";

		return attr;
	}

	@Override
	public String[] getVals() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String vals[] = new String[cnt];

		int k = 0;
		if(student_id != null)	vals[k++] = String.format("'%s'", student_id);
		if(current != null)	vals[k++] = String.format("'%s'", current);
		if(total != null)	vals[k++] = String.format("'%s'", total);
		if(pc != null)	vals[k++] = String.format("'%s'", pc);
		if(pe != null)	vals[k++] = String.format("'%s'", pe);
		if(cp != null)	vals[k++] = String.format("'%s'", cp);
		if(prev_credits != null && prev_credits.length != 0){
			String arr = prev_credits[0] + "";
			for(int i = 1; i < prev_credits.length; i++){
				arr += ", " + prev_credits[i];
			}
			vals[k++] = String.format("ARRAY[%s]", arr);
		}

		return vals;
	}

	@Override
	public String[] primaryVals() {
		if(student_id == null)	return null;

		String[] pattr = new String[1];
		pattr[0] = String.format("student_id='%s'", student_id);

		return pattr;
	}

	@Override
	public Credits parse(ResultSet res) throws Exception {
		Credits cr = new Credits();

		cr.student_id = res.getString("student_id");
		cr.prev_credits = (Integer[])(res.getArray("prev_credits").getArray());
		cr.current = res.getInt("current");
		cr.total = res.getInt("total");
		cr.pc = res.getInt("pc");
		cr.pe = res.getInt("pe");
		cr.cp = res.getInt("cp");

		return cr;
	}

	@Override
	public boolean isValid() {
		if(student_id != null)	return true;
		return false;
	}
	
}
