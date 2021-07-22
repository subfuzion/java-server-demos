package helloworld;

public class HelloService {

    String createGreeting() {
        return createGreeting("World");
    }

    String createGreeting(String name) {
        return "Hello, " + name + "!";
    }
}
