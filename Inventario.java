import java.util.ArrayList;

public class Inventario {

    // Todos los trabajos registrados
    private ArrayList<Trabajo> listaTrabajos;

    // Todos los nodos registrados
    private ArrayList<Nodo> listaNodos;
    // Todos los lotes creados
    private ArrayList<Lote> listaLotes;

    // Ingreso acumulado por el registro de trabajos
    private double ingresoRegistro;

    // Ingreso acumulado por cargos de reintento
    private double ingresoReintentos;

    // Nombres de las categorías con ingresos
    private ArrayList<String> categoriasIngreso;
    // Ingreso acumulado de cada categoría (paralelo a categoriasIngreso)
    private ArrayList<Double> valoresIngresoCategoria;
    // Contador para asignar el código del siguiente lote
    private int contadorLotes;

    // Constructor: todas las listas empiezan vacías y los ingresos en 0
    public Inventario() {
        this.listaTrabajos = new ArrayList<Trabajo>();
        this.listaNodos = new ArrayList<Nodo>();
        this.listaLotes = new ArrayList<Lote>();
        this.ingresoRegistro = 0;
        this.ingresoReintentos = 0;
        this.categoriasIngreso = new ArrayList<String>();
        this.valoresIngresoCategoria = new ArrayList<Double>();
        this.contadorLotes = 0;
    }

    public ArrayList<Trabajo> getListaTrabajos() {
        return new ArrayList<Trabajo>(listaTrabajos);
    }

    public ArrayList<Nodo> getListaNodos() {
        return new ArrayList<Nodo>(listaNodos);
    }

    public ArrayList<Lote> getListaLotes() {
        return new ArrayList<Lote>(listaLotes);
    }

    public double getIngresoRegistro() {
        return ingresoRegistro;
    }

    public double getIngresoReintentos() {
        return ingresoReintentos;
    }

    // Busca un trabajo por su código, devuelve null si no existe
     public Trabajo buscarTrabajo(int codigo) {
        // Recorre los trabajos registrados
        for (Trabajo trabajo : listaTrabajos) {
            // Si el código coincide, devuelve ese trabajo
            if (trabajo.getCodigo() == codigo) {
                return trabajo;
            }
        }

        // No se encontró ninguno
        return null;
    }

    // Busca un nodo por su códigp, devuelve null si no existe
    public Nodo buscarNodo(int codigo) {

        // Recorre los nodos registrados
        for (Nodo nodo : listaNodos) {
            // Si el código coincide, devuelve ese nodo
            if (nodo.getCodigoNodo() == codigo) {
                return nodo;
            }
        }
        // No se encontró ninguno
        return null;
    }


    // Busca un lote por su código, devuelve null si no existe
    public Lote buscarLote(int codigo) {
       // Recorre los lotes creados
        for (Lote lote : listaLotes) {
            // Si el código coincide, devuelve ese lote
            if (lote.getCodigoLote() == codigo) {
                return lote;
            }
        }
        // No se encontró ninguno
        return null;
    }
    // Cotiza un trabajo: valida que sea aceptable y lo devuelve para consultar costo y tiempo. NO registra nada ni toca los ingresos
    public Trabajo cotizacion(Trabajo trabajo){
        // Rechaza el trabajo si es nulo o incumple la regla de su categoría
        validarAceptacion(trabajo);
        // Devuelve el mismo trabajo sin registrarlo
        return trabajo;
    }

    // Registra un trabajo aceptado: lo deja en cola y cobra el monto completo. Los ingresos solo cambian si todas las validaciones pasan
    public void registrarTrabajo(Trabajo trabajo) {
        
        // Valida la regla de la categoría y que el código no esté repetido
        validarAceptacion(trabajo);
        validarCodigoNuevo(trabajo);
        
        // Calcula el cobro (cada categoría sabe cuánto cuesta)
        double monto = trabajo.calcularCosto();
       
        // Registra el trabajo (queda en cola)
        listaTrabajos.add(trabajo);
        // Suma el cobro al ingreso por registro
        ingresoRegistro += monto;
        // Suma el cobro al ingreso de su categoría
        sumarIngresoCategoria(trabajo.categoria(), monto);
    }

    // Carga un trabajo inicial de prueb, se registra en cola pero sin cobrar nada
    public void cargarTrabajoInicial(Trabajo trabajo) {
       // Aplica las mismas validaciones que un registro normal
        validarAceptacion(trabajo);
        validarCodigoNuevo(trabajo);
        // Agrega el trabajo sin tocar los ingresos
        listaTrabajos.add(trabajo);
        // Si la categoría aún no aparece en los ingresos, la agrega con valor 0
        if (!categoriasIngreso.contains(trabajo.categoria())) {
            categoriasIngreso.add(trabajo.categoria());
            valoresIngresoCategoria.add(0.0);
        }
    }
    // Registra un nodo nuevo; su código no puede repetirse
     public void registrarNodo(Nodo nodo) {
       // Rechaza nodos nulos o con código/capacidad no positivos
        if (nodo == null || nodo.getCodigoNodo() <= 0 || nodo.getCapacidadNodo() <= 0) {
            throw new IllegalArgumentException("El nodo requiere código y capacidad positivos.");
        }
        // Rechaza códigos de nodo repetidos
        if (buscarNodo(nodo.getCodigoNodo()) != null) {
            throw new IllegalArgumentException("Ya existe un nodo con el código " + nodo.getCodigoNodo() + ".");
        }
        // Agrega el nodo al inventario
        listaNodos.add(nodo);
    }

    // Crea un lote en preparación para el nodo indicado y ocupa el nodo
    public void crearLote(Nodo nodo) {
       // El nodo debe existir y estar registrado
        if (nodo == null || !listaNodos.contains(nodo)) {
            throw new IllegalArgumentException("El nodo indicado no está registrado.");
        }
         // Ocupa el nodo; si ya tenía un lote sin cerrar lanza excepción antes de crear nada
        nodo.ocupar();
         // Genera el siguiente código de lote
        contadorLotes++;
        // Crea el lote y lo guarda
        listaLotes.add(new Lote(contadorLotes, nodo));
    }

    // Asigna un trabajo en cola a un lote en preparación (las reglas de asignación las valida el lote)

    public void asignarTrabajo(Lote lote, Trabajo trabajo) {
        // Verifica que el lote y el trabajo estén registrados
        validarRegistrados(lote, trabajo);
        // El lote valida el estado y que el trabajo le pertenezca
        lote.asignar(trabajo);
    }

   
    public void retirarTrabajo(Lote lote, Trabajo trabajo) {
      
        validarRegistrados(lote, trabajo);
        lote.retirar(trabajo);
    }

     // Inicia un lote y registra aquí los cargos por reintento. El cargo se registra al iniciar el lote, no al asignar el trabajo
    public void iniciarLote(Lote lote) {

        // El lote debe existir y estar registrado
        if (lote == null || !listaLotes.contains(lote)) {
            throw new IllegalArgumentException("El lote indicado no está registrado.");
        }
         // Copia de los trabajos del lote
        ArrayList<Trabajo> trabajosLote = lote.getTrabajos();
        // Arreglo donde se guarda el cargo de cada trabajo antes de iniciar
        double[] cargos = new double[trabajosLote.size()];

        // Calcula el cargo por reintento de cada trabajo
        for (int i = 0; i < trabajosLote.size(); i++) {
            cargos[i] = trabajosLote.get(i).cargoReintento();
        }
       
        // Inicia el lote; si es inválido (vacío o ya iniciado) lanza excepción y no se cobra nada
        lote.iniciar();

        // Solo si el inicio fue exitoso, se registran los cargos
        for (int i = 0; i < trabajosLote.size(); i++) {

            //Solo se registran los trabajos que sí generaron cargo
            if (cargos[i] > 0) {
                // Suma el cargo al ingreso por reintentos
                ingresoReintentos += cargos[i];

                // Suma el cargo al ingreso de la categoría del trabajo
                sumarIngresoCategoria(trabajosLote.get(i).categoria(), cargos[i]);
            }
        }
    }

    // Registra el resultado de un trabajo dentro de un lote en ejecución
    public void registrarResultado(Lote lote, Trabajo trabajo, boolean completado) {
        
        // Verifica que el lote y el trabajo estén registrados
        validarRegistrados(lote, trabajo);
        // El lote valida y actualiza los estados (y se cierra si todos ya tienen resultado)
        lote.registrarResultado(trabajo, completado);
    }

    // Devuelve un texto con todos los trabajos registrados, uno por líne

    public String listarTrabajos() {

        // Si no hay trabajos, lo indica
        if (listaTrabajos.isEmpty()) {
            return "No hay trabajos registrados.";
        }

        // Texto acumulado
        String texto = "";

        // Agrega la descripción de cada trabajo en una línea
        for (Trabajo trabajo : listaTrabajos) {
            texto += trabajo + "\n";
        }

        // Devuelve el texto completo
        return texto;
    } 

    // Devuelve un texto con todos los nodos registrados, uno por línea
    public String listarNodos() {
    
        // Si no hay nodos, lo indica
        if (listaNodos.isEmpty()) {
            return "No hay nodos registrados.";
        }

        //Texto acumulado
        String texto = "";

        // Agrega la descripción de cada nodo en una línea
        for (Nodo nodo : listaNodos) {
            texto += nodo + "\n";
        }

        // Devuelve el texto completo
        return texto;
    }

    // Reporte, cuántos trabajos hay de cada categoría y en cada estado

    public String reporteTrabajosPorCategoriaYEstado() {

        // Si no hay trabajos, lo indica
        if (listaTrabajos.isEmpty()) {
            return "No hay trabajos registrados.";
        }

        //Lista de categorías existentes, sin repetie
        ArrayList<String> categorias = new ArrayList<String>();

        // Recorre los trabajos y agrega cada categoría la primera vez que aparece
        for (Trabajo trabajo : listaTrabajos) {
            if (!categorias.contains(trabajo.categoria())) {
                categorias.add(trabajo.categoria());
            }
        }   

        // Texto acumulado del reporte
        String texto = "";

        // Arma el detalle de cada categoría
        for (String categoria : categorias) {

            // Total de trabajos de esta categoría
            int total = 0;
            // Texto con el conteo por estado
            String detalle = "";
            // Cuenta los trabajos de la categoría en cada estado posible
            for (String estado : Trabajo.Estados) {

                //Contador de trabajos de esta categoría y este estado
                int cantidad = 0;

                // Recorre todos los trabajos y cuenta los que coinciden en categoría y estado
                for (Trabajo trabajo : listaTrabajos) {
                    if (trabajo.categoria().equals(categoria) && trabajo.getEstadoTrabajo().equals(estado)) {
                        cantidad++;
                    }
                }

                // Suma la cantidad al total de la categoría
                total += cantidad;
                //Agrega el estado y su cantidad al detalle
                detalle += estado + ": " + cantidad + "  ";
            }

            // Agrega la categoría con su total y su detalle al reporte
            texto += categoria + " (total " + total + ") \n (Detalle "+ detalle + ")";
        }

        // Devuelve el reporte completo
        return texto;
    
    
    }

    // Reporte: ingreso total, ingreso por registro, ingreso por reintentos e ingreso por categoría}
    public String reporteIngresos() {
        
        // Texto acumulado del reporte
        String texto = "";

        // Total = ingreso por registro + ingreso por reintentos
        texto += "Ingreso total: Q" + (ingresoRegistro + ingresoReintentos) + "\n";

        // Ingreso por registro de trabajos
        texto += "  Por registro: Q" + ingresoRegistro + "\n";

        // Ingreso por cargos de reintento
        texto += "  Por reintentos: Q" + ingresoReintentos + "\n";

        // Encabezado del detalle por categoría
        texto += "Ingreso por categoría:\n";

  
        //Agrega una línea por cada categoría con su ingreso acumulado
        for (int i = 0; i < categoriasIngreso.size(); i++) {
            texto += "  " + categoriasIngreso.get(i) + ": Q" + valoresIngresoCategoria.get(i) + "\n";
        }

        // Devuelve el reporte completo
        return texto;
    }

    // Reporte: lotes que no están cerrados, con su nodo, horas asignadas y capacidad disponible
    public String reporteLotesAbiertos() {
        
        // Texto acumulado del reporte
        String texto = "";

        // Recorre los lotes y agrega solo los que no están cerrados
        for (Lote lote : listaLotes) {
            if (!lote.estaCerrado()) {
                texto += lote + "\n";
            }
        }

         // Si no se agregó ningún lote, lo indica
        if (texto.length() == 0) {
            return "No hay lotes sin cerrar.";
        }

        // Devuelve el reporte completo

        return texto;
    }

    // Rechaza trabajos nulos o que incumplan la regla de su categoría (cada categoría decide qué es aceptable)
    private void validarAceptacion(Trabajo trabajo) {

        // No se puede validar un trabajo nulo
        if (trabajo == null) {
            throw new IllegalArgumentException("No se indicó ningún trabajo.");
        }

        // Si la validación de la categoría falla, rechaza el trabajo con el mensaje de su regla
        if (!trabajo.validacionDeAceptacion()) {
            throw new IllegalArgumentException("Trabajo rechazado: " + trabajo.descripcionRegla());
        }
    }

    // Rechaza trabajos cuyo código ya existe en el inventario
    private void validarCodigoNuevo(Trabajo trabajo) {

        // Si ya existe un trabajo con ese código, lo rechaza
        if (buscarTrabajo(trabajo.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un trabajo con el código " + trabajo.getCodigo() + ".");
        }
    }

    // Verifica que el lote y el trabajo indicados existan en el inventario
    private void validarRegistrados(Lote lote, Trabajo trabajo) {

        // El lote debe existir y estar registrado
        if (lote == null || !listaLotes.contains(lote)) {
            throw new IllegalArgumentException("El lote indicado no está registrado.");
        }

        // El trabajo debe existir y estar registrado
        if (trabajo == null || !listaTrabajos.contains(trabajo)) {
            throw new IllegalArgumentException("El trabajo indicado no está registrado.");
        }
    }

    // Suma un monto al ingreso acumulado de una categoría
    private void sumarIngresoCategoria(String categoria, double monto) {
        // Posición de la categoría en la lista (-1 significa que aún no está)
        int indice = -1;

        // Busca la categoría en la lista de categorías con ingresos
        for (int i = 0; i < categoriasIngreso.size(); i++) {
            if (categoriasIngreso.get(i).equals(categoria)) {
                // Guarda la posición encontrada y deja de buscar
                indice = i;
                break;
            }
        }

        // Si la categoría no estaba, la agrega con el monto como primer ingreso
        if (indice == -1) {
            categoriasIngreso.add(categoria);
            valoresIngresoCategoria.add(monto);
        } else {
            // Si ya estaba, toma lo acumulado y le suma el monto
            double ingresoActual = valoresIngresoCategoria.get(indice);
            valoresIngresoCategoria.set(indice, ingresoActual + monto);
        }
    }




    
}
