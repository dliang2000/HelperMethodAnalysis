package ca.uwaterloo.liang;

public class MethodHelper {
	private int hold;
	public MethodHelper(int hold) {
	    this.hold = hold;
	}
	
	public void setHold(int hold) {
		this.hold = hold;
	}
	
	public int getHold() {
		return this.hold;
	}
	public static boolean isValidInteger(MethodHelper mh, int low, int high) { 
		return (mh.getHold() >= low && mh.getHold() <= high); 
	}

	public void testFromSelf() {
		MethodHelper.isValidInteger(new MethodHelper(7), 1, 10);
	}

	@Override
	public boolean equals(Object o) {
		return true;
	}
}
