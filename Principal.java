import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;



public class Principal {

    //Scanner utilizado para interactuar con el programa por medio del teclado

    private static final Scanner teclado = new Scanner(System.in);

    //Método de la main, usado para cargar los datos iniciales y repetir el menú hasta que el usuario decida salir

    public static void main(String[] arg) {

        
        // Crea el inventario
        Inventario inventario = new Inventario();
        // Carga los trabajos y nodos iniciales
        cargarDatosIniciales(inventario);

        // Opción elegida por el usuario (-1 para que el ciclo inicie)
        int opcion = -1;

        // El menú se repite hasta que el usuario elige 0
        while (opcion != 0) {

            //Muestra las opciones
            mostrarMenu();

            //Lee la opción (si el formato es incorrecto, vuelve a preguntar)
            opcion = leerEntero("Seleccione una opción: ");

            try {
                //Cada opción llama a su método, cualquier regla incumplida llega como excepción
                switch (opcion) {
                    case 1:
                        cotizarTrabajo(inventario);
                        break;
                    case 2:
                        registrarTrabajo(inventario);
                        break;
                    case 3:
                        registrarNodo(inventario);
                        break;
                    case 4:
                        crearLote(inventario);
                        break;
                    case 5:
                        asignarTrabajo(inventario);
                        break;
                    case 6:
                        retirarTrabajo(inventario);
                        break;
                    case 7:
                        iniciarLote(inventario);
                        break;
                    case 8:
                        registrarResultado(inventario);
                        break;
                    case 9:

                        //Imprime todos los trabajos
                        System.out.println(inventario.listarTrabajos());
                        break;

                    case 10:

                        //Imprime todos los nodos
                        System.out.println(inventario.listarNodos());
                        break;
                    case 11:
                        mostrarReportes(inventario);
                        break;
                    case 0:

                        //Termina porque opcion vale 0
                        System.out.println("Hasta pronto.");
                        break;
                    default:
                        //Cualquier otro número no corresponde a una opción del menú
                        System.out.println("Opción inválida. Elija un número del menú.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                //Se informa el motivo de por qué no se pudo llevar a cabo la operación
                System.out.println("Operación rechazada: " + e.getMessage());
            }
        }

        


    }

    // Imprime las opciones del menú
    private static void mostrarMenu() {
        System.out.println("\nRenderGT");
        System.out.println(" 1. Cotizar trabajo");
        System.out.println(" 2. Registrar trabajo");
        System.out.println(" 3. Registrar nodo");
        System.out.println(" 4. Crear lote");
        System.out.println(" 5. Asignar trabajo a lote");
        System.out.println(" 6. Retirar trabajo de lote");
        System.out.println(" 7. Iniciar lote");
        System.out.println(" 8. Registrar resultado de ejecución");
        System.out.println(" 9. Consultar trabajos");
        System.out.println("10. Consultar nodos");
        System.out.println("11. Reportes");
        System.out.println(" 0. Salir");
    }

    //Pide los datos de un trabajo y muestra su costo y tiempo de entrega sin registrar nada
    private static void cotizarTrabajo(Inventario inventario) {

        // Lee los datos y construye el trabajo de la categoría elegida
        Trabajo trabajo = pedirTrabajo();

        // Valida el trabajo y obtiene la cotización (no se registra)
        Trabajo cotizado = inventario.cotizacion(trabajo);

        //Muestra el resultado de la cotización
        System.out.println("Cotización (no se registró nada)");
        System.out.println("Categoría: " + cotizado.categoria());
        System.out.println("Costo: Q" + cotizado.calcularCosto());
        System.out.println("Tiempo estimado de entrega: " + cotizado.tiempoEntrega() + " horas");
    }

    //Pide los datos de un trabajo y lo registra (se cobra el monto completo y queda en cola)
    private static void registrarTrabajo(Inventario inventario) {

        // Lee los datos y construye el trabajo de la categoría elegida
        Trabajo trabajo = pedirTrabajo();

        //Registra el trabajo (valida primero y solo cobra si todo es válido)
        inventario.registrarTrabajo(trabajo);

        //Confirma el registro con el monto cobrado
        System.out.println("Trabajo " + trabajo.getCodigo() + " registrado en cola. Cobro: Q" + trabajo.calcularCosto());
    }

    //Pide los datos de un nodo y lo registra
    private static void registrarNodo(Inventario inventario) {

        // Lee el código, la capacidad y si tiene GPU
        int codigo = leerEntero("Código del nodo: ");
        int capacidad = leerEntero("Capacidad máxima de horas por lote: ");
        boolean gpu = leerSiNo("¿Cuenta con GPU? (1 = Sí, 2 = No): ");

        //Crea el nodo y lo registra (el inventario rechaza datos inválidos o códigos repetidos)
        inventario.registrarNodo(new Nodo(capacidad, gpu, codigo));

        //Confirma el registro
        System.out.println("Nodo " + codigo + " registrado.");
    }

    // Crea un lote para el nodo indicado
    private static void crearLote(Inventario inventario) {

        //Busca el nodo por código (si no existe, lanza excepción)
        Nodo nodo = hallarNodo(inventario, leerEntero("Código del nodo: "));

        //Crea el lote (se rechaza si el nodo ya tiene un lote sin cerrar)
        inventario.crearLote(nodo);

        //Obtiene los lotes para recuperar el último creado
        ArrayList<Lote> lotes = inventario.getListaLotes();
        Lote nuevo = lotes.get(lotes.size() - 1);

        // Informa el código del lote creado
        System.out.println("Lote " + nuevo.getCodigoLote() + " creado para el nodo " + nodo.getCodigoNodo() + ".");
    }

    // Asigna un trabajo en cola a un lote
    private static void asignarTrabajo(Inventario inventario) {

        // Busca el lote y el trabajo por código (si no existen, lanza excepción)
        Lote lote = hallarLote(inventario, leerEntero("Código del lote: "));
        Trabajo trabajo = hallarTrabajo(inventario, leerEntero("Código del trabajo: "));
        // Asigna el trabajo (el lote valida estado, GPU y capacidad)
        inventario.asignarTrabajo(lote, trabajo);
        // Confirma la asignación
        System.out.println("Trabajo " + trabajo.getCodigo() + " asignado al lote " + lote.getCodigoLote() + ".");
    }

    // Retira un trabajo de un lote en preparación
    private static void retirarTrabajo(Inventario inventario) {

        //Busca el lote y el trabajo por código (si no existen, lanza excepción)
        Lote lote = hallarLote(inventario, leerEntero("Código del lote: "));
        Trabajo trabajo = hallarTrabajo(inventario, leerEntero("Código del trabajo: "));
        // Retira el trabajo (el lote valida el estado y la pertenencia)
        inventario.retirarTrabajo(lote, trabajo);

        // Confirma el retiro
        System.out.println("Trabajo " + trabajo.getCodigo() + " retirado; vuelve a quedar en cola.");
    }

    // Inicia un lote y muestra los cargos por reintento que generó
    private static void iniciarLote(Inventario inventario) {


       // Busca el lote por código (si no existe, lanza excepción)
        Lote lote = hallarLote(inventario, leerEntero("Código del lote: "));
       // Guarda el ingreso por reintentos actual para calcular cuánto se cargó con este inicio
        double antes = inventario.getIngresoReintentos();

        // Inicia el lote (los cargos por reintento se registran solo si el inicio es válido)
        inventario.iniciarLote(lote);
        // Confirma el inicio
        System.out.println("Lote " + lote.getCodigoLote() + " iniciado.");
        // Muestra la diferencia entre el ingreso por reintentos de antes y de ahora
        System.out.println("Cargos por reintento generados: Q" + (inventario.getIngresoReintentos() - antes));
    }

    //Registra el resultado de un trabajo (completado o ejecución fallida)
    private static void registrarResultado(Inventario inventario) {

        // Busca el lote y el trabajo por código (si no existen, lanza excepción)
        Lote lote = hallarLote(inventario, leerEntero("Código del lote: "));
        Trabajo trabajo = hallarTrabajo(inventario, leerEntero("Código del trabajo: "));
        //Lee el resultado: 1 = completado (true), 2 = fallido (false)
        boolean completado = leerSiNo("Resultado (1 = Completado, 2 = Ejecución fallida): ");
        //Registra el resultado (el lote actualiza el trabajo y se cierra si todos tienen resultado)
        inventario.registrarResultado(lote, trabajo, completado);
        // Muestra el estado en que quedaron el trabajo y el lote
        System.out.println("Resultado registrado. Estado del trabajo: " + trabajo.getEstadoTrabajo() + ". Estado del lote: " + lote.getEstadoLote() + ".");
    }

    // Muestra el submenú de reportes y el reporte elegido
    private static void mostrarReportes(Inventario inventario) {
        // Imprime las opciones de reporte
        System.out.println("1. Trabajos por categoría y estado");
        System.out.println("2. Ingresos");
        System.out.println("3. Lotes sin cerrar");

        // Lee la opción elegida
        int opcion = leerEntero("Reporte: ");

        // Muestra el reporte elegido
        switch (opcion) {
            case 1:
                System.out.println(inventario.reporteTrabajosPorCategoriaYEstado());
                break;
            case 2:
                System.out.println(inventario.reporteIngresos());
                break;
            case 3:
                System.out.println(inventario.reporteLotesAbiertos());
                break;
            default:
                System.out.println("Opción de reporte inválida.");
        }
    }


    // Pide la categoría y sus datos, y construye el trabajo correspondiente
     private static Trabajo pedirTrabajo() {

        // Muestra las categorías disponibles
        System.out.println("Categoría: 1) Imagen fija  2) Animación  3) Animación con postproducción  4) Simulación física");
        // Lee la categoría elegida
        int categoria = leerEntero("Categoría: ");

        // Rechaza categorías fuera del rango 1 a 4
        if (categoria < 1 || categoria > 4) {
            throw new IllegalArgumentException("Categoría inválida.");
        }

        //Lee los datos comunes a todos los trabajos
        int codigo = leerEntero("Código del trabajo: ");
        String estudio = leerTexto("Nombre del estudio: ");
        String proyecto = leerTexto("Nombre del proyecto: ");
        double horas = leerDecimal("Horas de cómputo estimadas: ");
        boolean diurno = leerSiNo("Horario (1 = Diurno, 2 = Nocturno): ");

        // Según la categoría, pide los datos propios y construye el objeto correspondiente
        switch (categoria) {
            // Imagen fija, no tiene datos propios
            case 1:
                return new ImagenFija(codigo, estudio, proyecto, horas, diurno);
            // Animación, pide la cantidad de fotogramas
            case 2:
                return new Animacion(codigo, estudio, proyecto, horas, diurno, leerEntero("Cantidad de fotogramas: "));
            case 3:
            // Animación con postproducción, pide fotogramas y valor de recursos licenciados
                int fotogramas = leerEntero("Cantidad de fotogramas: ");
                double recursos = leerDecimal("Valor declarado de recursos licenciados (Q): ");
                return new AnimacionPostProduccion(codigo, estudio, proyecto, horas, diurno, fotogramas, recursos);
            default:
                // Simulación física, pide la resolución de la malla
                return new SimulacionFisica(codigo, estudio, proyecto, horas, diurno, leerEntero("Resolución de la malla (millones de celdas, 1 a 50): "));
        }
    }

    // Busca un trabajo por código y lanza excepción si no existe
    private static Trabajo hallarTrabajo(Inventario inventario, int codigo) {

        // Busca el trabajo en el inventario
        Trabajo trabajo = inventario.buscarTrabajo(codigo);
       // Si no existe, rechaza la operación
        if (trabajo == null) {
            throw new IllegalArgumentException("No existe un trabajo con el código " + codigo + ".");
        }

        // Devuelve el trabajo encontrado
        return trabajo;
    }

    // Busca un nodo por código y lanza excepción si no existe
    private static Nodo hallarNodo(Inventario inventario, int codigo) {
       // Busca el nodo en el inventario
        Nodo nodo = inventario.buscarNodo(codigo);

        // Si no existe, rechaza la operación
    
        if (nodo == null) {
            throw new IllegalArgumentException("No existe un nodo con el código " + codigo + ".");
        }

        // Devuelve el nodo encontrado
        return nodo;
    }

    // Busca un lote por código y lanza excepción si no existe
    private static Lote hallarLote(Inventario inventario, int codigo) {

        // Busca el lote en el inventario
        Lote lote = inventario.buscarLote(codigo);

        // Si no existe, rechaza la operación
        if (lote == null) {
            throw new IllegalArgumentException("No existe un lote con el código " + codigo + ".");
        }

        // Devuelve el lote encontrado
        return lote;
    }

    // Lee un entero con nextInt() + nextLine(); repite la pregunta si el formato es incorrecto
    private static int leerEntero(String mensaje) {

        // Se repite hasta recibir un entero válido
        while (true) {

            // Muestra la pregunta
            System.out.print(mensaje);
            try {

                // Lee el entero
                
                int valor = teclado.nextInt();

                // Limpia el salto de línea que queda
                teclado.nextLine();
                // Devuelve el valor leído y termina el ciclo
                return valor;
            } catch (InputMismatchException e) {

                // Descarta lo escrito (el dato incorrecto) para poder preguntar de nuevo
                teclado.nextLine();
                // Avisa que la entrada no fue válida
                System.out.println("Entrada inválida: escriba un número entero.");
            }
        }
    }

    // Lee un decimal con nextDouble() + nextLine(); repite la pregunta si el formato es incorrecto
    private static double leerDecimal(String mensaje) {

        // Se repite hasta recibir un número válido
        while (true) {
            // Muestra la pregunta
            System.out.print(mensaje);
            try {
                // Lee el decimal
                double valor = teclado.nextDouble();
               
               // Limpia el salto de línea que queda
                teclado.nextLine();
                // Devuelve el valor leído y termina el ciclo
                return valor;
            } catch (InputMismatchException e) {

                // Descarta lo escrito (el dato incorrecto) para poder preguntar de nuevo
               
                teclado.nextLine();
                // Avisa que la entrada no fue válida
                System.out.println("Entrada inválida: escriba un número (use punto decimal).");
            }
        }
    }

    // Lee una línea de texto completa (permite espacios)
    private static String leerTexto(String mensaje) {

        // Muestra la pregunta
        System.out.print(mensaje);

        // Devuelve la línea escrita por el usuario
        return teclado.nextLine();
    }


    //Lee 1 o 2 y lo convierte a boolean (1 = true, 2 = false), repite la pregnta si es otro valor
    private static boolean leerSiNo(String mensaje) {
        while (true) {

            // Reutiliza la lectura de enteros
            int valor = leerEntero(mensaje);

            // 1 equivale a true
            if (valor == 1) {
                return true;
            }

            // 2 equivale a false
            if (valor == 2) {
                return false;
            }

            // Cualquier otro valor no es valido
            System.out.println("Entrada inválida: escriba 1 o 2.");
        }
    }

    // Carga los datos iniciales: 2 trabajos de cada categoría en cola (sin cobro) y 3 nodos (uno con GPU y dos sin ella)
    private static void cargarDatosIniciales(Inventario inventario) {
      
        inventario.cargarTrabajoInicial(new ImagenFija(101, "so", "es", 1.5, true));
        inventario.cargarTrabajoInicial(new ImagenFija(102, "Eso", "dafasggas", 2.0, false));
      
        inventario.cargarTrabajoInicial(new Animacion(201, "gasdgas", "gasgda", 9.5, true, 610));
        inventario.cargarTrabajoInicial(new Animacion(202, "gasgas", "gasgda", 20.0, false, 500));
       
        inventario.cargarTrabajoInicial(new AnimacionPostProduccion(301, "gdagda", "daga", 9.5, true, 610, 1800.0));
        inventario.cargarTrabajoInicial(new AnimacionPostProduccion(302, "gasg", "gsdgs", 6.2, false, 250, 900.0));
       
        inventario.cargarTrabajoInicial(new SimulacionFisica(401, "asga", "asgdsa", 15.0, false, 10));
        inventario.cargarTrabajoInicial(new SimulacionFisica(402, "asgag", "dsakgap", 6.5, false, 25));
      
        inventario.registrarNodo(new Nodo(24, false, 1));
        inventario.registrarNodo(new Nodo(40, true, 2));
        inventario.registrarNodo(new Nodo(12, false, 3));
    }





    
}
