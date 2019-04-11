package zyon.notifier;

public class List_Main {

    private String Color;
    private String Title;
    private String Text;

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public List_Main(String color, String title, String text) {
        Color = color;
        Title = title;
        Text = text;
    }
}
