import org.mindrot.jbcrypt.BCrypt;
public class CheckPw {
  public static void main(String[] args) {
    String hash = "$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS";
    String[] guesses = {"123456","password","pass","qwerty","admin123","teachsync","123123","000000","111111","root","admin","1234","12345","qwerty123","password123"};
    for (String g : guesses) {
      System.out.println(g + "=" + BCrypt.checkpw(g, hash));
    }
  }
}
