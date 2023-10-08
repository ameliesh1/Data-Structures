/**
 * Class containing methods that calculate the Greatest Common Divisor (GCD)
 * @author Amelie Sharples
 */

public class GCD {

    /**
     * Main method ensures that there are two inputs and that they are integers.
     * If inputs satisfy conditions to calculate GCD, method prints the GCD
     * of the two numbers provided using iterative and recursive methods.
     *
     * @param args array consisting of two strings that are numbers for GCD calc
     */

    public static void main(String[] args) {

        if(args.length != 2) {
            System.err.println("Usage: java GCD <integer m> <integer n>");
            System.exit(1);
        }

        int m = 0;
        int n = 0;

        try {
            m = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            System.err.println("Error: The first argument is not a valid integer.");
            System.exit(1);
        }

        try {
            n = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            System.err.println("Error: The second argument is not a valid integer.");
            System.exit(1);
        }

        if(m == 0 && n == 0) {
            System.err.println("gcd(0, 0) = undefined");
            System.exit(0);
        }

        int mAbs = Math.abs(m);
        int nAbs = Math.abs(n);

        System.out.println("Iterative: gcd(" + m + ", " + n + ") = "
                + iterativeGcd(mAbs, nAbs));
        System.out.println("Recursive: gcd(" + m + ", " + n + ") = "
                + recursiveGcd(mAbs, nAbs));
    }

    /**
     * Uses Euclid's method to calculate GCD in a while loop.
     *
     * @param m first number for GCD calc
     * @param n second number for GCD calc
     * @return GCD
     */

    public static int iterativeGcd(int m, int n) {
        if(m == 0) {
            return n;
        } else if(n==0) {
            return m;
        } else {
            int GCD = n;
            while(n!=0) {
                int rem = m%n;
                m = n;
                n = rem;
                GCD = m;
            }
            return GCD;
        }
    }

    /**
     * Uses Euclid's method to calculate GCD with recursion.
     *
     * @param m first number for GCD
     * @param n second number for GCD
     * @return GCD
     */
    public static int recursiveGcd(int m, int n) {
        if(m==0) {
            return n;
        } else if(n==0) {
            return m;
        } else {
            int rem = m%n;
            m = n;
            n = rem;
            int GCD = m;
            if (n == 0) {
                return GCD;
            }
            return recursiveGcd(m, n);
        }
    }
}
