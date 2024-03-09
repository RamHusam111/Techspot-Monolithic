package object_orienters.techspot.exception;

public class PostUnrelatedToUserException extends Exception {
    public PostUnrelatedToUserException(String username, long postId){
        super("Post With ID: " + postId + " is Unrelated To User With Username:" + username);
    }
}