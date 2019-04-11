package zyon.notifier;

import android.graphics.drawable.Drawable;

public class List_Info {

    public Drawable Image;
    private String Title;
    private String Text;

    public Drawable getImage() {
        return Image;
    }

    public void setImage(Drawable image) {
        Image = image;
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

    public List_Info(Drawable image, String title, String text) {
        Image = image;
        Title = title;
        Text = text;
    }

}
