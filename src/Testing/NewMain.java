package Testing;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewMain {

    public static void main(String[] args) {
        SimpleDateFormat m = new SimpleDateFormat("HH:mm");
        Date Date1 = new Date();
        Date Date2 = new Date();
        Date2.setMinutes(Date2.getMinutes() + 10);

        System.out.println(m.format(Date1));
        System.out.println(m.format(Date2));

    }
}
