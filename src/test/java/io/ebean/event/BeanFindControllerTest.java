package io.ebean.event;

import io.ebean.BaseTestCase;
import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.bean.BeanCollection;
import io.ebean.common.BeanList;
import io.ebean.config.ServerConfig;
import org.junit.Test;
import org.tests.example.ModUuidGenerator;
import org.tests.model.basic.EBasic;
import org.tests.model.basic.ECustomId;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BeanFindControllerTest extends BaseTestCase {

  @Test
  public void test() {

    ServerConfig config = new ServerConfig();

    config.setName("h2otherfind");
    config.loadFromProperties();
    config.setDdlGenerate(true);
    config.setDdlRun(true);
    config.setDdlExtra(false);

    config.setRegister(false);
    config.setDefaultServer(false);
    config.add(new ModUuidGenerator());
    config.getClasses().add(EBasic.class);
    config.getClasses().add(ECustomId.class);

    EBasicFindController findController = new EBasicFindController();
    config.getFindControllers().add(findController);

    EbeanServer ebeanServer = EbeanServerFactory.create(config);

    assertFalse(findController.calledInterceptFind);
    assertFalse(findController.calledPostProcess);
    ebeanServer.find(EBasic.class, 42);
    assertTrue(findController.calledInterceptFind);
    assertTrue(findController.calledPostProcess);
    assertFalse(findController.calledPostProcessMany);

    findController.calledInterceptFind = false;
    findController.calledPostProcess = false;
    ebeanServer.find(EBasic.class).findList();
    assertTrue(findController.calledInterceptFindMany);
    assertFalse(findController.calledPostProcess);
    assertTrue(findController.calledPostProcessMany);
    findController.calledInterceptFindMany = false;

    findController.findIntercept = true;
    EBasic eBasic = ebeanServer.find(EBasic.class, 42);

    assertEquals(Integer.valueOf(47), eBasic.getId());
    assertEquals("47", eBasic.getName());

    assertFalse(findController.calledInterceptFindMany);

    List<EBasic> list = ebeanServer.find(EBasic.class).where().eq("name", "AnInvalidNameSoEmpty").findList();
    assertEquals(0, list.size());
    assertTrue(findController.calledInterceptFindMany);

    findController.findManyIntercept = true;

    list = ebeanServer.find(EBasic.class).where().eq("name", "AnInvalidNameSoEmpty").findList();
    assertEquals(1, list.size());

    eBasic = list.get(0);
    assertEquals(Integer.valueOf(47), eBasic.getId());
    assertEquals("47", eBasic.getName());

    ECustomId bean = new ECustomId("check");
    ebeanServer.save(bean);
    assertNotNull(bean.getId());
  }

  static class EBasicFindController implements BeanFindController {

    boolean findIntercept;
    boolean findManyIntercept;
    boolean calledInterceptFind;
    boolean calledInterceptFindMany;
    boolean calledPostProcess;
    boolean calledPostProcessMany;


    @Override
    public boolean isRegisterFor(Class<?> cls) {
      return EBasic.class.equals(cls);
    }

    @Override
    public boolean isInterceptFind(BeanQueryRequest<?> request) {
      calledInterceptFind = true;
      return findIntercept;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T find(BeanQueryRequest<T> request) {
      return (T) createBean();
    }

    @Override
    public boolean isInterceptFindMany(BeanQueryRequest<?> request) {
      calledInterceptFindMany = true;
      return findManyIntercept;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> BeanCollection<T> findMany(BeanQueryRequest<T> request) {

      BeanList<T> list = new BeanList<>();
      list.add((T) createBean());
      return list;
    }

    @Override
    public <T> T postProcess(BeanQueryRequest<T> request, T result) {
      calledPostProcess = true;
      return result;
    }

    @Override
    public <T> BeanCollection<T> postProcessMany(BeanQueryRequest<T> request, BeanCollection<T> result) {
      calledPostProcessMany = true;
      return result;
    }
  }

  private static EBasic createBean() {
    EBasic b = new EBasic();
    b.setId(47);
    b.setName("47");
    return b;
  }
}
