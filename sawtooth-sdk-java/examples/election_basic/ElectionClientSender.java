package election_basic;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

public class ElectionClientSender {

    public static void main(String[] args) throws UnirestException, IOException, ClassNotFoundException {

        ElectionImp electionService = new ElectionImp();

        switch (args[0]) {
            case "list":
                //electionService.list(args);
                break;
            case "vote":
                electionService.vote(args);
                break;
            case "create":
                electionService.create(args);
            case "spoil":
                //electionService.spoil(args);
            default:
                System.out.println("Invalid operation");
        }
    }
}