package iDAO;

import Models.Incidencia;

import java.util.List;

public interface IIncidenciaDao {
    List<Incidencia> getByUser(Long user);

    List<Incidencia> getByArea(Long user);
}
