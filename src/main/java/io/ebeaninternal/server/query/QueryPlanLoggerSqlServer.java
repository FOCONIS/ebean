package io.ebeaninternal.server.query;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.ebeaninternal.server.type.DataBind;

/**
 * A QueryPlanlogger for sqlserver. It will return the plan as XML, which can be opened in
 * Microsoft SQL Server Management Studio.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class QueryPlanLoggerSqlServer extends QueryPlanLogger {

  @Override
  public void logQueryPlan(Connection conn, CQueryPlan plan, CQueryPredicates predicates)  {
    try (Statement stmt = conn.createStatement()) {
      stmt.execute("SET STATISTICS XML ON");
      StringBuilder sb = new StringBuilder();
      sb.append("SQL: ").append(plan.getSql()).append('\n');
      try (PreparedStatement explainStmt = conn.prepareStatement(plan.getSql())) {
        DataBind dataBind = plan.bindEncryptedProperties(explainStmt, conn);
        String bindLog = predicates.bind(dataBind);
        sb.append("Bindlog: ").append(bindLog).append('\n');

        try (ResultSet rset = explainStmt.executeQuery()) {
          // unfortunately, this will execute the
        }
        if (explainStmt.getMoreResults()) {
          try (ResultSet rset = explainStmt.getResultSet()) {
            while (rset.next()) {
              if (BASE_PATH == null || BASE_PATH.isEmpty()) {
                sb.append("XML: ").append(rset.getString(1));
              } else {
                try (OutputStream os = new FileOutputStream(getFilePrefix(plan.getSql()) + ".sqlplan")) {
                  os.write(rset.getString(1).getBytes(StandardCharsets.UTF_8));
                }
              }
            }
          }
        }
        queryplanLog.trace(sb.toString());

      } catch (SQLException | IOException e) {
        queryplanLog.error("Could not log query plan", e);
      } finally {
        stmt.execute("SET STATISTICS XML OFF");
      }
    } catch (SQLException e) {
      queryplanLog.error("Could not log query plan", e);
    }
  }

}
