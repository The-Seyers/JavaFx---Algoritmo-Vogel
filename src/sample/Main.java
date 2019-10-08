package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    private final double anchoVentana = 500;
    private final double altoVentana = 700;

    /**
     *
     * @param ventana Ventana principal de la aplicación
     * @throws Exception
     */
    @Override
    public void start(Stage ventana) throws Exception{
        // Realizo la instancia del objeto componentes el cual contiene todos los nodos (componentes/elementos graficos) que se mostrarán en la ventana
        Componentes componentes = new Componentes(this.anchoVentana, this.altoVentana);

        // Agrego un archivo de estilos para estilizar algunos de los componentes
        Scene lienzo = new Scene(componentes.capaCero, this.anchoVentana,  this.altoVentana);
        lienzo.getStylesheets().add("/sample/estilos.css");

        // Agrego la escena a la ventana (Lienzo dónde caerán todos los componentes que se crearán)
        ventana.setScene(lienzo);
        // Desactivo la habilidad de redimencionar la ventana
        ventana.setResizable(false);
        // Agrego un titulo a la ventana
        ventana.setTitle("Algoritmo de Vogel");
        // Muestro la ventana al usuario
        ventana.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
