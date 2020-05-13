package ca.uwaterloo.liang;

import static org.junit.Assert.*;

import org.junit.Test;

public class MethodHelperTest {
	MethodHelper mh = new MethodHelper(7);
	
	@Test
	public void test1() {
		
		assertTrue(MethodHelper.isValidInteger(mh, 1, 10));
	}
	
	@Test
	public void test2() {
		assertFalse(MethodHelper.isValidInteger(mh, 8, 10));
	}

}
