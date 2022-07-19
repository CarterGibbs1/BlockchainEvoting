import java.util.Scanner;
public class Text {
    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        String vote = "";
        System.out.println("Who is your (insert race) (1 for (name), 2 for (name), 3 for (name)):");
        vote += myObj.nextInt();
        System.out.println("Who is your (insert race) (1 for (name), 2 for (name), 3 for (name)):");
        vote += myObj.nextInt();
        System.out.println("Who is your (insert race) (1 for (name), 2 for (name), 3 for (name)):");
        vote += myObj.nextInt();
        System.out.println(vote);
    }
}