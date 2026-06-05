package org.weekendsoft.smallutils;

import org.testng.Assert;
import org.testng.annotations.Test;

class RTOFinderTest {
	
	@Test
	void test() {
		
		String rto = RTOFinder.findRTO("MH12MR5930");
		Assert.assertEquals("MH12", rto);
		
	}
	
	@Test
	void test2() {
		
		String rto = RTOFinder.findRTO("DL3CAB3465");
		Assert.assertEquals("DL3", rto);
		
	}

	@Test
	void test3() {
		
		String rto = RTOFinder.findRTO("DL3SBG7162");
		Assert.assertEquals("DL3", rto);
		
	}

	@Test
	void test4() {
		
		String rto = RTOFinder.findRTO("DL3SBG7162");
		Assert.assertEquals("DL3", rto);
		
	}
	
	@Test
	void test5() {
		
		String rto = RTOFinder.findRTO("MH126758");
		Assert.assertEquals("MH12", rto);
		
	}


}
