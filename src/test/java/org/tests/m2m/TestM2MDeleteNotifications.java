package org.tests.m2m;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.tests.model.m2m.Notification;
import org.tests.model.m2m.NotificationTarget;
import org.tests.model.m2m.ReportResult;
import org.tests.model.m2m.Role;

import io.ebean.BaseTestCase;
import io.ebean.DB;

public class TestM2MDeleteNotifications extends BaseTestCase {
  
  @Test
  public void testWithDbDelete() {
    
    ReportResult report = createEntities();
    
    DB.delete(report);
    
    assertEquals(0, DB.find(ReportResult.class).findCount());
    assertEquals(1, DB.find(Notification.class).findCount());
    assertEquals(1, DB.find(NotificationTarget.class).findCount());
  }

  @Test
  public void testWithFindDelete() {
    
    ReportResult report = createEntities();
    
    List<Object> ids = Arrays.asList(report.getId());    
    DB.find(ReportResult.class).where().idIn(ids).delete();
    
    assertEquals(0, DB.find(ReportResult.class).findCount());
    assertEquals(1, DB.find(Notification.class).findCount());
    assertEquals(1, DB.find(NotificationTarget.class).findCount());
  }
  
  private ReportResult createEntities() {
    ReportResult report = new ReportResult();
    report.setName("new report");
    Role role = new Role("x");
    DB.save(role);
    report.getReadAuthorization().add(role);
    
    DB.save(report);
    
    NotificationTarget target = new NotificationTarget();
    target.setTargetUuid(report.getId());
    
    Notification notification = new Notification();
    notification.setTypeId(2);
    notification.setRefTableName("report_result");
    notification.getTargets().add(target);
    
    DB.save(notification);
    
    assertEquals(1, DB.find(ReportResult.class).findCount());
    assertEquals(1, DB.find(Notification.class).findCount());
    assertEquals(1, DB.find(NotificationTarget.class).findCount());
    
    report = DB.find(ReportResult.class).findOne();
    
    assertEquals(1, report.getNotifications().size());
    assertEquals("report_result",report.getNotifications().get(0).getRefTableName());
    return report;
  }
  
  @After
  public void restoreDatabase() {
    DB.find(ReportResult.class).delete();
    DB.find(Notification.class).delete();
    DB.find(NotificationTarget.class).delete();
    DB.find(Role.class).delete();
  }

}
