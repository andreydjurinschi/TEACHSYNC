import org.mindrot.jbcrypt.BCrypt;
public class VerifyNew {
  public static void main(String[] args) {
    String hash = args[0];
    System.out.println(BCrypt.checkpw("secret123", hash));
  }
}
