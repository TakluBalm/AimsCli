package aimscli.commands.Base;

import aimscli.commands.Login;
import aimscli.dataObjects.Course;
import aimscli.dataObjects.Enrollment;
import aimscli.pgManager.CourseTable;
import aimscli.pgManager.EnrollmentTable;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Function;

public abstract class Cmd implements Callable<Integer>{

	public static User user;

	public Cmd(User u){
		user = u;
	}
	
	@Override
	public abstract Cmd clone();

	@Override
	abstract public Integer call() throws Exception;

	public void prompt(String p){
		Login.out.print(p);
	}

	public float CGPA(String student_id) throws Exception{

		ArrayList<Enrollment> arr = new ArrayList<Enrollment>();
		Enrollment ref = new Enrollment();
		ref.student_id = student_id;
		EnrollmentTable et = new EnrollmentTable();
		ResultSet r = et.query(user, ref);

		int score = 0, credits = 0;

		while(r.next()){
			ref = ref.parse(r);
			if(ref.grade == null)	continue;
			arr.add(ref);
		}
		r.close();

		if(arr.size() == 0){
			return 0;
		}

		CourseTable ct = new CourseTable();
		Course c = new Course();
		for(int i = 0; i < arr.size(); i++){
			ref = arr.get(i);
			c.course_id = ref.course_id;
			r = ct.query(user, c);
			r.next();
			c = c.parse(r);
			score += c.credits * Grade2Int(ref.grade);
			credits += c.credits;
		}

		return (score/credits);
	}

	int Grade2Int(String grade){
		if(grade.equals("A"))	return 10;
		if(grade.equals("A-"))	return 9;
		if(grade.equals("B"))	return 8;
		if(grade.equals("B-"))	return 7;
		if(grade.equals("C"))	return 6;
		if(grade.equals("C-"))	return 5;
		if(grade.equals("D"))	return 4;
		if(grade.equals("E"))	return 2;
		if(grade.equals("F"))	return 0;
		return -1;
	}

	public String fetchMultiLine(String prompt){
		String ml = "";
		if(prompt != null){
			prompt(prompt);
			Login.out.println();
		}
		while(true){
			Login.out.print('\t');
			String temp = Login.cons.readLine().strip();
			if(temp.length() == 0)	break;
			ml += temp + '\n';
		}
		if(ml.equals(""))	return null;
		ml.strip();
		return ml;
	}

	public String fetchLine(String p, String errmsg, Function<String, Boolean> check){
		String in;
		while(true){
			if(p != null)	prompt(p);

			in = Login.cons.readLine().strip();
			if(check.apply(in))	break;

			if(errmsg != null)	Login.err.println(errmsg);
		}
		return in;
	}

	public String fetchLine(String p){
		String in;
		if(p != null)	prompt(p);

		in = Login.cons.readLine().strip();

		return in;
	}

	public String[] extractPrereq(String[] in){
		int cnt = in.length;
		for(int i = 0; i < in.length; i++){
			in[i] = in[i].strip();
			if(in[i].equals("")){
				cnt--;
			}
		}
		if(cnt == 0)	return null;

		String[] out = new String[cnt];
		cnt = 0;
		for(int i = 0; i < in.length; i++){
			if(!in[i].equals("")){
				out[i] = in[i];
			}
		}
		return out;
	}

	public boolean validLTP(String in){
		int i = 0;
		for(int j = 0; j < 2; j++){
			while(i < in.length()){
				char c = in.charAt(i);
				if(c <= '9' && c >= '0'){
					i++;
					continue;
				}
				break;
			}
			if(i == in.length() || in.charAt(i) != '-')	return false;
			i++;
		}
		while(i < in.length()){
			char c = in.charAt(i);
			if(c <= '9' && c >= '0'){
				i++;
				continue;
			}
			return false;
		}
		return true;
	}

	public boolean validUid(String u){
		for(int i = 0; i < u.length(); i++){
			char c = u.charAt(i);
			if(c <= 'z' && c >= 'a')	continue;
			if(c <= 'Z' && c >= 'A')	continue;
			if(c <= '9' && c >= '0')	continue;
			if(c == '_')	continue;
			return false;
		}
		return true;
	}

	public boolean validDept(String in){
		for(int i = 0; i < in.length(); i++){
			char c = in.charAt(i);
			if(c <= 'z' && c >= 'a')	continue;
			if(c <= 'Z' && c >= 'A')	continue;
			if(c == ' ')	continue;
			return false;
		}
		return true;
	}

	public boolean validBatch(String in){
		for(int i = 0; i < in.length(); i++){
			char c = in.charAt(i);
			if(c > '9' || c < '0')	return false;
		}
		return true;
	}

	public boolean validName(String in){
		for(int i = 0; i < in.length(); i++){
			char c = in.charAt(i);
			if(c <= 'z' && c >= 'a')	continue;
			if(c <= 'Z' && c >= 'A')	continue;
			if(c == ' ')				continue;
			return false;
		}
		return true;
	}

	public boolean validName(String[] in){
		if(in.length == 0)	return false;
		for(int i = 0; i < in.length; i++){
			if(!validName(in[i]))	return false;
		}
		return true;
	}

	public boolean validNum(String in){
		for(int i = 0; i < in.length(); i++){
			char c = in.charAt(i);
			if(c > '9' || c < '0')	return false;
		}
		return true;
	}

	public boolean validSem(String in){
		if(in.length() != 1)	return false;
		char c = in.charAt(0);
		if(c != '1' && c != '2')	return false;
		return true;
	}
	
	public boolean validArray(String in){
		for(int i = 0; i < in.length(); i++){
			char c = in.charAt(i);
			if(c <= 'z' && c >= 'a')	continue;
			if(c <= 'Z' && c >= 'A')	continue;
			if(c <= '9' && c >= '0')	continue;
			if(c == ' ' || c == ',')	continue;
			return false;
		}
		return true;
	}

	public boolean validGrade(String in){
		if(in.length() == 1){
			char c = in.charAt(0);
			if(c < 'A' || c > 'F')	return false;
			return true;
		}else if(in.length() == 2){
			if(in.charAt(1) != '-')	return false;
			if(in.charAt(0) > 'C' || in.charAt(0) < 'A')	return false;
			return true;			
		}
		return false;
	}

	public boolean validCType(String in){
		if(in.equals("PC") || in.equals("PE") || in.equals("CP"))	return true;
		return false;
	}

}
