package series;

public class Main {

    // Comprobar
    public static void test() {
        // Crea el gestor de conexiones
        ConnectionManager cm = new ConnectionManager("alumno","bbdd-upm");

        // Crear las tareas
        DataBaseTask[] tasks = {
            // Descomenta esta linea.
            new InsertaUsuarioConFoto(),
            new ConsultaTemporadasIncompletas()
        };
        String[] data = {
            "1,Juan,Boadilla,del Monte,data/foto.jpeg", "" };

        // Llamar a run:
        String ok = cm.runTask(tasks, data);
        System.out.println(ok);

        ConsultaTemporadasIncompletas c = (ConsultaTemporadasIncompletas) tasks[1];
        System.out.println(c.get());
    }

    public static void main(String[] args) {
        test();
    }
}


