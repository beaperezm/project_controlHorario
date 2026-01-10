package com.ue.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			//Clase que sirve para cargar un archivo fxml
			FXMLLoader loader = new FXMLLoader();
			//Se establece la ubicación de la clase 
			loader.setLocation(getClass().getResource("/view/EjemploView.fxml")); 
			Parent ventana = loader.load();
			
			Scene scene = new Scene(ventana);
			
			ventana.getStylesheets().add(getClass().getResource("/css/ejemploView.css").toExternalForm());
			//Se carga el archivo ejemplo-view.fxml en memoria y se convierte en un objeto Pane (un tipo de contenedor en JavaFX).
			//Pane ventana = (Pane) loader.load();
			
			
			//Se crea una escena con ventana (el contenido del FXML) como su nodo raíz.
			
			//Se establece la escena en el primaryStage, que es la ventana principal de la aplicación.
			primaryStage.setScene(scene);
			primaryStage.setTitle("Control Horario");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Método para lanzar la ventana
	public static void main(String[] args) {
		launch(args);
	}
}
