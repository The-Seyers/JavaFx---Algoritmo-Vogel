package sample;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.Random;

public class Componentes {
    /**
     * VARIABLES JAVAFX
     */

    // Contenedor vertical principal del la ventana
    public VBox capaCero = new VBox();
    /*
        Diseño de Básico del la aplicación

        [Contenedor: CAPA CERO]
        ==============================
                LABEL TITULO
        ==============================
                AREA MATRIZ
        ==============================
                AREA CONTROLES
        ==============================
     */

    // Contenedor de centrado único para los controles. Ubicación: Fila Inferior de la ventana
    public StackPane areaControles = new StackPane();
    // Contenedor vertical (El contenido se añadirá en filas) Ubicación: Fila central
    public VBox areaMatriz = new VBox(10);
    // Scroll para el contenedor AREA MATRIZ
    private ScrollPane scrollAreaMatriz = new ScrollPane();


    /**
     * VARIABLES DE APOYO
     */

    // Ancho de la ventana definida desde Main.java
    private double anchoVentana = 0;
    // Alto de la ventana definida desde Main.java
    private double altoVentana = 0;

    // Ancho de la celda para generar la matriz del ejercicio
    private final Integer anchoCelda = 65;
    private final Integer altoCelda = 65;
    public final String colorCelda = "palegreen";

    // Tiempo de duración de las transiciones usadas en la matriz (Solo para animar la generación de cualquier nodo)
    private final Integer tiempoTransiciones = 2000;

    // Número de filas a generar
    public Integer filas = 0;
    // Número total de las filas
    public Integer filasTotales = 0;
    // Número de columnas a generar
    public Integer columnas = 0;

    // Palanca de activación del botón resolver (Ubicado en areaControles)... Cuando se llenen por completo todas las demandas entonces se habilitará el botón resolver, en caso contrario no se podrá usar. IMPORTANTE: Para poder validar cada entrada de la demanda se deberá ingresar el número y dar un ENTER o TAB
    public Integer llenadoDemandas = 0;

    public Logica logica = new Logica();

    // Ejemplo Numeros Ezquina
//    private Integer[][] numEzq = {{5,7,0}, {8,8,0}, {6,5,0}};
//    private Integer[] ofertas = {900, 500, 600};
//    private Integer[] demandas = {1200, 700, 100};

//    private Integer[][] numEzq = {{10,2,20,11}, {12,7,9,20}, {4,14,16,18}};
//    private Integer[] ofertas = {15, 25, 10};
//    private Integer[] demandas = {5, 15, 15, 15};

//    private Integer[][] numEzq = {{45,17,21,30}, {14,18,19,31}, {0,0,0,0}};
//    private Integer[] ofertas = {15, 13, 3};
//    private Integer[] demandas = {9, 6, 7, 9};

    private Integer[][] numEzq = {{27,45,37,30}, {29,40,36,28}, {31,28,50,40}, {0,0,0,0}};
    private Integer[] ofertas = {10, 40, 20, 7};
    private Integer[] demandas = {12, 15, 30, 20};


    /**
     *  CONSTRUCTOR del objeto (Clase Componentes)
     * @param anchoVentana Ancho de la ventana
     * @param altoVentana Alto de la ventana
     */
    public Componentes (double anchoVentana, double altoVentana) {
        this.anchoVentana = anchoVentana;
        this.altoVentana = altoVentana;

        // Configuración básica de todos mis contenedores denominados AREAS. IMPORTANTE: Aquí NÓ se generá la matriz
        this.areas();

        // Construcción del contenedor AREA CONTROLES
        ensamblandoControles();
    }

    /**
     * Método que se encarga de la configuración inicial de las áreas del Lienzo
     */
    private void areas () {
        // Área Matriz
        // Dimensiones del contenedor
        //this.areaMatriz.setPrefSize(this.anchoVentana, this.altoVentana - 200);
        // Alineación central de sus nodos (Componentes internos)
        this.areaMatriz.setAlignment(Pos.CENTER);
        // Margen interno del contenedor
        this.areaMatriz.setPadding(new Insets(10, 10, 10, 10));
        // Color de fondo del contenedor
        this.areaMatriz.setStyle("-fx-background-color: crimson;");
        // Evento del usuario que se ejecutará al hacer multiples clicks en cualquier parte del contenedor. Su función es la de habilitar la escritura para las ofertas y demandas
        this.areaMatriz.setOnMouseClicked(e -> EventosMatriz.habilitarOfertasDemandas(e, this));

        // Envolviendo la Área Matriz en un SCROLL para no romper los limites cuando se genere una matriz que supere los limites de ancho y alto de la ventana
        // Redefinir la altura del contenedor
        this.scrollAreaMatriz.setPrefHeight(this.altoVentana - 200);
        // Añadir la habilidad de scroll al contenedor areaMatriz
        this.scrollAreaMatriz.setContent(areaMatriz);

        // Área de Controles
        this.areaControles.setPrefSize(this.anchoVentana, 200);
        this.areaControles.setAlignment(Pos.CENTER);
        this.areaControles.setPadding(new Insets(10, 10, 10, 10));
        this.areaControles.setStyle("-fx-background-color: indigo;");

        // LABEL TITULO
        Label titulo = new Label(("Algoritmo Vogel").toUpperCase()); // Transformar a mayusculas
        titulo.setAlignment(Pos.CENTER);
        titulo.setStyle("-fx-font-size: 22px;" + // Tamaño del texto
                "-fx-text-fill: white;" + // Color de la letra
                "-fx-background-color: crimson;");

        // CONFIGURACIÓN BÁSICA DEL CONTENEDOR PRINCIPAL
        this.capaCero.setAlignment(Pos.CENTER);
        this.capaCero.setStyle("-fx-background-color: indigo");
        // Añadir LABEL TITULO - AREA MATRIZ (FORMA DE SCROLL) - AREA CONTROLES al contenedor principal IMPORTANTE: El orden en el que se añaden es vital si se desea el diseño básico de la aplicación explicado al inicio de este archivo
        this.capaCero.getChildren().addAll(titulo, scrollAreaMatriz, areaControles);
    }

    /**
     * Método que se encarga de la construcción de la mátriz dependiendo del número de filas y número de columnas
     */
    public void ensamblandoMatriz () {
        // Los números que se creen al interior de la matriz serán generados por números random
        Random numeroRandom = new Random();
        // Instancia del objeto Duration para limitar el tiempo de las transiciones (Animaciones de los nodos)
        Duration duracion = new Duration(this.tiempoTransiciones);

        // Ciclo que controla las filas de la matriz
        for (int i = 0; i < this.filas; i++) {
            // En cada iteración se generará una nueva linea con X número de columnas
            /*
                FILA EN CICLO X
                =====================================================================================
                  CONTENEDOR LINEA (UNA NUEVA FILA VACIA ESPERANDO LA CONSTRUCCIÓN DE SUS COLUMNAS)
                =====================================================================================
                NOTA: En cada intervalo se espera una generación de una linea (fila) vacia
             */
            HBox fila = new HBox(10);
            // Identificador de la fila
            fila.setId("fila-" + i);
            fila.setAlignment(Pos.CENTER);

            // Ciclo que controla las columnas de cada linea (fila) nueva vacia
            for (int j = 0; j < this.columnas; j++) {
                // En cada iteración se generará una nueva columna (celda) el cual inmediatamente se incrustará en la linea (fila) definida en el ciclo exterior
                /*
                    COLUMNA EN CICLO X
                    FILA NUMERO "i"
                    ===========================================================
                        CELDA 1 |  CELDA 2 | ... ... ... CELDA 10 | CELDA 11
                    ===========================================================
                    NOTA: En cada intervalo se espera una generación de una linea (fila) vacia
                */
                // Ejemplo de matriz predefinida
                VBox unaCelda = celda(this.numEzq[i][j], numeroRandom.nextInt(100));

                // Creo un nuevo nodo el cual será incrustado en la columna que le corresponde
                //VBox unaCelda = celda(numeroRandom.nextInt(10), numeroRandom.nextInt(100));
                // Añado a la fila nueva un nuevo conjunto de nodos (denominados como celda) los cuales formarán una nueva columna
                fila.getChildren().add(unaCelda);

                // COLUMNAS ADICIONALES A LOS PARAMETROS INICIALES DE LA MATRIZ
                if (j == this.columnas - 1) {
                    // Solo se entrará a este if en la última iteración del ciclo j (bucle de las columnas)

                    // Columna Oferta (TextField el cual el usuario deberá ingresar la oferta disponible)
                    //fila.getChildren().add(ofertasDemandas(true, null));
                    fila.getChildren().add(ofertasDemandas(true, this.ofertas[i]));

                    // Columna ColResta (Operaciones de resta para el ejercicio de las restas de los numEsq)
                    fila.getChildren().add(celdaResta());

                    // Columna DemandasVisuales
                    fila.getChildren().add(ofertasDemandasVisuales(true, this.ofertas[i]));

                    // ESPACIADORES
                    fila.getChildren().addAll(
                            this.crearEspaciador(false),
                            this.crearEspaciador(false)
                    );
                }
            }

            // Una vez que tenemos la linea (fila) con sus celdas (columnas) creadas, entonces se añade la fila al contenedor de la matriz. Una vez agregado el proceso se repite una y otra vez hasta que se creen por completo la matriz deseada
            this.areaMatriz.getChildren().add(fila);
            this.filasTotales++;
        }

        /*
            Matriz generada al instante de esta Linea

            Eje: Filas = 2, Columnas = 2

            ============= ============= =============  =============
                Celda         Celda         OFERTA        COL_RES
                 0,0           0,1           0,2             0,3
            ============= ============= =============  =============
            ============= ============= =============  =============
                Celda         Celda         OFERTA        COL_RES
                 1,0           1,1           1,2             1,3
            ============= ============= =============  =============
         */

        // Creación de la fila demandas
        HBox filaDemandas = new HBox(10);
        filaDemandas.setId("filaDemandas-"+this.filasTotales);
        filaDemandas.setAlignment(Pos.CENTER);
        // Ciclo que añadirá las celdas (columnas) de las demandas a la linea (fila) de las demandas
        for (int col = 0; col < this.columnas; col++) {
            //filaDemandas.getChildren().add(ofertasDemandas(false, null));
            filaDemandas.getChildren().add(ofertasDemandas(false, this.demandas[col]));
        }
        // Se añaden espaciadores para no romper la maquetación de la tabla. Estos espaciadores solo sirven para ajustar los nodos en su correspondientes columnas
        // Titular
        Label titularDemanda = new Label("DEMANDA");
        titularDemanda.setPrefSize((this.anchoCelda*2) +10, this.altoCelda);
        titularDemanda.setAlignment(Pos.CENTER);
        titularDemanda.setStyle("-fx-text-fill: yellow;" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: purple;");
        filaDemandas.getChildren().addAll(
            crearEspaciador(true),
            crearEspaciador(false),
            crearEspaciador(false),
                titularDemanda
        );

        // Agregamos nuestra fila demandas a nuestro contenedor de la matriz
        this.areaMatriz.getChildren().add(filaDemandas);
        this.filasTotales++; // Se añade siempre y cuando se desee agregar una nueva fila siguiente

        // Creación de la fila FilaResta
        HBox filaRestas = new HBox(10);
        filaRestas.setId("filaRestas-" + this.filasTotales);
        filaRestas.setAlignment(Pos.CENTER);
        // Ciclo que añadirá las celdas (columnas) de las celdaRestas a la linea (fila) de las restas
        for (int col = 0; col < this.columnas; col++)
            filaRestas.getChildren().add(celdaResta());
        // Titular
        Label titularResta = new Label("RESTA");
        titularResta.setPrefSize((this.anchoCelda*2) +10, this.altoCelda);
        titularResta.setAlignment(Pos.CENTER);
        titularResta.setStyle("-fx-text-fill: yellow;" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: purple;");
        // Se añaden espaciadores para no romper la maquetación de la tabla. Estos espaciadores solo sirven para ajustar los nodos en su correspondientes columnas
        filaRestas.getChildren().addAll(
                crearEspaciador(false),
                crearEspaciador(false),
                crearEspaciador(false),
                titularResta
        );
        this.areaMatriz.getChildren().add(filaRestas);
        this.filasTotales++;

        // Creación de la fila visual Demandas
        HBox filaVisualDemandas = new HBox(10);
        filaVisualDemandas.setId("filaVisualDemandas-" +this.filasTotales);
        filaVisualDemandas.setAlignment(Pos.CENTER);
        for (int col = 0; col < this.columnas; col++)
            filaVisualDemandas.getChildren().add(ofertasDemandasVisuales(false, this.demandas[col]));
        // Titular
        Label titularVisualDemanda = new Label("DEMANDA");
        titularVisualDemanda.setPrefSize((this.anchoCelda*2) +10, this.altoCelda);
        titularVisualDemanda.setAlignment(Pos.CENTER);
        titularVisualDemanda.setStyle("-fx-text-fill: yellow;" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: purple;");
        filaVisualDemandas.getChildren().addAll(
                crearEspaciador(false),
                crearEspaciador(false),
                crearEspaciador(false),
                titularVisualDemanda
        );
        this.areaMatriz.getChildren().add(filaVisualDemandas);
        this.filasTotales++;

        // Creación de la fila Titulares
        HBox filaTitulares = new HBox(10);
        filaTitulares.setId("filaTitulares-" + this.filasTotales);
        filaTitulares.setAlignment(Pos.CENTER);
        // Titulo Matriz
        Label titularMatriz = new Label("MATRIZ");
        titularMatriz.setPrefSize((this.anchoCelda * (this.columnas) + ((this.columnas-1) * 10)), this.altoCelda);
        titularMatriz.setAlignment(Pos.CENTER);
        titularMatriz.setStyle("-fx-text-fill: yellow;" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: purple;");
        filaTitulares.getChildren().add(titularMatriz);
        // Titulo Ofertas
        Label titularOferta = new Label("OFERTA");
        titularOferta.setPrefSize(this.anchoCelda, this.altoCelda);
        titularOferta.setAlignment(Pos.CENTER);
        titularOferta.setStyle("-fx-text-fill: yellow;" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: purple;");
        filaTitulares.getChildren().add(titularOferta);
        // Titulo Restas
        Label titularRestas = new Label("RESTAS");
        titularRestas.setPrefSize(this.anchoCelda, this.altoCelda);
        titularRestas.setAlignment(Pos.CENTER);
        titularRestas.setStyle("-fx-text-fill: yellow;" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: purple;");
        filaTitulares.getChildren().add(titularRestas);
        // Titulo Oferta Visual
        Label titularVisualOferta = new Label("OFERTA");
        titularVisualOferta.setPrefSize(this.anchoCelda, this.altoCelda);
        titularVisualOferta.setAlignment(Pos.CENTER);
        titularVisualOferta.setStyle("-fx-text-fill: yellow;" +
                "-fx-font-size: 15px;" +
                "-fx-background-color: purple;");
        filaTitulares.getChildren().add(titularVisualOferta);
        filaTitulares.getChildren().addAll(
                this.crearEspaciador(false),
                this.crearEspaciador(false)
        );
        this.areaMatriz.getChildren().add(filaTitulares);

//        for (int col = 0; col < this.columnas; col++)
//            filaVisualDemandas.getChildren().add(ofertasDemandasVisuales(false, this.demandas[col]));


        // IMPORTANTE: Aqui ya no es necesario agregar un this.filasTotales++ puesto que ya no se van a agregar nuevas filas

        /*
            Matriz generada al instante de esta Linea

                    EJEMPLO
              Filas = 2, Columnas = 2          COLUMNAS ADICIONALES
            ============= =============    ============= =============
                Celda         Celda            OFERTA       COL_RES
                 0,0           0,1              0,2           0,3
            ============= =============    ============= =============
            ============= =============    ============= =============
                Celda         Celda            OFERTA       COL_RES
                 1,0           1,1              1,2           1,3
            ============= =============    ============= =============

                                FILAS ADICIONALES
            ============= =============    ============= =============
               DEMANDA       DEMANDA         ESPACIADOR    ESPACIADOR
                 2,0           2,1              2,2           2,3
            ============= =============    ============= =============
            ============= =============    ============= =============
               FIL_RES       FIL_RES         ESPACIADOR    ESPACIADOR
                 3,0           3,1              3,2           3,3
            ============= =============    ============= =============
         */
    }

    private TextFieldNumber ofertasDemandas (boolean esUnaOferta, Integer valor) {
        TextFieldNumber ofertasDemandas = new TextFieldNumber();

        if (esUnaOferta == true) {
            ofertasDemandas.setId("oferta"); // Identificador para poder ubicarlo durante los CASTING
            // Placeholder ó texto falso el cual desaparecerá al momento de hacer un focus en el nodo
            ofertasDemandas.setPromptText("Ofe");
            if (valor != null)
                ofertasDemandas.setText(valor.toString());
        } else {
            ofertasDemandas.setId("demanda");
            ofertasDemandas.setPromptText("Dem");
            if (valor != null)
                ofertasDemandas.setText(valor.toString());
        }

        ofertasDemandas.setPrefSize(this.anchoCelda, this.altoCelda);

        // Evento del usuario que se ejecutará al presionar cualquier tecla del teclado. Su función será la de monitorear el estado habilitado/deshabilitado del nodo y además habilitar el botón resolver
        ofertasDemandas.setOnKeyPressed(e -> EventosMatriz.llenadoOfertasDemandas(this, e, ofertasDemandas));

        // Transición: desplazamiento del nodo
        Duration duracion = new Duration(this.tiempoTransiciones);
        TranslateTransition desplazamiento = new TranslateTransition(duracion, ofertasDemandas);
        if (esUnaOferta == true) {
            // Posición inicial: 50px a la derecha de su posición original
            desplazamiento.setFromX(50);
            // Posición final: 0px de su posición original; es decir, en su posición original
            desplazamiento.setToX(0);
        } else {
            // Posición inicial: 50px por debajo de su posición original
            desplazamiento.setFromY(50);
            // Posición final: 0px de su posición original; es decir, en su posición original
            desplazamiento.setToY(0);
        }

        desplazamiento.play();

        return ofertasDemandas;
    }

    private StackPane ofertasDemandasVisuales (boolean esUnaOferta, Integer valor) {
        StackPane ofertasDemandas = new StackPane();
        Label lbl_ofeDem = new Label();

        if (esUnaOferta == true) {
            lbl_ofeDem.setId("visual-oferta"); // Identificador para poder ubicarlo durante los CASTING
            if (valor != null)
                lbl_ofeDem.setText(valor.toString());
        } else {
            lbl_ofeDem.setId("visual-demanda");
            if (valor != null)
                lbl_ofeDem.setText(valor.toString());
        }

        ofertasDemandas.setPrefSize(this.anchoCelda, this.altoCelda);
        lbl_ofeDem.setPrefSize(this.anchoCelda, this.altoCelda);

        // Transición: desplazamiento del nodo
        Duration duracion = new Duration(this.tiempoTransiciones);
        FadeTransition desplazamiento = new FadeTransition(duracion, ofertasDemandas);
        desplazamiento.setFromValue(0);
        desplazamiento.setToValue(1);
        desplazamiento.play();

        ofertasDemandas.getChildren().add(lbl_ofeDem);

        return ofertasDemandas;
    }

    private Label celdaResta () {
        Label celdaResta = new Label();

        celdaResta.setAlignment(Pos.CENTER);

        celdaResta.setId("celdaResta");

        celdaResta.setPrefSize(this.anchoCelda, this.altoCelda);

        return celdaResta;
    }

    private Separator crearEspaciador (boolean visible) {
        Separator espaciador = new Separator();
        espaciador.setVisible(visible);
        espaciador.setPrefSize(this.anchoCelda, this.altoCelda);

        return espaciador;
    }

    /**
     * Método que se encarga de la construcción de los controles de la aplicación
     */
    private void ensamblandoControles () {
        // Contenedor global de los controles
        HBox contenedor = new HBox(10);
        /*
            DISEÑO BÁSICO DE LOS CONTROLES

            ====================  =====================
                  FILAS,
                 COLUMNAS                 BÓTON
                    Y                    RESOLVER
              BOTÓN GENERAR
            ====================  =====================
         */

        // Área de filas y columnas
        VBox pedirDatos = new VBox(10);
        pedirDatos.setPrefWidth(this.anchoVentana / 2);

        Label lbl_filas = new Label("Número de Filas");
        TextFieldNumber txt_filas = new TextFieldNumber();

        Label lbl_columnas = new Label("Número de Columnas");
        TextFieldNumber txt_columnas = new TextFieldNumber();

        Button btn_matriz = new Button("Construir");
        // Evento del usuario que se ejecutará al hacer 1 click en botón. Su función será la de construir la matriz deseada conforme al número de filas y columnas establecidos por el usuario
        btn_matriz.setOnAction(e -> EventosControles.construirTabla(Componentes.this, txt_filas, txt_columnas));

        pedirDatos.getChildren().addAll(
                lbl_filas,
                txt_filas,
                lbl_columnas,
                txt_columnas,
                btn_matriz
        );

        // Área de acciones
        StackPane acciones = new StackPane();
        acciones.setPrefWidth(this.anchoVentana / 2);

        Button resolver = new Button("Resolver");
        resolver.setStyle("-fx-background-color: orange;" +
                "-fx-text-fill: white;");
        resolver.setId("btnResolver");
        resolver.setDisable(true);
        // Evento del usuario que se ejecutará al hacer 1 click en botón. Su función será la de resolver la matriz por medio del método Vogel
        resolver.setOnAction(e -> EventosControles.resolver(this));
        acciones.getChildren().add(resolver);

        contenedor.getChildren().addAll(pedirDatos, acciones);

        areaControles.getChildren().add(contenedor);
    }

    /**
     * Método que genera una celda para la matriz
     * @param numEsq Representa el valor númerico ubicado en la parte superior izquierda de la celda
     * @param numCen Representa el valor númerico ubicado en la parte central de la celda
     * @return VBox Celda
     */
    private VBox celda (Integer numEsq, Integer numCen) {
        /*
            DISEÑO BÁSICO DE LA CELDA

            CONTENEDOR: celda (VBOX, para que su contenido se añada en lineas o filas)
            ===================
             numEsq (Label)
                   numCen (Label)
            ===================

            REPRESENTACIÓN FINAL
            ===================
              9
                   21
            ===================
         */

        // Contenedor que envolverá diversos nodos para formar la celda
        VBox celda = new VBox();
        // Identificador del contenedor para su fácil busqueda en casting
        celda.setId("celda");
        celda.setPrefSize(this.anchoCelda, this.altoCelda);
        celda.setStyle("-fx-background-color:" + this.colorCelda);

        // Creación del nodo del número de la esquina (numEsq)
        Label lbl_numEsq = new Label(numEsq.toString()); // Esto es debido a que numEsq proviene de un Integer y por ende es necesario convertirlo en formato texto
        // Se añade un estilos a la letra de este nodo (tipo de letra: serif, En negritas, Nó cursiva, tamaño de letra = 10px;
        lbl_numEsq.setFont(Font.font("serif", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 10));
        lbl_numEsq.setStyle("-fx-padding: 5px;" +
                "-fx-text-fill: indigo;");

        // Creación del nodo del número central (numCen)
        //Label lbl_numCen = new Label(numCen.toString());
        Label lbl_numCen = new Label();
        lbl_numCen.setPrefWidth(this.anchoCelda);
        lbl_numCen.setAlignment(Pos.CENTER);
        lbl_numCen.setStyle("-fx-font-size: 20px;" + // Tamaño de la letra
                "-fx-padding: 5px;" +
                "-fx-text-fill: indigo;");

        // Se añaden los números al su contenedor correspondiente (celda). IMPORTANTE: El orden es importante
        celda.getChildren().addAll(lbl_numEsq, lbl_numCen);

        // Transiciones de cada celda generada
        Duration duracion = new Duration(this.tiempoTransiciones);

        // Transición: Rotar nodo en el eje Y
        RotateTransition rotarCelda = new RotateTransition(duracion, celda);
        // Comenzar a rotar desde el angulo 180
        rotarCelda.setFromAngle(180);
        // Terminar de rotar en el angulo 0
        rotarCelda.setToAngle(0);
        rotarCelda.setAxis(Rotate.Y_AXIS);

        // Transición: Ocultar y aparecer nodo
        FadeTransition ocultar = new FadeTransition(duracion, celda);
        // Iniciar la opacidad desde 0 = Invisible
        ocultar.setFromValue(0);
        // Terminal la opacidad con 1 = Visible
        ocultar.setToValue(1);

        // Mezclar transiciones para comenzarlas al mismo tiempo
        ParallelTransition transiciones = new ParallelTransition(rotarCelda, ocultar);
        transiciones.play();

        return celda;
    }

}
