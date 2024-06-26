module com.cgos.side {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens com.cgos.side to javafx.fxml;
    exports com.cgos.side;
}