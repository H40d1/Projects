package series;

public class Main {

    // Si tienes InsertaTemporadas bien hecho puedes usar este:
    public static void testInserta() {
        // Crea el gestor de conexiones
        ConnectionManager cm = new ConnectionManager("alumno","bbdd-upm");

        // Crear las tareas
        // Cambia los archivos para hacer mas pruebas
        DataBaseTask[] tasks = {
            // Descomenta esta linea.
            // new InsertaTemporadas(), // O inserta directamente en el workbench
        };
        String[] data = { "data/temporadas.csv" };

        // Llamar a run:
        String ok = cm.runTask(tasks, data);
        System.out.println(ok);
    }

    public static void testActualiza() {
        // Crea el gestor de conexiones
        ConnectionManager cm = new ConnectionManager("alumno","bbdd-upm");

        // Crear las tareas
        // Cambia los archivos para hacer mas pruebas
        DataBaseTask[] tasks = {
            // new InsertaTemporadas(), // O inserta directamente en el workbench
            new ActualizaColumna(), // Modifica titulo en capitulo
        };
        String[] data = {
            "data/temporadas.csv",
            "" };

        // Llamar a run:
        String ok = cm.runTask(tasks, data);
        System.out.println(ok);
    }

    public static void testConsulta() {
        // Crea el gestor de conexiones
        ConnectionManager cm = new ConnectionManager("alumno","bbdd-upm");

        // Crear las tareas
        // Cambia los archivos para hacer mas pruebas
        DataBaseTask[] tasks = {
            new ConsultaTemporadas(), // Consulta todas
            new ConsultaTemporadasDeSerie(), // Solo las que cumplen patron
        };
        String[] data = {
            "",
            "%ficticio%" };

        // Llamar a run:
        String ok = cm.runTask(tasks, data);
        System.out.println(ok);

        // Comprobar los resultados:
        System.out.println("---- Todas las temporadas: ----");
        {
            @SuppressWarnings("unchecked") // Suprime warnings.
            ConsultaConResultado<Temporada> c = (ConsultaConResultado<Temporada>)tasks[0];
            System.out.println(c.get());
        }

        System.out.println("---- Temporadas con ficticio: ----");
        {
            @SuppressWarnings("unchecked") // Suprime warnings.
            ConsultaConResultado<Temporada> c = (ConsultaConResultado<Temporada>)tasks[1];
            System.out.println(c.get());
        }
    }

    public static void main(String[] args) {
        testActualiza();
        testInserta();
        testConsulta();
    }
}


