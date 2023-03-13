package aimscli.commands;

import aimscli.commands.Base.Cmd;
import aimscli.commands.Curriculum.CurriculumCmd;
import aimscli.commands.Fetch.Fetch;
import aimscli.commands.New.New;
import aimscli.commands.Update.Update;
import aimscli.pgManager.pgManager;
import aimscli.pgManager.pgManager.User;
import aimscli.viewManager.ansi;
import aimscli.viewManager.commandPrompt;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.Console;
import java.io.InputStream;
import java.io.PrintStream;


@Command(name="app", mixinStandardHelpOptions = true)
public class Login extends Cmd {

	public Login(User u) {
		super(u);
	}


	public static Console cons = System.console();
	public static PrintStream out = System.out;
	public static PrintStream err = System.out;
	public static InputStream in = System.in;
	
	String passwd;

	@Option(names = { "-u", "--user" })
	String username;

	@Option(names = {"-g", "--first-run"})
	boolean first_run;

	User user;

	public Login clone(){
		return new Login(user);
	}

	private commandPrompt initPrompt(){
		commandPrompt p = new commandPrompt(user);
		p.addCommand("main", this);
		p.addCommand("new", new New(user));
		p.addCommand("fetch", new Fetch(user));
		p.addCommand("update", new Update(user));
		p.addCommand("session", new Session(user));
		p.addCommand("curriculum", new CurriculumCmd(user));
		p.addCommand("upload-grades", new UploadGrades(user));
		return p;
	}

	@Override
	public Integer call() throws Exception{

		pgManager.init(first_run);

		if (username == null || !validUid(username)) {
			out.printf("Username: ");
			username = cons.readLine().strip().split(" ")[0];
		}

		//	Resolve the user
		out.printf("Password: ");
		passwd = new String(cons.readPassword());
		user = pgManager.authenticate(username, passwd);
		if(user  == null){
			Login.err.println(ansi.Err("Failed to authenticate\n"));
			return 1;
		}

		commandPrompt p = initPrompt();
		p.start();
		return 0;
	}
}
