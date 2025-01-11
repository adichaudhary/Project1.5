module jgoodman.Template {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires javafx.base;

    opens template to javafx.fxml;
    exports template;
}
