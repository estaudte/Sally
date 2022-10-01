import com.shortn.sally.interfacing.ShellVerb;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class VerbTest extends ShellVerb {

    private boolean optSwitch;
    private String optText;
    private int optNum;

    public VerbTest() {
        super("This verb is a test of the shell processor");
        Map<String, Consumer<String>> temp = new HashMap<>();
        temp.put("switch", p -> setOptSwitch(!this.optSwitch));
        temp.put("text", this::setOptText);
        temp.put("num", p -> setOptNum(convertInt(p)));
        this.setOptionMap(temp);
    }

    @Override
    public String get() {
        System.out.println("Switch on? " + optSwitch + "\nText entered: " + optText +
                "\nNumber entered: " + optNum + " + 10 = " + optNum+10);
        return "Success!";
    }

    public boolean getOptSwitch() {
        return optSwitch;
    }

    public void setOptSwitch(boolean optSwitch) {
        this.optSwitch = optSwitch;
    }

    public String getOptText() {
        return optText;
    }

    public void setOptText(String optText) {
        this.optText = optText;
    }

    public int getOptNum() {
        return optNum;
    }

    public void setOptNum(int optNum) {
        this.optNum = optNum;
    }
}
