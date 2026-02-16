package cz.spookelsesfly.invoice_maker;

import cz.spookelsesfly.invoice_maker.view.JavaFxInitializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(InvoiceMakerApplication.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        JavaFxInitializer initializer = context.getBean(JavaFxInitializer.class);
        initializer.init(primaryStage);
        primaryStage.show();
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
