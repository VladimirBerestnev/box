package client;

public class Control {

    public static Controller controller;

    public static Controller getController() {
        return controller;
    }

    public static void setController(Controller controller) {
        Control.controller = controller;
    }
}
