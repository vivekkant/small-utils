package org.weekendsoft.smallutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.weekendsoft.smallhacks.RTOFinder;

class RTOFinderTest {
	
	@Test
	void test() {
		
		String rto = RTOFinder.findRTO("MH12MR5930");
		assertEquals("MH12", rto);
		
	}
	
	@Test
	void test2() {
		
		String rto = RTOFinder.findRTO("DL3CAB3465");
		assertEquals("DL3", rto);
		
	}

	@Test
	void test3() {
		
		String rto = RTOFinder.findRTO("DL3SBG7162");
		assertEquals("DL3", rto);
		
	}

	@Test
	void test4() {
		
		String rto = RTOFinder.findRTO("DL3SBG7162");
		assertEquals("DL3", rto);
		
	}
	
	@Test
	void test5() {
		
		String rto = RTOFinder.findRTO("MH126758");
		assertEquals("MH12", rto);
		
	}


}
