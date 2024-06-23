import java.io.Serializable;

public class Car implements Serializable {
    private String model;

    public Car(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "Car{model='" + model + "'}";
    }
}
