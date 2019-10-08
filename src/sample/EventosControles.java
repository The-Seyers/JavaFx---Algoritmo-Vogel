package sample;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class EventosControles {

    /**
     * Método que se encarga de construir la matriz (tabla)
     * @param comp Objeto actual que contiene todos los componentes de la ventana
     * @param filas Nodo propio de las filas; Ubicado en el areaControl
     * @param columnas Nodo propio de las columnas; Ubicado en el areaControl
     */
    public static void construirTabla (Componentes comp, TextField filas, TextField columnas) {
        comp.filas = Integer.valueOf(filas.getText());
        comp.columnas = Integer.valueOf(columnas.getText());

        // Resetear filas totales
        comp.filasTotales = 0;

        // Limpiar matriz vieja
        comp.areaMatriz.getChildren().clear();
        comp.ensamblandoMatriz();

        // Resetear demandas y botón resolver
        comp.llenadoDemandas = 0;
        Button btnResolver = (Button) comp.areaControles.lookup("#btnResolver");
        btnResolver.setDisable(true);

        comp.logica.resta_seleccionada.clear();
        comp.logica.celdasLlenas.clear();
    }

    /**
     * Método encargado de realizar la resolución de la matríz por medio del método Vogel
     * @param comp Objeto actual que contiene todos los componentes de la ventana
     */
    public static void resolver (Componentes comp) {
        // Obtención de datos de cada celda
        Integer [][] lista_numEsq = new Integer[comp.filas][comp.columnas];
        Integer [][] lista_numCen = new Integer[comp.filas][comp.columnas];

        for (int fila = 0; fila < comp.filas; fila++) { // Se usa comp.filas y no comp.filasTotales debido a que no deseo leer los nodos adicionales como son: demandas, restas, etc.
            HBox filaSeleccionada = (HBox) comp.areaMatriz.getChildren().get(fila);

            for (int columna = 0; columna < comp.columnas; columna++) {
                VBox celdaSeleccionada = (VBox) filaSeleccionada.getChildren().get(columna);

                // Reviso si la elección no se ha guardado anteriormente
                boolean encontrado = false;

                if (comp.logica.celdasLlenas.size() > 0) {
                    for (String celdaRegistrada: comp.logica.celdasLlenas) {
                        String [] partesCeldaRegistrada = celdaRegistrada.split("-");
                        Integer filaCeldaRegistrada = Integer.valueOf(partesCeldaRegistrada[0]);
                        Integer colCeldaRegistrada = Integer.valueOf(partesCeldaRegistrada[1]);

                        if (fila == filaCeldaRegistrada && columna == colCeldaRegistrada)  {
                            encontrado = true;
                            break;
                        }
                    }
                }

                if (encontrado == true) {
                    lista_numEsq[fila][columna] = -2190;
                    lista_numCen[fila][columna] = -2190;
                } else {
                    lista_numEsq[fila][columna] = Integer.valueOf(
                            ((Label) celdaSeleccionada.getChildren().get(0)).getText()
                    );

                    Label numCen = (Label) celdaSeleccionada.getChildren().get(1);
                    if (!numCen.getText().equals(""))
                        lista_numCen[fila][columna] = Integer.valueOf(numCen.getText());
                }

            }
        }

        // Obtención de datos de las ofertas y demandas
        Integer [] lista_demandas = new Integer[comp.columnas];

        HBox demandas = (HBox) comp.areaMatriz.lookup("#filaDemandas-" + comp.filas);
        for (int i = 0; i < comp.columnas; i++) {
            if (demandas.getChildren().get(i) instanceof TextFieldNumber) {
                TextFieldNumber demanda = (TextFieldNumber) demandas.getChildren().get(i);
                lista_demandas[i] = Integer.valueOf(demanda.getText());
            }
        }

        Integer [] lista_ofertas = new Integer[comp.filas];
        for (int i = 0; i < comp.filas; i++) {
            HBox fila = (HBox) comp.areaMatriz.getChildren().get(i);

            for (int j = 0; j < fila.getChildren().size(); j++) {
                if (fila.getChildren().get(j) instanceof TextFieldNumber) {
                    TextFieldNumber oferta = (TextFieldNumber) fila.getChildren().get(j);
                    lista_ofertas[i] = Integer.valueOf(oferta.getText());
                }
            }
        }

        // Envio mis listas a mi objeto (clase) destinado a manejar la lógica de mi proceso
        // TODO Validar si todas las listas contienen datos y no solo valores nulos
        comp.logica.setVariablesLogica(comp, lista_numEsq, lista_numCen, lista_demandas, lista_ofertas);
    }
}
