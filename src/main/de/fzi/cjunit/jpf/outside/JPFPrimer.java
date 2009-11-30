package de.fzi.cjunit.jpf.outside;


public class JPFPrimer {

	public static class DoNothing {
		public static void main(String[] args) {
		}
	}

	protected static boolean primed = false;

	public static void prime() {
		if (!primed) {
			String[] args = new String[1];
			args[0] = DoNothing.class.getName();
			try {
				new JPFInvoker().run(args);
			} catch (Throwable t) {};
			primed = true;
		}
	}
}
