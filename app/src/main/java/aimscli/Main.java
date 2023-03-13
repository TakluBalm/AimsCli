package aimscli;

import aimscli.commands.Login;
import picocli.CommandLine;


public class Main {
	public static void main(String[] args){
		int exit = new CommandLine(new Login(null)).execute(args);
		System.exit(exit);
	}	
}
