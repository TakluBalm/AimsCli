package aimscli.viewManager;

public class ansi {
	final private static String BLK = (char)27 + "[30m";
	final private static String RED = (char)27 + "[31m";
	final private static String GRN = (char)27 + "[32m";
	final private static String YEL = (char)27 + "[33m";
	final private static String BLU = (char)27 + "[34m";
	final private static String MGT = (char)27 + "[35m";
	final private static String CYN = (char)27 + "[36m";
	final private static String WHT = (char)27 + "[37m";
	final private static String RST = (char)27 + "[0m";

	final private static String ITL = (char)27 + "[3m";
	final private static String ITL_RST = (char)27 + "[23m";
	final private static String BLD = (char)27 + "[1m";
	final private static String BLD_RST = (char)27 + "[22m";

	public static String Black(Object txt){
		return BLK + txt + RST;
	}

	public static String Red(Object txt){
		return RED + txt + RST;
	}

	public static String Green(Object txt){
		return GRN + txt + RST;
	}

	public static String Yellow(Object txt){
		return YEL + txt + RST;
	}

	public static String Blue(Object txt){
		return BLU + txt + RST;
	}

	public static String Magenta(Object txt){
		return MGT + txt + RST;
	}

	public static String Cyan(Object txt){
		return CYN + txt + RST;
	}

	public static String White(Object txt){
		return WHT + txt + RST;
	}

	public static String Italic(Object txt){
		return ITL + txt + ITL_RST;
	}

	public static String Bold(Object txt){
		return BLD + txt + BLD_RST;
	}

	public static String Err(Object txt){
		return Bold(Italic(Red(txt)));
	}
}
