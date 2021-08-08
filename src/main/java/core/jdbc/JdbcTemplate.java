package core.jdbc;

import next.dao.UserDao;
import next.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    public void executeUpdate(String sql, PreparedStatementSetter pss){
        try(Connection con = ConnectionManager.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)) {
            pss.setParameters(pstmt);
            pstmt.executeUpdate();
        }catch(SQLException e) {
            throw new DataAccessException(e);
        }
    }


    public void executeUpdate(String sql, Object... parameters){
        PreparedStatementSetter pss = createPreparedStatementSetter(parameters);
        executeUpdate(sql, pss);
    }

    public <T> T executeQuery(String sql,  RowMapper<T> rm, PreparedStatementSetter pss){
        List<T> list = list(sql, rm, pss);
        if(list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public <T> T executeQuery(String sql,  RowMapper<T> rm, Object... parameters){
        PreparedStatementSetter pss = createPreparedStatementSetter(parameters);
        return executeQuery(sql, rm, pss);
    }


    public <T> List<T> list(String sql, RowMapper<T> rm, PreparedStatementSetter pss){

        ResultSet rs = null;
        try(Connection con = ConnectionManager.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)) {

            pss.setParameters(pstmt);

            rs = pstmt.executeQuery();

            List<T> list = new ArrayList<>();
            while(rs.next()) {
                list.add(rm.mapRow(rs));
            }
            return list;

        }catch(SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public <T> List<T> list(String sql,  RowMapper<T> rm, Object... parameters){
        return list(sql, rm, createPreparedStatementSetter(parameters));
    }




    private PreparedStatementSetter createPreparedStatementSetter(Object[] parameters) {
        return new PreparedStatementSetter() {
            @Override
            public void setParameters(PreparedStatement pstmt) throws SQLException {
                for (int i = 0; i < parameters.length; i++) {
                    pstmt.setObject(i + 1, parameters[i]);
                }
            }
        };
    }



}
