import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Date;

public class Server extends Application {



    @Override
    public void start(Stage stage) throws Exception {

        TextArea ta = new TextArea();

        Scene scene = new Scene(new ScrollPane(ta), 450,200);
        stage.setTitle("Search Server");
        stage.setScene(scene);
        stage.show();


        new Thread(() -> {
            try {
                ServerSocket server = new ServerSocket(8080);
                Connection connectDb = DriverManager.getConnection("jdbc:mysql://localhost/sakila", "DataBaseClass", "password");

                Platform.runLater(() ->{
                    ta.setText("Server & DB connected...." + new Date() +"\n");
                });


                while (true){
                    Socket client = server.accept();
                    DataInputStream fromClient = new DataInputStream(client.getInputStream());
                    String fName = fromClient.readUTF().trim();
                    String lName = fromClient.readUTF().trim();
                    Platform.runLater(() ->{
                        ta.appendText( fName + " " + lName + "\n");
                    });


                    PreparedStatement  prepStatement = connectDb.prepareStatement("select title from film f inner join film_actor fa on f.film_id = fa.film_id inner join actor a on a.actor_id = fa.actor_id where a.first_name = ? and a.last_name = ?;");
                    prepStatement.setString(1,fName);
                    prepStatement.setString(2,lName);
                    ResultSet result = prepStatement.executeQuery();
                    String toSend="";
                    while(result.next()){
                        toSend += result.getString(1) + ",";
                    }

                    DataOutputStream toClient = new DataOutputStream(client.getOutputStream());

                    toClient.writeUTF(toSend);
                    Platform.runLater(()->{
                        ta.appendText("Result sent to client" + "\n");
                    });

                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }).start();


    }
}
