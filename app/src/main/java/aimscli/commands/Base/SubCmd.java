package aimscli.commands.Base;

public abstract class SubCmd extends Cmd{
	public SubCmd(){
		super(user);
	}

	@Override
	public Cmd clone(){
		return null;
	}
}
