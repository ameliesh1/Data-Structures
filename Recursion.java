/**
 * @author Amelie Sharples aes2367
 * Programming Assignment 2 - Recursion exercises
 * COMS W3134
 *
 * Note: All methods must be written recursively. No credit will be given for
 * methods written without recursion, even if they produce the correct output.
 */
public class Recursion {

    public static void main(String[] args) {
        System.out.println(compare(1));
        System.out.println(compareNext(2));
        System.out.println(largestN(200000, 3600000));
    }

    public static double compare(int n) {
        double x = 4*Math.pow(n, 3);
        double y = 64*n*(Math.log(n)/Math.log(2));
        while(x >= y) {
            return compare(n+1);
        }
        return n;
    }

    public static double compareNext(int n) {
        double x = 4*Math.pow(n, 3);
        double y = 64*n*(Math.log(n)/Math.log(2));
        while(y > x) {
            return compareNext(n+1);
        }
        return n-1;
    }


    public static double largestN(int n, double max) {
        double testN = n*(Math.log(n)/Math.log(2));
        while(testN < max) {
            return largestN(n+1, max);
        }
        return n-1;
    }
    /**
     * Returns the value of x * y, computed via recursive addition.
     * x is added y times.
     *
     * @param x integer multiplicand 1
     * @param y integer multiplicand 2
     * @return x * y
     */
    public static int recursiveMultiplication(int x, int y) {
        if (x == 0 || y == 0) {
            return 0;
        }
        return x + recursiveMultiplication(x, y - 1);

    }

/******************************************************************************/
    /**
     * Reverses a string via recursion.
     *
     * @param s the non-null string to reverse
     * @return a new string with the characters in reverse order
     */
    public static String reverse(String s) {
        if (s.length() == 0) {
            return s;
        }
        return reverse(s.substring(1)) + s.charAt(0);
    }

    /******************************************************************************/
    private static int maxHelper(int[] array, int index, int max) {
        if(array.length<=1) {
            return array[0];
        }
        if(index < array.length-1) {
            max = Math.max(array[index], maxHelper(array, index+1, max));
            return maxHelper(array, index+1, max);
        }
        return max;
    }

    /**
     * Returns the maximum value in the array.
     * Uses a helper method to do the recursion.
     *
     * @param array the array of integers to traverse
     * @return the maximum value in the array
     */
    public static int max(int[] array) {
        return maxHelper(array, 0, Integer.MIN_VALUE);
    }

/******************************************************************************/

    /**
     * Returns whether or not a string is a palindrome, a string that is
     * the same both forward and backward.
     *
     * @param s the string to process
     * @return a boolean indicating if the string is a palindrome
     */
    public static boolean isPalindrome(String s) {
        if (s.length() <= 1) {
            return true;
        }
        int n = s.length() - 1;
        if (s.charAt(0) != s.charAt(n)) {
            return false;
        } else {
            return isPalindrome(s.substring(1, n));
        }
    }

    /******************************************************************************/
    private static boolean memberHelper(int key, int[] array, int index) {
        if(array.length == 0) {
            return false;
        }
        if(index < array.length) {
            if(key == array[index]) {
                return true;
            }
            return memberHelper(key, array, index+1);
        }
        return false;
    }

    /**
     * Returns whether or not the integer key is in the array of integers.
     * Uses a helper method to do the recursion.
     *
     * @param key   the value to seek
     * @param array the array to traverse
     * @return a boolean indicating if the key is found in the array
     */
    public static boolean isMember(int key, int[] array) {
        return memberHelper(key, array, 0);
    }

/******************************************************************************/
    /**
     * Returns a new string where identical chars that are adjacent
     * in the original string are separated from each other by a tilde '~'.
     *
     * @param s the string to process
     * @return a new string where identical adjacent characters are separated
     * by a tilde
     */
    public static String separateIdentical(String s) {
        if (s.length() <= 1) {
            return s;
        }
        char c = s.charAt(0);

        if (s.charAt(0) != s.charAt(1)) {
            return c + separateIdentical(s.substring(1));
        }
        return c + "~" + separateIdentical(s.substring(1));
    }
}