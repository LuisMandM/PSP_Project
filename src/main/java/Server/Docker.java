package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Docker {

    /**
     * Metodo encargado de lanzar en Docker Compose correspondiente para el uso debido
     * de la aplicacion, se debe tener el archivo siempre alojado en la siguiente ruta
     * .\\Lanzadera\\docker-compose.yml
     *
     * @return True: el contenedor se monto correctamente, False: problemas con el Docker Compose
     */
    public static boolean DockerLauncher() {
        boolean result = false;
        String composeFilePath = ".\\Lanzadera\\docker-compose.yml";
        String[] command = {"docker-compose", "-f", composeFilePath, "up", "-d"};

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Docker Compose se ejecutó correctamente.");
                result = true;
            } else {
                System.err.println("Error al ejecutar Docker Compose. Código de salida: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al ejecutar Docker Compose");
        }
        return result;

    }
}
