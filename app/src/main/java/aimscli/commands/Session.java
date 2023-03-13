package aimscli.commands;

import aimscli.commands.Base.Cmd;
import aimscli.pgManager.pgManager;
import aimscli.pgManager.pgManager.User;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
	name = "session",
	description = "Start/end an Academic Session",
	mixinStandardHelpOptions = true
)
public class Session extends Cmd{

	User u;

	public Session(User user){
		super(user);
		u = user;
	}

	@Override
	public Cmd clone() {
		return new Session(u);
	}

	@Parameters(index = "0")
		String action;
	@Option(names={"-y", "--year"}, description = "Year for the session")
		String year;
	@Option(names={"-s", "--sem"}, description = "Sem for the session")
		String sem;
	@Option(names={"--next"}, description = "When action='start' it starts the next session\nWhen action='end' it closes the current session and starts the next")
		boolean next;

	@Override
	public Integer call() throws Exception {
		if(action.equals("start")){
			if(!next){
				if(year == null || !validBatch(year)) {
					year = fetchLine("Year: ", "Invalid Year", this::validBatch);
				}
				if(sem == null || !validSem(sem)) {
					sem = fetchLine("Semester: ", "Invalid Semester", this::validSem);
				}
			}else{
				year = (Integer.parseInt(pgManager.currentYear()) + 1)+"";
				sem = ((pgManager.currentSem() == '1')?'2':'1')+"";
			}
			try{
				pgManager.startSession(user, year, sem.charAt(0));
				pgManager.commit();
				return 0;
			}catch(Exception e){
				pgManager.rollback();
				Login.err.println(ansi.Err(e.getMessage()));
				return 1;
			}
		}
		else if(action.equals("end")){
			try{
				pgManager.endSession(user);
				pgManager.commit();
				return 0;
			}catch(Exception e){
				pgManager.rollback();
				Login.err.println(ansi.Err(e.getMessage()));
				return 1;
			}
		}else{
			Login.err.println(ansi.Err("Unrecognized Parameter Value '"+action+"'\n  Try using start/stop"));
			return 1;
		}
	}
	
}
