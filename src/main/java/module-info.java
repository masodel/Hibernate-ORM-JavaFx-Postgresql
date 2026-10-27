module ni.edu.uam.hibernateormjavafxpostgresql {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.hibernateormjavafxpostgresql to javafx.fxml;
    exports ni.edu.uam.hibernateormjavafxpostgresql;
}