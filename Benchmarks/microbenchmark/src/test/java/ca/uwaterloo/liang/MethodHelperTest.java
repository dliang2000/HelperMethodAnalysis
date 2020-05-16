package ca.uwaterloo.liang;

import static org.junit.Assert.*;

import org.junit.Test;

public class MethodHelperTest {
	MethodHelper mh = new MethodHelper(7);
	
	@Test
	public void first() {
		assertTrue(MethodHelper.isValidInteger(mh, 1, 10));
		mh.equals(mh);
		mh.equals(mh);
	}
	
	@Test
	public void test2() {
		assertFalse(MethodHelper.isValidInteger(mh, 8, 10));
		mh.equals(mh);
	}

}
