package org.zanata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.h2.tools.TriggerAdapter;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

public class H2TextFlowTargetHistoryTrigger extends TriggerAdapter
{

   private static Log log = Logging.getLog(H2TextFlowTargetHistoryTrigger.class);

   @Override
   public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException
   {
      log.debug("Executing HTextFlowTargetHistory trigger");

      int oldRev = oldRow.getInt("versionNum");
      int newRev = newRow.getInt("versionNum");
      if (oldRev != newRev)
      {

         log.debug("revision incremented from {0} to {1}. Executing trigger..", oldRev, newRev);

         PreparedStatement prep = conn.prepareStatement(
               "INSERT INTO HTextFlowTargetHistory " +
                     "(target_id,versionNum,content0,content1,content2,content3,content4,content5,lastChanged,last_modified_by_id,state,tf_revision) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

         prep.setObject(1, oldRow.getObject("id"));
         prep.setObject(2, oldRow.getObject("versionNum"));
         prep.setObject(3, oldRow.getObject("content0"));
         prep.setObject(4, oldRow.getObject("content1"));
         prep.setObject(5, oldRow.getObject("content2"));
         prep.setObject(6, oldRow.getObject("content3"));
         prep.setObject(7, oldRow.getObject("content4"));
         prep.setObject(8, oldRow.getObject("content5"));
         prep.setObject(9, oldRow.getObject("lastChanged"));
         prep.setObject(10, oldRow.getObject("last_modified_by_id"));
         prep.setObject(11, oldRow.getObject("state"));
         prep.setObject(12, oldRow.getObject("tf_revision"));
         prep.execute();
      }
      else
      {
         log.warn("HTextFlowTarget updated without incrementing revision... skipping trigger");
      }
   }
}
