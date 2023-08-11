import org.testng.annotations.Test;

import fortestng.TestNg;

import org.testng.annotations.BeforeTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.AfterTest;

public class TestNG {
	
	
	 @BeforeTest
	  public void beforeTest() {
		  
	  }
	  
	 
  @Test
public void f() {
int a=20;
int b =30;
assertTrue(a!=b);	  
  }
  
  
  
  
  @Test(priority=1)
  public void fullname() {
String expected = "Sagar v";
String actual = "Sagar v";


assertEquals(expected,actual);	  
  }
  
  
  
  @Test
  public void null_() {
String expected = null;
assertNull(expected);  
  }

  @AfterTest
  public void afterTest() {

	  
  }

}
