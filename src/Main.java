import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Date data = new Date();
        SimpleDateFormat newData = new SimpleDateFormat("hh:mm:ss");
        System.out.println(newData.format(data));
    }
}
