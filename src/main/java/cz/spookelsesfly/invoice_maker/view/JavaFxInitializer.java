package cz.spookelsesfly.invoice_maker.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class JavaFxInitializer {

    private final ApplicationContext applicationContext;

    @Value("${app.width}")
    private double width;

    @Value("${app.height}")
    private double height;

    public JavaFxInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void init(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));

        loader.setControllerFactory(applicationContext::getBean);

        Parent root = loader.load();

        Scene scene = new Scene(root, width, height);
        scene.setFill(Color.TRANSPARENT);

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
    }
}
