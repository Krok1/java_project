import java.io.Serializable;

public class Book implements Serializable {
    private String title;

    public Book(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Book{title='" + title + "'}";
    }
}
