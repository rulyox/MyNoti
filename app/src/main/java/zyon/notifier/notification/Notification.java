package zyon.notifier.notification;

public class Notification {

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

    public Notification(String color, String title, String text) {
        Color = color;
        Title = title;
        Text = text;
    }
}
