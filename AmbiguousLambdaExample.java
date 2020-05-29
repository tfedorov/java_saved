package java_save
public class AmbiguousLambdaExample {

    static class ClassExample {

        public final String field;

        ClassExample(String field) {
            this.field = field;
        }

        static String staticMethod2(ClassExample param1, ClassExample param2) {
            return "[" + param1.field + "] and [" + param2.field + "] are in staticMethod2.";
        }

        String instanceMethod1(ClassExample param1) {
            return "[" + field + "] in instanceMethod1. But i also have a param [" + param1.field + "].";
        }

        String problemMethod(ClassExample param1) {
            return "[" + field + "] in problemMethod {INSTANCE}. But i also have a param [" + param1.field + "].";
        }

        static String problemMethod(ClassExample param1, ClassExample param2) {
            return "[" + param1.field + "] and [" + param2.field + "] are in problemMethod{STATIC}.";
        }

    }

    public interface Inter {
        String interfaceMethod(ClassExample param1, ClassExample param2);
    }

    public static void main(String[] args) {

        //Inter interfaceWithStatic = (param1, param2) -> ClassExample.staticMethod(param1, param2);
        Inter interfaceWithStatic = ClassExample::staticMethod2;
        //Inter interfaceWithInst = (param1, param2) -> param1.instanceMethod(param2);
        Inter interfaceWithInst = ClassExample::instanceMethod1;

        ClassExample obj1 = new ClassExample("First obj");
        ClassExample obj2 = new ClassExample("Second obj");

        System.out.println(interfaceWithStatic.interfaceMethod(obj1, obj2));
        System.out.println(interfaceWithInst.interfaceMethod(obj1, obj2));

        Inter interWithStaticProblem = (param1, createdTwo) -> ClassExample.problemMethod(param1, createdTwo);
        Inter interWithInstanceProblem = (param1, createdTwo) -> param1.problemMethod(createdTwo);

        System.out.println(interWithStaticProblem.interfaceMethod(obj1, obj2));
        System.out.println(interWithInstanceProblem.interfaceMethod(obj1, obj2));

        //Inter interProblem = ClassExample::problemMethod;
        // Could not deside which
    }
}
