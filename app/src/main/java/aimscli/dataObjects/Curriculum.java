package aimscli.dataObjects;

import java.sql.ResultSet;

public class Curriculum implements DataObject{

	public String batch, programme;
	public Integer pc, pe, cp;

	private int getCnt(){
		int cnt = 0;
		if(batch != null)	cnt++;
		if(programme != null)	cnt++;
		if(pc != null)		cnt++;
		if(pe != null)		cnt++;
		if(cp != null)		cnt++;
		return cnt;
	}

	@Override
	public String[] getAttr() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] attr = new String[cnt];

		int k = 0;
		if(batch != null)		attr[k++] = "batch";
		if(programme != null)	attr[k++] = "programme";
		if(pc != null)			attr[k++] = "pc";
		if(pe != null)			attr[k++] = "pe";
		if(cp != null)			attr[k++] = "cp";

		return attr;
	}

	@Override
	public String[] getVals() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] attr = new String[cnt];

		int k = 0;
		if(batch != null)		attr[k++] = "'" + batch +"'";
		if(programme != null)	attr[k++] = "'"+programme+"'";
		if(pc != null)			attr[k++] = ""+pc;
		if(pe != null)			attr[k++] = ""+pe;
		if(cp != null)			attr[k++] = ""+cp;

		return attr;
	}

	@Override
	public String[] primaryVals() {
		if(batch == null || programme == null)	return null;

		String pattr[] = new String[2];
		pattr[0] = String.format("batch='%s'", batch);
		pattr[1] = String.format("programme='%s'", programme);

		return pattr;
	}

	@Override
	public Curriculum parse(ResultSet res) throws Exception {
		Curriculum c = new Curriculum();

		c.batch = res.getString("batch");
		c.programme = res.getString("programme");
		c.pc = res.getInt("pc");
		c.pe = res.getInt("pe");
		c.cp = res.getInt("cp");

		return c;
	}

	@Override
	public boolean isValid() {
		if(batch != null && programme != null && pc != null && pe != null && cp != null)	return true;
		return false;
	}
	
}
