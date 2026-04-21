import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        File file = new File("docs/Gui-Interface/login.html");
        engine.load(file.toURI().toString());

        stage.setTitle("CAB302");
        stage.setScene(new Scene(webView, 1280, 720));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}