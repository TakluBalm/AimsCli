package aimscli.viewManager;

import aimscli.commands.Base.Cmd;
import aimscli.commands.Login;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;
import picocli.CommandLine;

import java.util.TreeMap;

public class commandPrompt {
	String greeting = "$ ";
	User user;
	TreeMap<String, Cmd> map = new TreeMap<>();

	public commandPrompt(User user2){
		if(user2 != null){
			user = user2;
			greeting = String.format("%s[%s@iitrpr]:$ ", user.user_id, role2prompt(user.privilege));
		}
	}

	private String role2prompt(Privilege r){
		switch(r){
			case STU:
				return "Student";
			case FAC:
				return "Faculty";
			case ADM:
				return "Acad";
			default:
				return "Guest";
		}
	}

	private int execute(String[] args){
		String command = args[0];
		if(!map.containsKey(command)){
			Login.err.printf("Invalid Command: %s\n", command);
			return 1;
		}
		String[] args2 = new String[args.length-1];
		for(int i = 0; i < args2.length; i++){
			args2[i] = args[i+1].replaceAll("\\\\", "");
		}
		CommandLine cmd = new CommandLine(map.get(command).clone());
		return cmd.execute(args2);
	}

	public void addCommand(String key, Cmd command){
		map.put(key, command);
	}

	public int start(){
		try{
			while(true){
				Login.out.print(greeting);
				String in = Login.cons.readLine();
				String[] args = in.split("(?<!\\\\)\\s+");
				if(args[0].equals("exit") || args[0].equals("quit"))	break;
				int code = execute(args);
				Login.err.println(ansi.Italic("\n\nExit code: "+ ((code == 0)?ansi.Green(code):ansi.Red(code))));
			}
			return 0;
		}catch(Exception e){
			Login.err.println(e);
			return 0;
		}
	}
}
