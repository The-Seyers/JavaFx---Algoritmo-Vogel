package sample;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.*;


public class EventosMatriz {

    /**
     * Método que habilitará todos los nodos (TextField) representativos a ofertas y demandas. Además deshabilitará el botón resolver
     * @param e Evento del nodo
     * @param comp Objeto actual dónde se encuentran todos los componentes
     */
    public static void habilitarOfertasDemandas (MouseEvent e, Componentes comp) {
        // Filtramos el número de clicks deseados para que pueda cumplir con nuestra acción desada
        if (e.getClickCount() == 2) {
            // Casting del botón resolver. Realizamos la busqueda del botón en nuestro conjunto de nodos en el contenedor apropiado. IMPORTANTE: Es necesario pensar que al llegar a este método nuestra ventana (aplicación) ya ha sido construida al 100% con todos sus componentes funcionales
            Button btnResolver = (Button) comp.areaControles.lookup("#btnResolver");
            btnResolver.setDisable(true);
            comp.llenadoDemandas = 0;

            // Usando el paradigma de la "programación Funcional": Es otra forma para ir recorriendo un arreglo de elementos, una alternativa más a un ciclo for, while, do-while.
            comp.areaMatriz.getChildren().stream() // Convertimos el arreglo getChildren de "Area Matriz" en un arreglo valido para ser usado en este paradigma
                    .forEach(nodoFila -> { // En cada iteración obtenemos un nodo cualquiera
                        if (nodoFila instanceof HBox) // Validamos que el nodo pertenezca a un HBox (Representado como las lineas (filas) de la matriz
                            ((HBox) nodoFila).getChildren().stream() // Casting de la fila y nuevamente realizamos un segundo recorrido de los elementos internos de esta fila
                                    .forEach(nodoColumna -> { // En cada iteración obtenemos el nodo de la fila antes extraida
                                        if (nodoColumna instanceof TextFieldNumber) // Validamos que el nodo columna sea de tipo TextFieldNumber
                                            ((TextField) nodoColumna).setDisable(false); // Casting de la demanda u oferta y deshabilitación del nodo
                                        });
                    });
        }
    }

    /**
     * Método que se encarga de la desactivación tanto de las ofertas y demandas como también de la activación del botón resolver si este cumple lo requerido
     * @param comp Objeto actual donde se encuentran todos los componentes
     * @param e Evento del nodo
     * @param nodo Nodo correspondiente a la demanda ú oferta del evento particular
     */
    public static void llenadoOfertasDemandas (Componentes comp, KeyEvent e, TextFieldNumber nodo) {
        // Filtro para que la acción pueda ser ejecutada cuando se teclee la tecla TABULADOR o el ENTER
        if ( (e.getCode() == KeyCode.TAB) || (e.getCode() == KeyCode.ENTER) ) {
            String texto = nodo.getText() +  e.getText(); // e.getText() Captura la última letra que se ha presionado. Sin esta asignación entonces solo obtendrá el texto sin el último carácter presionado en el teclado

            nodo.setDisable(true); // Deshabilito el nodo oferta o demanda

            if (texto.length() > 1) comp.llenadoDemandas++; // Debe de ser > 1 puesto que el evento keyevent añade 1 caracter oculto

            Button btnResolver = (Button) comp.areaControles.lookup("#btnResolver");
            // Si el total de llenadoDemandas es igual el número total de demandas y ofertas de la matriz entonces se activará el botón resolver, en caso contrario se deshabilitará dicho nodo
            if (comp.llenadoDemandas == (comp.filas + comp.columnas))
                btnResolver.setDisable(false);
            else btnResolver.setDisable(true);

            // Una vez realizado la acción anterior ahora se intentará pasar el foco del cursor al siguiente campo (nodo ó componente) de oferta o demanda
            HBox fila = (HBox) nodo.getParent(); // Seleccionamos el contenedor padre de envuelve este nodo (oferta o demanda)
            String [] idFila = fila.getId().split("-");
            Integer numeroFila = Integer.valueOf(idFila[idFila.length -1]);

            if (numeroFila + 1 < comp.filasTotales) { // Compruebo que exista la fila siguiente a la actual
                HBox filaSiguiente = (HBox) comp.areaMatriz.getChildren().get(numeroFila + 1);

                String[] idFilaSiguente = filaSiguiente.getId().split("-");

                if (idFilaSiguente[0].equals("fila")) {
                    // Trabajamos con las ofertas
                    filaSiguiente.lookup("#oferta").requestFocus();
                } else if (idFilaSiguente[0].equals("filaDemandas-" + (comp.filas))) { // No se agrega comp.filas + 1 ya que la matriz comienza desde 0 y no desde 1
                    filaSiguiente.getChildren().stream() // Comienzo a leer las columnas de esta fila
                        .forEach(nodoDemanda -> {
                            if (nodoDemanda instanceof TextField) { // Valido que sea una demanda
                                TextField demanda = (TextField) nodoDemanda;
                                if (!demanda.isDisable()) { // Valido que la demanda no se encuentre deshabilitado
                                    demanda.requestFocus();
                                    return; // IMPORTANTE: Si no se hace un return, entonces enviará el foco a todas las demandas vacias. Con esto logramos que solo se envie el foco a la primera demanda vacia que encuentre. Estamos deteniendo el bucle
                                }
                            }
                        });
                }
            }
        }

    }

    public static void opcionesRestaMayor (List<String> keysMayores, MouseEvent e, Logica logica, Integer numeroMayor, String filaOColumna, Integer identificador) {
        // Restaurar estado anterior
        for (String key: keysMayores) {
            String [] idNodoParts = key.split("-");
            Integer idNodo = Integer.valueOf(idNodoParts[idNodoParts.length-1]);

            logica.pintarRestasMatriz(keysMayores, numeroMayor, idNodoParts[0], idNodo);
        }

        logica.pintarRestasMatriz(new ArrayList<>(), numeroMayor, filaOColumna, identificador);
    }

    public static void elegirRestaMayor (MouseEvent e, Logica logica, Integer numeroMayor, String filaOColumna, Integer identificador) {
        if (e.getClickCount() == 1) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("¿Elegir este camino?");
            String eleccion = (filaOColumna.equals("col") )? "fila" : "columna";
            confirmacion.setContentText("¿Deseas elegir esta " + eleccion + " de celdas ?");
            //ButtonType btnSi = new ButtonType("Lo elijo",  ButtonBar.ButtonData.OK_DONE);
            //ButtonType btnNo = new ButtonType("Mejor Nó", ButtonBar.ButtonData.CANCEL_CLOSE);
            //confirmacion.getButtonTypes().addAll(btnSi, btnNo);

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.get() == ButtonType.OK) {
                //logica.resta_seleccionada.clear();
                logica.resta_seleccionada.add(filaOColumna + "-" + identificador.toString());

                logica.limpiarColoresMatriz();
                logica.agregarCeldaNumCen();

                Integer iteracionesTotales = logica.comp.filas+logica.comp.columnas;

                // OFERTAS
                for (int fila = 0; fila < logica.comp.filas; fila++) {
                    HBox nodoFila = (HBox) logica.comp.areaMatriz.getChildren().get(fila);
                    for (int col = 0; col < nodoFila.getChildren().size(); col++) {
                        if (nodoFila.getChildren().get(col) instanceof TextFieldNumber) {
                            TextFieldNumber oferta = (TextFieldNumber) nodoFila.getChildren().get(col);
                            if (!oferta.isVisible()) iteracionesTotales--;
                        }
                    }
                }

                // DEMANDAS
                HBox filaDemandas = (HBox) logica.comp.areaMatriz.lookup("#filaDemandas-" + logica.comp.filas);
                for (int col = 0; col < logica.comp.columnas; col++) {
                    TextFieldNumber demanda = (TextFieldNumber) filaDemandas.getChildren().get(col);
                    if (!demanda.isVisible()) iteracionesTotales--;
                }

                if (iteracionesTotales != 0) EventosControles.resolver(logica.comp);
            }
        }
    }

}
