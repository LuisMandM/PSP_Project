package iDAO;

import Models.Area;

import java.sql.SQLException;
import java.util.List;

public interface IAreaDao {
    Area findByName(String input) throws SQLException;

    List<Area> getAll();
}
