import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application{

	TextField txtFirstName;
	TextField txtLastName;
	TextArea txtResults;

	Socket socket;
	DataOutputStream toServer;
	DataInputStream fromServer;

	public void start(Stage mainStage) {
		BorderPane mainWindowLayout = new BorderPane();
		Label lblHeading = new Label("Film Database Search");
		lblHeading.setFont(new Font("Arial", 30));
		mainWindowLayout.setTop(lblHeading);
		mainWindowLayout.setAlignment(lblHeading, Pos.CENTER);
		mainWindowLayout.setPadding(new Insets(20, 20, 20, 20));

		Label lblFirstName = new Label("First Name : ");
		Label lblLastName = new Label("Last Name : ");
		Label lblResult = new Label("Results : ");

		txtFirstName = new TextField();
		txtLastName = new TextField();
		txtResults = new TextArea();
		txtResults.setPrefWidth(400);

		Button btnSearch = new Button("Search");
		btnSearch.setPrefWidth(100);
		GridPane fieldsPane = new GridPane();
		GridPane fieldSubPane1 = new GridPane();
		GridPane fieldSubPane2 = new GridPane();
		HBox fieldSubPane3 = new HBox();
		fieldsPane.setPadding(new Insets(20, 20, 20, 20));
		fieldSubPane1.setHgap(10);
		fieldSubPane2.setHgap(10);
		fieldsPane.setVgap(15);
		fieldSubPane1.addRow(0,lblFirstName,txtFirstName,lblLastName,txtLastName);
		fieldSubPane2.addRow(0,lblResult,txtResults);
		fieldSubPane3.getChildren().add(0,btnSearch);
		fieldsPane.addRow(0,fieldSubPane1);
		fieldsPane.addRow(1,fieldSubPane2);
		GridPane.setValignment(lblResult, VPos.TOP);
		fieldsPane.setAlignment(Pos.CENTER);

		mainWindowLayout.setCenter(fieldsPane);
		mainWindowLayout.setBottom(fieldSubPane3);
		fieldSubPane3.setAlignment(Pos.CENTER);

		btnSearch.setOnAction(e->connectToServer());


        Scene mainScene = new Scene(mainWindowLayout, 600, 400);
        mainStage.setTitle("Film Actor Search Form");
        mainStage.setScene(mainScene);
        mainStage.show();


	}



	private void connectToServer(){

		try{
			socket = new Socket("localhost", 8080);
			toServer = new DataOutputStream(socket.getOutputStream());
			fromServer = new DataInputStream(socket.getInputStream());
		}catch (Exception e){
			e.printStackTrace();
		}

		new Thread(() -> {
			try{
				toServer.writeUTF(txtFirstName.getText());
				toServer.writeUTF(txtLastName.getText());


				String[] result = fromServer.readUTF().split(",");
				for(String str : result){
					Platform.runLater(()->{
						txtResults.appendText(str + "\n");
					});
				}


			}catch (Exception ex){
				ex.printStackTrace();
			}
		}).start();
	}

	private void sendToServer(String fName, String lName){

	}

	public static void main(String[] args) {

		launch(args);
	}

}
