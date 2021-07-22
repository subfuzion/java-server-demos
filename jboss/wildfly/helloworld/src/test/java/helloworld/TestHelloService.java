package helloworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_METHOD)
class TestHelloService {
   private HelloService h = new HelloService();
	
   @Test
   public void testHelloDefault() {
      assertEquals(h.createGreeting(), "Hello, World!");
   }
	
   @Test
   public void testHelloWorld() {
      assertEquals(h.createGreeting("Java"), "Hello, Java!");
   }
}