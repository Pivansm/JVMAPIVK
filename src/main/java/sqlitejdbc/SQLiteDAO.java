package sqlitejdbc;

import exporttxt.Records;
import exporttxt.TableRecordsAll;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static runner.Main.LOGGER;

public class SQLiteDAO extends AbstractDAO {
    private static final String SQL_CRT_TBL1 = "CREATE TABLE IF NOT EXISTS POSTVK(IDPOST INTEGER PRIMARY KEY, " +
            "FROMID INTEGER, CAPTVK VARCHAR(250), POSTTXT TEXT)";
    private static final String SQL_META_TBL = "SELECT * FROM POSTVK";
    private List<SetFields> fieldsList;

    public SQLiteDAO(Connection connection) {
        super(connection);
        fieldsList = new ArrayList<>();
    }

    @Override
    public void runnerToQuery(String query) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(query);
            st.execute();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void createTable(){
        //Первая таблица
        runnerToQuery(SQL_CRT_TBL1);
    }

    public String fieldsToSqlParameter() {

        PreparedStatement st = null;
        try
        {
            st = connection.prepareStatement(SQL_META_TBL);
            ResultSet rs = st.executeQuery();
            ResultSetMetaData metaData = st.getMetaData();
            List<String> stringList = new ArrayList<>();
            List<String> stringParam = new ArrayList<>();
            String sqlInz = "INSERT INTO POSTVK ";
            for(int i = 1, collCount = metaData.getColumnCount(); i <= collCount; i++) {
                stringList.add(metaData.getColumnName(i));
                stringParam.add("?");
                fieldsList.add(new SetFields(metaData.getColumnName(i), metaData.getColumnTypeName(i)));
            }
            sqlInz += "(" + String.join(",", stringList) + ") VALUES (" + String.join(",", stringParam) + ")";
            st.close();

            return sqlInz;
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
     }

     public void insertBatch(TableRecordsAll tbl, String insertQuery, int iBatch) {
            if(tbl != null) {
                PreparedStatement st = null;
                try {
                    st = connection.prepareStatement(insertQuery);
                    int countRow = 0;
                    for (Records row : tbl.getListRec()) {
                        for (int i = 0, collCount = fieldsList.size(); i < collCount; i++) {
                            SetFields field = fieldsList.get(i);
                            Object coll = null;
                            if (row.cellCount() > i)
                                coll = row.getCell(i);
                            if (coll == null) {
                                st.setNull(i + 1, Types.NULL);
                                continue;
                            }

                            if(field.getFieldType() == "VARCHAR") {
                                st.setString(i+1, coll.toString());
                            }
                            if(field.getFieldType().equals("INTEGER")) {
                                int ci = Integer.parseInt(coll.toString());
                                st.setInt(i+1, ci);
                            }
                            if(field.getFieldType().equals("TEXT")) {
                                st.setString(i+1, coll.toString());
                            }

                        }
                        //Added to Batch
                        st.addBatch();
                        //Сброс данных в БД
                        countRow++;
                        if(countRow % iBatch == 0) {
                            int[] updateBatch = st.executeBatch();
                            st.clearBatch();
                        }

                    }
                    //
                    int[] updateBatch = st.executeBatch();
                    st.clearBatch();
                    st.close();
                    System.out.println("Insert Batch finish!");

                } catch (SQLException e) {
                    e.printStackTrace();
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
     }

}
