import java.io.Serializable;

public class House implements Serializable {
    private String address;

    public House(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "House{address='" + address + "'}";
    }
}
