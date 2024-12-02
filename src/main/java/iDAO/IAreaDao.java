package iDAO;

import Models.Area;

import java.sql.SQLException;

public interface IAreaDao {
    Area findByName(String input) throws SQLException;
}
