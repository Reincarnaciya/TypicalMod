package space.typro.typicalmod.server.database;

import space.typro.typicalmod.TypicalMod;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static space.typro.typicalmod.TypicalMod.LOGGER;

public class Table {
    private final Connection connection;
    private final String tblName;

    public Table(String tableName, Connection connection) throws SQLException {
        this.connection = connection;
        this.tblName = tableName;
        if (!tableExist()) {
            LOGGER.info("Table '" + tableName + "' not exist");
        }
    }


    private boolean tableExist() throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet tables = databaseMetaData.getTables(null, null, tblName, null);
        return tables.next();
    }

    /**
     * @param elements Передается хэш карта с массивами строк. Ключи - имя столбца, в котором смотрим. Значения - значения.
     *                 При разных размерах массивов выдаст SQLException.
     * @return Возвращает ResultSet - некий массив (или нет) ответов. В общем, все строки, которые совпали.
     * @throws SQLException любая ошибка связанная с БД. От неверного запроса до ошибки на серверах.
     */
    public ResultSet request(HashMap<String[], String[]> elements) throws SQLException {
        if (!tableExist()) throw new SQLException("Table not exist");
        String querry;

        querry = generateSelectSqlQuery(elements);
        PreparedStatement preparedStatement = connection.prepareStatement(querry);
        LOGGER.info("DB query = " + preparedStatement);
        return preparedStatement.executeQuery();
    }

    /**
     * @param elements Передается хэш карта с массивами строк. Ключи - столбцы, в которые добавляем. Значения - значения.
     *                 При разных размерах массивов выдаст SQLException.
     * @throws SQLException любая ошибка связанная с БД. От неверного запроса до ошибки на серверах.
     */
    public void addToTable(HashMap<String[], String[]> elements) throws SQLException {
        if (!tableExist()) throw new SQLException("Table not exist");
        PreparedStatement[] requests = new PreparedStatement[elements.size()];
        String querry;
        int i = 0;
        for (Map.Entry<String[], String[]> entry : elements.entrySet()) {
            querry = generateInsertSqlQuery(elements);
            PreparedStatement preparedStatement = connection.prepareStatement(querry);
            LOGGER.info("DB query = " + preparedStatement);
            requests[i] = preparedStatement;
            i++;
        }
        for (PreparedStatement statement : requests) {
            connection.createStatement().execute(statement.toString().substring(statement.toString().indexOf(":") + 2));
            statement.close();
        }
    }

    /**
     * @param field поле, которое обновляем
     * @param value значение, которое ставим
     * @param uuid  uuid пользователя, с которым работаем
     * @throws SQLException любая ошибка связанная с БД. От неверного запроса до ошибки на серверах.
     */
    public void updateTable(String field, String value, String uuid) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE " + tblName + " SET " + field + " = ? WHERE uuid = ?");
        statement.setString(1, value);
        statement.setString(2, uuid);
        statement.executeUpdate();
        statement.close();
    }
    /**
     * @param map Карта с тем, куда ставим (где ключ = значению)
     * @param value значение, которое ставим
     * @param field поле, которое выставляем
     * @throws SQLException любая ошибка связанная с БД. От неверного запроса до ошибки на серверах.
     */
    public void updateTableMass(HashMap<String[], String[]> map, String field, String value) throws SQLException {
        String stmt = generateUpdateSqlQuery(map, field, value);
        LOGGER.info("statementUPDATE = " + stmt);
        PreparedStatement statement = connection.prepareStatement(stmt);
        statement.executeUpdate();
        statement.close();
    }
    private String generateUpdateSqlQuery(HashMap<String[], String[]> map, String field, String value) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("UPDATE ").append(tblName).append(" SET ").append(field).append(" = '").append(value).append("'");


        sqlBuilder.append(" WHERE ");
        boolean isFirstEntry;
        isFirstEntry = true;
        for (Map.Entry<String[], String[]> entry : map.entrySet()) {
            String[] keyArray = entry.getKey();
            String[] valueArray = entry.getValue();
            if (keyArray.length == valueArray.length) {
                for (int i = 0; i < keyArray.length; i++) {
                    if (!isFirstEntry) {
                        sqlBuilder.append(" AND ");
                    }
                    sqlBuilder.append(keyArray[i]).append(" = '").append(valueArray[i]).append("'");
                    isFirstEntry = false;
                }
            } else {
                map.clear();
                throw new IllegalArgumentException("Key and value arrays must be of equal length");
            }
        }

        map.clear();
        return sqlBuilder.toString();
    }

    private String generateSelectSqlQuery(HashMap<String[], String[]> map) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM ").append(tblName).append(" WHERE ");

        for (Map.Entry<String[], String[]> entry : map.entrySet()) {
            String[] keyArray = entry.getKey();
            String[] valueArray = entry.getValue();
            if (keyArray.length == valueArray.length) {
                for (int i = 0; i < keyArray.length; i++) {
                    if (i > 0) {
                        sqlBuilder.append(" AND ");
                    }
                    sqlBuilder.append(keyArray[i]).append(" = '").append(valueArray[i]).append("'");
                }
            } else {
                map.clear();
                throw new IllegalArgumentException("Key and value arrays must be of equal length");
            }
        }
        map.clear();
        return sqlBuilder.toString();
    }

    private String generateInsertSqlQuery(HashMap<String[], String[]> data) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(tblName).append(" (");

        // Формирование части запроса с именами столбцов
        String[] keys = data.keySet().iterator().next();
        for (String key : keys) {
            sqlBuilder.append(key).append(", ");
        }
        sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length()); // Удаление последней запятой и пробела
        sqlBuilder.append(") VALUES ");

        // Формирование части запроса со значениями
        int rows = data.get(keys).length;
        sqlBuilder.append("(");
        for (int i = 0; i < rows; i++) {

            for (String[] values : data.values()) {
                sqlBuilder.append("'").append(values[i]).append("', ");
            }
            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length()); // Удаление последней запятой и пробела
            sqlBuilder.append(", ");
        }
        sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length()); // Удаление последней запятой и пробела
        sqlBuilder.append(")");

        return sqlBuilder.toString();
    }

    public boolean check(String uuid) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM " + tblName + " WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
