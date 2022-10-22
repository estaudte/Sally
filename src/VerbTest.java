import com.shortn.sally.interfacing.ShellVerb;

public class VerbTest extends ShellVerb {

    private boolean optSwitch;
    private String optText;
    private int optNum;

    public VerbTest() {
        super("This verb is a test of the shell processor");
        this.addOption("switch", p -> setOptSwitch(!this.optSwitch))
                .addOption("text", this::setOptText)
                .addOption("num", p -> setOptNum(convertInt(p)))
                .addOption("s", p -> setOptSwitch(!this.optSwitch))
                .addOption("t", this::setOptText)
                .addOption("n", p -> setOptNum(convertInt(p)));
    }

    @Override
    public String get() {
        System.out.println("Switch on? " + optSwitch + "\nText entered: " + optText +
                "\nNumber entered: " + optNum + " + 10 = " + (optNum+10));
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
