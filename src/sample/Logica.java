package sample;

import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.event.MouseEvent;
import java.util.*;

public class Logica {

    // Objeto de mis componentes
    public Componentes comp;

    // Listas de mi matriz
    private Integer [][] lista_numEzq = null;
    private Integer [][] lista_numCen = null;
    private Integer [] lista_demandas = null;
    private Integer [] lista_ofertas = null;

    // Listas de apoyo
    private Integer [] resta_columnas = null; // Ubicado en la columna correspondiente a restas
    private Integer [] resta_fila = null; // Ubicado en la fila correspondiente a restas
    private Integer numeroMayor = null;
    public LinkedList<String> resta_seleccionada = new LinkedList<>();
    public LinkedList<String> celdasLlenas = new LinkedList<String>();

    public Logica () {}

    /**
     * Método que actualiza las variables escenciales para comenzar con la logica del problema
     * @param comp Objeto que contiene todos los componentes de mi ventana
     * @param lista_numEzq Lista de los números chicos de cada celda de la matriz global
     * @param lista_numCen Lista de los números centrales de cada celda de la matriz global
     */
    public void setVariablesLogica (Componentes comp, Integer[][] lista_numEzq, Integer[][] lista_numCen, Integer[] lista_demandas, Integer[] lista_ofertas) {
        this.comp = comp;

        this.lista_numEzq = lista_numEzq;
        this.lista_numCen = lista_numCen;

        this.lista_demandas = lista_demandas;
        this.lista_ofertas = lista_ofertas;

        this.resta_columnas = new Integer[comp.filas];
        this.resta_fila = new Integer[comp.columnas];

        restaChicos();
    }

    private Integer[][] transpuestaNumEzq () {
        Integer [][] transpuesta_numEzq = new Integer[this.lista_numEzq[0].length][this.lista_numEzq.length];

        for (int i = 0; i < this.lista_numEzq.length; i++)
            for (int j = 0; j < this.lista_numEzq[i].length; j++)
                transpuesta_numEzq[j][i] = this.lista_numEzq[i][j];

        return transpuesta_numEzq;
    }

    /**
     * PASO 1: Método que se encarga de realizar la operación resta VOGEL
     */
    public void restaChicos () {
        // Transpuesta de numEzq con el fin de obtener las restas para la fila de restas
        Integer [][] transpuesta_numEzq = this.transpuestaNumEzq();

        // Realizar resta tanto para columnas como para la fila
        for (int filas = 0; filas < this.lista_numEzq.length; filas++) {
            Integer [] numEzq_columnas = this.lista_numEzq[filas].clone();

            // Ordena el array de menor a mayor
            Arrays.sort(numEzq_columnas);
            Integer numA = null;
            Integer numB = null;
            for (int i = 0; i < numEzq_columnas.length; i++) {
                if (numEzq_columnas[i] == -2190) continue;

                if (numA == null) {
                    numA = numEzq_columnas[i];
                    continue;
                }
                if (numB == null) {
                    numB = numEzq_columnas[i];
                    break;
                }
            }

            if (numB == null) this.resta_columnas[filas] = numA;
            else this.resta_columnas[filas] = numB - numA;

//            if (this.resta_columnas[filas] == null) this.resta_columnas[filas] = -2190;
        }

        for (int filas = 0; filas < transpuesta_numEzq.length; filas++) {
            Integer [] numEzq_fila = transpuesta_numEzq[filas];
            Arrays.sort(numEzq_fila);
            Integer numA = null;
            Integer numB = null;
            for (int i = 0; i < numEzq_fila.length; i++) {
                if (numEzq_fila[i] == -2190) continue;

                if (numA == null) {
                    numA = numEzq_fila[i];
                    continue;
                }
                if (numB == null) {
                    numB = numEzq_fila[i];
                    break;
                }
            }

            if (numB == null) this.resta_fila[filas] = numA;
            else this.resta_fila[filas] = numB - numA;

//            if (this.resta_fila[filas] == null) this.resta_fila[filas] = -2190;
        }

        System.out.println("FILA: " + Arrays.toString(this.resta_fila));
        System.out.println("COL: " + Arrays.toString(this.resta_columnas));
        // Añadiendo la respuesta a las columnas restas de la matriz
        this.comp.areaMatriz.getChildren().stream()
                .forEach(nodo -> {
                    HBox fila = (HBox) nodo;
                    String [] idFila = fila.getId().split("-");
                    Integer i = Integer.valueOf(idFila[idFila.length - 1]);

                    if (i < this.comp.filas) {
                        fila.getChildren().stream()
                                .forEach(nodoHijo -> {
                                    if (nodoHijo instanceof Label) {
                                        Label celdaResta = (Label) nodoHijo;
                                        if (celdaResta.getId().equals("celdaResta"))
                                            if (this.resta_columnas[i] != null)
                                                celdaResta.setText(this.resta_columnas[i].toString());
                                    }
                                });
                    }
                });

        // Añadiendo la respuesta a la fila de restas de la matriz (Ubicada al inferior de la matriz)
        HBox filaRestas = (HBox) this.comp.areaMatriz.lookup("#filaRestas-" + (this.comp.filas + 1));

        for (int i = 0; i < this.resta_fila.length; i++) {
            Label celdaResta = (Label) filaRestas.getChildren().get(i);
            if (celdaResta.getId().equals("celdaResta"))
                if (this.resta_fila[i] != null)
                    celdaResta.setText(this.resta_fila[i].toString());
        }

        this.conseguirRestaMayor();
    }

    private List<String> rebovinandoRestaMayor () {
        LinkedHashMap<String, Integer> restaTotales = new LinkedHashMap<>();

        for (int i = 0; i < this.resta_columnas.length; i++)
            restaTotales.put("col-"+i, this.resta_columnas[i]);

        for (int i = 0; i < this.resta_fila.length; i++)
            restaTotales.put("fila-"+i, this.resta_fila[i]);

        this.numeroMayor = -1;

        for (String key: restaTotales.keySet()) {
            if (restaTotales.get(key) == null) continue;

            if (restaTotales.get(key) > this.numeroMayor) {
                // Reviso si la elección no se ha guardado anteriormente
                // TODO: Revisar si esto se va o se queda
//                if (this.resta_seleccionada.size() > 0) {
//                    boolean encontrado = false;
//
//                    for (String keyResueltos: this.resta_seleccionada.keySet()) {
//                        if (key.equals(keyResueltos)) {
//                            encontrado = true;
//                            break;
//                        }
//                    }
//
//                    if (encontrado == true) continue;
//                }

                this.numeroMayor = restaTotales.get(key);
            }
        }
        System.out.println("MAYOR" + this.numeroMayor);
        // Reviso si existen duplicados del número
        List<String> keysMayores = new ArrayList<>();
        for (String key: restaTotales.keySet()) {
            // TODO: Revisar si esto se va o se queda
            // Reviso si la elección no se ha guardado anteriormente
//            if (this.resta_seleccionada.size() > 0) {
//                boolean encontrado = false;
//
//                for (String keyResueltos: this.resta_seleccionada.keySet()) {
//                    if (key.equals(keyResueltos)) {
//                        encontrado = true;
//                        break;
//                    }
//                }
//
//                if (encontrado == true) continue;
//            }

            if (restaTotales.get(key) == this.numeroMayor)
                keysMayores.add(key);
        }

        return keysMayores;
    }

    /**
     * Método que se encarga de encontrar el número mayor de la operación 1. Si existen repetidos entonces el usuario decidirá la ruta a seguir
     */
    private void conseguirRestaMayor () {
        List <String> keysMayores = this.rebovinandoRestaMayor();

        if (keysMayores.size() > 1) {
            // Aqui va a entrar cuando existan multiples números mayores. El usuario tendrá que decidir que ruta seguir
            for (String key: keysMayores) {
                String [] idNodoParts = key.split("-");
                Integer idNodo = Integer.valueOf(idNodoParts[idNodoParts.length-1]);

                this.pintarRestasMatriz(keysMayores, this.numeroMayor, idNodoParts[0], idNodo);
            }
        } else {
            // Aqui va a entrar cuando exista solo 1 número mayor. El ejercicio sigue sin más
            String [] idNodoParts = keysMayores.get(0).split("-");
            Integer idNodo = Integer.valueOf(idNodoParts[idNodoParts.length-1]);
            System.out.println("KEYS MAYORES: " + keysMayores);
            this.pintarRestasMatriz(keysMayores, this.numeroMayor, idNodoParts[0], idNodo);
            //this.resta_seleccionada.clear();
            this.resta_seleccionada.add(idNodoParts[0] + "-" + idNodo.toString());

            // AGREGAR UN TIMER
            this.limpiarColoresMatriz();

            this.agregarCeldaNumCen();

            Integer iteracionesTotales = this.comp.filas+this.comp.columnas;

            // OFERTAS
            for (int fila = 0; fila < this.comp.filas; fila++) {
                HBox nodoFila = (HBox) this.comp.areaMatriz.getChildren().get(fila);
                for (int col = 0; col < nodoFila.getChildren().size(); col++) {
                    if (nodoFila.getChildren().get(col) instanceof TextFieldNumber) {
                        TextFieldNumber oferta = (TextFieldNumber) nodoFila.getChildren().get(col);
                        if (!oferta.isVisible()) iteracionesTotales--;
                    }
                }
            }

            // DEMANDAS
            HBox filaDemandas = (HBox) this.comp.areaMatriz.lookup("#filaDemandas-" + this.comp.filas);
            for (int col = 0; col < this.comp.columnas; col++) {
                TextFieldNumber demanda = (TextFieldNumber) filaDemandas.getChildren().get(col);
                if (!demanda.isVisible()) iteracionesTotales--;
            }

            if (iteracionesTotales != 0) EventosControles.resolver(this.comp);
            this.limpiarRestas();
            this.comp.areaMatriz.getChildren().remove(this.comp.areaMatriz.lookup("#filaDemandas-"+this.comp.filas));
            this.comp.areaMatriz.getChildren().remove(this.comp.areaMatriz.lookup("#filaRestas-"+(this.comp.filas + 1)));
        }
    }

    /**
     * Método que sirve para pintar las filas o columnas con la resta más mayor de todas
     * @param keysMayores   Lista de nodos (Identificador string) que poseen el mismo número mayor
     * @param numeroMayor   Número mayor
     * @param filaOColumna  Identificador para revisar si es una columna o una fila
     * @param identificador Identificador númerico para comenzar a pintar
     */
    public void pintarRestasMatriz (List<String> keysMayores, Integer numeroMayor, String filaOColumna, Integer identificador) {
        String color = "yellow";
        if (keysMayores.size() > 1) color = "gray";

        HBox filaSeleccionada = null;

        if (filaOColumna.equals("col")) {
            filaSeleccionada = (HBox) this.comp.areaMatriz.getChildren().get(identificador);

            for (int i = 0; i < this.comp.columnas; i++) {
                filaSeleccionada.getChildren().get(i).setStyle("-fx-background-color:" + color + ";" +
                        "-fx-cursor: hand;");
                // Habilitamos el evento de selección para que el usuario decida que camino seguir
                if (keysMayores.size() > 1) {
                    filaSeleccionada.getChildren().get(i).setOnMouseEntered(e -> EventosMatriz.opcionesRestaMayor(keysMayores, e, this, numeroMayor, filaOColumna, identificador));
                    filaSeleccionada.getChildren().get(i).setOnMouseClicked(e -> EventosMatriz.elegirRestaMayor(e, this, numeroMayor, filaOColumna, identificador));
                }
            }
        } else {
            // Aqui se selecciona la fila de restas (Ubicado debajo de la matriz generada)
            for (int i = 0; i < this.comp.filas; i++) {
                filaSeleccionada = (HBox) this.comp.areaMatriz.getChildren().get(i);
                filaSeleccionada.getChildren().get(identificador).setStyle("-fx-background-color:" + color + ";" +
                        "-fx-cursor: hand;");
                // Habilitamos el evento de selección para que el usuario decida que camino seguir
                if (keysMayores.size() > 1)
                    filaSeleccionada.getChildren().get(identificador).setOnMouseEntered(e -> EventosMatriz.opcionesRestaMayor(keysMayores, e, this, numeroMayor, filaOColumna, identificador));
                    filaSeleccionada.getChildren().get(identificador).setOnMouseClicked(e -> EventosMatriz.elegirRestaMayor(e, this, numeroMayor, filaOColumna, identificador));
            }
        }
    }

    /**
     * Método que limpiará todos los colores ajenos a su escencia de la matriz orginal
     */
    public void limpiarColoresMatriz () {
        this.comp.areaMatriz.getChildren().stream()
                .forEach(nodo -> {
                    if (nodo instanceof HBox) {
                        HBox fila = (HBox) nodo;
                        String [] idFila = fila.getId().split("-");
                        Integer id = Integer.valueOf(idFila[idFila.length-1]);

                        if (id < this.comp.filas) {
                            fila.getChildren().stream()
                                    .forEach(nodoHijo -> {
                                        if (nodoHijo instanceof VBox) {
                                            VBox celda = (VBox) nodoHijo;
                                            if (celda.getId().equals("celda")) {
                                                celda.setStyle("-fx-background-color:" + this.comp.colorCelda);
                                                celda.setOnMouseClicked(null);
                                                celda.setOnMouseEntered(null);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void limpiarRestas () {
        this.comp.areaMatriz.getChildren().stream()
                .forEach(nodo -> {
                    HBox fila = (HBox) nodo;
                    fila.getChildren().stream()
                            .forEach(nodo2 -> {
                                if (nodo2 instanceof Label) {
                                    Label celda = (Label) nodo2;
                                    if (celda.getId() != null)
                                        if (celda.getId().equals("celdaResta"))
                                            celda.setText("");
                                }
                            });
                });
    }

    /**
     * Paso 2: Método que se encarga de agregar la cantidad correspondiente a la celda en su posición central
     */
    public void agregarCeldaNumCen () {
        String keyLast = this.resta_seleccionada.getLast();

        System.out.println("KYES :" + this.resta_seleccionada);
        System.out.println("KEYLAST: " + keyLast);
        // Obtenemos el último dato agregado
        String [] idPartes = keyLast.split("-");
        Integer posColumna = Integer.valueOf(idPartes[idPartes.length-1]);

        Integer [] numEzq_seleccionada = null;

        if (idPartes[0].equals("col")) {
            // Trabajar con la columna
            numEzq_seleccionada = this.lista_numEzq[posColumna].clone();
        } else {
            // Trabajar con la fila restas
            Integer [][] transpuesta_numEzq = this.transpuestaNumEzq();
            numEzq_seleccionada = transpuesta_numEzq[posColumna].clone();
        }

        Integer [] numEzq_seleccionada_copia = numEzq_seleccionada.clone();
        Arrays.sort(numEzq_seleccionada_copia);

        Integer numeroMenor = numEzq_seleccionada_copia[0];

        if (numeroMenor == -2190) {
            for (int i = 0; i < numEzq_seleccionada_copia.length; i++) {
                if (numEzq_seleccionada_copia[i] == -2190) continue;
                numeroMenor = numEzq_seleccionada_copia[i];
                break;
            }
        }

        // El número más chico
        // TODO: Posible error debido a que existan multiples numeros menores. Por defecto se enviará el primero que encuentre
        Integer posFila = -1;
        for (int i = 0; i < numEzq_seleccionada.length; i++) {
            if (numeroMenor == numEzq_seleccionada[i]) {
                posFila = i;
                break;
            }
        }

        // Reviso si la elección no se ha guardado anteriormente
        if (this.celdasLlenas.size() > 0) {
            int encontrado = 0;

            for (String celdaRegistrada: this.celdasLlenas) {
                String [] partesCeldaRegistrada = celdaRegistrada.split("-");
                Integer filaCeldaRegistrada = Integer.valueOf(partesCeldaRegistrada[0]);
                Integer colCeldaRegistrada = Integer.valueOf(partesCeldaRegistrada[1]);

                if (idPartes[0].equals("col")) {
                    if (posColumna == filaCeldaRegistrada) encontrado++;
                    if (posFila == colCeldaRegistrada) encontrado++;
                } else {
                    if (posFila == filaCeldaRegistrada) encontrado++;
                    if (posColumna == colCeldaRegistrada) encontrado++;
                }
            }

            if (encontrado == 2) System.out.println("CELDA REPETIDA");
        }

        // Seleccionar la celda ganadora
        VBox celda = null;

        if (idPartes[0].equals("col")) {
            HBox fila = (HBox) this.comp.areaMatriz.getChildren().get(posColumna);
            celda = (VBox) fila.getChildren().get(posFila);
            //this.celdasLlenas.add(posColumna.toString() + "-" + posFila.toString());
        } else {
            HBox fila = (HBox) this.comp.areaMatriz.getChildren().get(posFila);
            celda = (VBox) fila.getChildren().get(posColumna);
            //this.celdasLlenas.add(posFila.toString() + "-" + posColumna.toString());
        }

        celda.setStyle("-fx-background-color: tomato;");

        // Obtener diferencia demandas
        Integer numeroCentral = diferenciaOfertaDemanda(idPartes[0], posFila, posColumna);
        ((Label)celda.getChildren().get(1)).setText(numeroCentral.toString());

        // Última iteración
        if (this.comp.columnas * this.comp.filas - this.celdasLlenas.size() == 1) {
            for (int i = 0; i < this.comp.filas; i++) {
                for (int j = 0; j < this.comp.columnas; j++) {
                    if (!this.celdasLlenas.contains(i + "-" + j)) {
                        HBox fila = (HBox) this.comp.areaMatriz.getChildren().get(i);
                        celda = (VBox) fila.getChildren().get(j);

                        HBox filaDemandas = (HBox) this.comp.areaMatriz.lookup("#filaDemandas-"+this.comp.filas);
                        TextFieldNumber demanda = (TextFieldNumber) filaDemandas.getChildren().get(j);
                        ((Label)celda.getChildren().get(1)).setText(demanda.getText());
                        demanda.setText("0");
                        demanda.setVisible(false);

                        TextFieldNumber oferta = (TextFieldNumber) fila.lookup("#oferta");
                        oferta.setText("0");
                        oferta.setVisible(false);
                    }
                }
            }
        }
        this.limpiarColoresMatriz();
        this.limpiarRestas();
    }

    private Integer diferenciaOfertaDemanda (String filaOColumna, Integer fila, Integer columna) {
        Integer oferta = null;
        Integer demanda = null;

        Integer diferencia = null;

        if (filaOColumna.equals("col")) {
            oferta = this.lista_ofertas[columna];
            demanda = this.lista_demandas[fila];
        } else {
            oferta = this.lista_ofertas[fila];
            demanda = this.lista_demandas[columna];
        }

//        System.out.println("FILA: " + fila + " COL:" + columna);
//        System.out.println(Arrays.toString(this.lista_ofertas));
//        System.out.println(Arrays.toString(this.lista_demandas));
//        System.out.println("OFE: " + oferta + " DEM: " + demanda);

        if (oferta > demanda) {
            // Caso cuando la oferta es mayor
            diferencia = oferta - demanda;

            if (filaOColumna.equals("col")) this.recalcularDemandas(columna, fila, diferencia, true, demanda);
            else this.recalcularDemandas(fila, columna, diferencia, true, demanda);

            if (demanda == 0) return oferta;
            return demanda;
        } else {
            // Caso cuando la demanda es mayor
            diferencia = demanda - oferta;

            if (filaOColumna.equals("col")) this.recalcularDemandas(columna, fila, diferencia, false, oferta);
            else this.recalcularDemandas(fila, columna, diferencia, false, oferta);

            if (oferta == 0) return demanda;
            return oferta;
        }
    }

    private void recalcularDemandas (Integer posFila, Integer posColumna, Integer diferencia, boolean esMayorOferta, Integer ofertaDemanda) {
        // Ofertas
        HBox nodoFila = (HBox) this.comp.areaMatriz.getChildren().get(posFila);
        for (int i = 0; i < nodoFila.getChildren().size(); i++) {
            if (nodoFila.getChildren().get(i) instanceof TextFieldNumber) {
                TextFieldNumber nodoOferta = (TextFieldNumber) nodoFila.getChildren().get(i);
                nodoOferta.setStyle("-fx-text-fill: green");
                nodoOferta.setStyle("-fx-background-color: cyan");

                if (diferencia == 0) {
                    // Cuando la demanda y oferta son iguales, entonces se cancelan los 2
                    nodoOferta.setText("0");
                    nodoOferta.setVisible(false);

                    // Desactivar numEzq de toda la columna y toda la fila
                    for (int i2 = 0; i2 < this.comp.filas; i2++) {
                        String celdaSellada = i2 + "-" + posColumna.toString();
                        this.celdasSelladas(celdaSellada);
                    }
                    for (int i2 = 0; i2 < this.comp.columnas; i2++) {
                        String celdaSellada = posFila.toString() + "-" + i2;
                        this.celdasSelladas(celdaSellada);
                    }
                } else {
                    if (ofertaDemanda == 0) {
                        nodoOferta.setText("0");
                        nodoOferta.setVisible(false);
                        // Desactivar numEzq de toda la fila
                        for (int j = 0; j < this.comp.columnas; j++) {
                            String celdaSellada = posFila.toString() + "-" + j;
                            this.celdasLlenas.stream();
                            this.celdasSelladas(celdaSellada);
                        }
                    } else {
                        if (esMayorOferta) nodoOferta.setText(diferencia.toString());
                        else {
                            nodoOferta.setText("0");
                            nodoOferta.setVisible(false);
                            // Desactivar numEzq de toda la fila
                            for (int j = 0; j < this.comp.columnas; j++) {
                                String celdaSellada = posFila.toString() + "-" + j;
                                this.celdasSelladas(celdaSellada);
                            }
                        }
                    }
                }
            }
        }

        // Demandas
        HBox nodoColumnas = (HBox) this.comp.areaMatriz.lookup("#filaDemandas-" + this.comp.filas);
        TextFieldNumber nodoDemanda = (TextFieldNumber) nodoColumnas.getChildren().get(posColumna);
        nodoDemanda.setStyle("-fx-text-fill: crimson");
        if (diferencia == 0) {
            // Cuando la demanda y oferta son iguales, entonces se cancelan los 2
            nodoDemanda.setText("0");
            nodoDemanda.setVisible(false);

            // Desactivar numEzq de toda la columna y toda la fila
            for (int i = 0; i < this.comp.filas; i++) {
                String celdaSellada = i + "-" + posColumna.toString();
                this.celdasSelladas(celdaSellada);
            }
            for (int i = 0; i < this.comp.columnas; i++) {
                String celdaSellada = posFila.toString() + "-" + i;
                this.celdasSelladas(celdaSellada);
            }
        } else {
            if (ofertaDemanda == 0) {
                nodoDemanda.setText("0");
                nodoDemanda.setVisible(false);
                // Desactivar numEzq de toda la columna
                for (int i = 0; i < this.comp.filas; i++) {
                    String celdaSellada = i + "-" + posColumna.toString();
                    this.celdasSelladas(celdaSellada);
                }
            } else {
                if (esMayorOferta) {
                    nodoDemanda.setText("0");
                    nodoDemanda.setVisible(false);
                    // Desactivar numEzq de toda la columna
                    for (int i = 0; i < this.comp.filas; i++) {
                        String celdaSellada = i + "-" + posColumna.toString();
                        this.celdasSelladas(celdaSellada);
                    }
                } else nodoDemanda.setText(diferencia.toString());
            }
        }
        System.out.println();
    }

    private void celdasSelladas (String llaveCeldaSellada) {
        for (int i = 0; i < this.celdasLlenas.size(); i++) {
            if (this.celdasLlenas.get(i).equals(llaveCeldaSellada)) return;
        }

        this.celdasLlenas.add(llaveCeldaSellada);
    }

}
