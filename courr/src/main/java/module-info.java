module com.example.courr {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.io;
    requires org.apache.logging.log4j;


    opens com.example.courr to javafx.fxml;
    exports com.example.courr;
}