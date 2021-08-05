package org.tests.json;

import io.ebean.BaseTestCase;
import io.ebean.BeanState;
import io.ebean.DB;
import io.ebean.ValuePair;
import io.ebean.event.BeanPersistAdapter;
import io.ebean.event.BeanPersistRequest;
import io.ebeantest.LoggedSql;
import org.junit.Test;
import org.tests.model.json.EBasicJsonJackson3;
import org.tests.model.json.EBasicJsonList;
import org.tests.model.json.PlainBean;
import org.tests.model.json.PlainBeanDirtyAware;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDbJson_Jackson3 extends BaseTestCase {

  public static class EBasicJsonListPersistController extends BeanPersistAdapter {

    private static Map<String, ValuePair> updatedValues;

    @Override
    public boolean isRegisterFor(Class<?> cls) {
      return EBasicJsonList.class.isAssignableFrom(cls);
    }

    @Override
    public boolean preInsert(BeanPersistRequest<?> request) {
      updatedValues = request.getUpdatedValues();
      return true;
    }

    @Override
    public boolean preUpdate(BeanPersistRequest<?> request) {
      updatedValues = request.getUpdatedValues();
      return true;
    }
  }
  @Test
  public void updateIncludesJsonColumn_when_explicit_isMarkedDirty() {

    PlainBeanDirtyAware contentBean = new PlainBeanDirtyAware("a", 42);

    EBasicJsonJackson3 bean = new EBasicJsonJackson3();
    bean.setName("b1");
    bean.setPlainValue(contentBean);

    bean.save();

    final EBasicJsonJackson3 found = DB.find(EBasicJsonJackson3.class, bean.getId());
    found.setName("b1-mod");

    LoggedSql.start();
    found.save();
    expectedSql(0, "update ebasic_json_jackson3 set name=?, version=? where id=? and version=?");

    found.setName("b1-mod2");
    found.getPlainValue().setName("b");
    // found.getPlainValue().setMarkedDirty(true); // Irrelevant for SOURCE or HASH based mutation detection

    found.save();
    expectedSql(0, "update ebasic_json_jackson3 set name=?, plain_value=?, version=? where id=? and version=?");
    LoggedSql.stop();

    final EBasicJsonJackson3 found2 = DB.find(EBasicJsonJackson3.class, bean.getId());

    assertThat(found2.getName()).isEqualTo("b1-mod2");
    assertThat(found2.getPlainValue().getName()).isEqualTo("b");
  }

  @Test
  public void updateIncludesJsonColumn_when_loadedAndNotDirtyAware() {

    PlainBean contentBean = new PlainBean("a", 42);
    EBasicJsonList bean = new EBasicJsonList();
    bean.setName("p1");
    bean.setPlainBean(contentBean);
    bean.setBeanList(Arrays.asList(contentBean));

    DB.save(bean);
    final EBasicJsonList found = DB.find(EBasicJsonList.class, bean.getId());
    // json bean not modified but not aware
    // ideally don't load the json content if we are not going to modify it
    found.setName("p1-mod");
    found.setBeanList(null);

    BeanState state = DB.getBeanState(found);
    assertThat(state.getChangedProps()).containsExactly("name", "beanList");

    ValuePair pair = state.getDirtyValues().get("name");
    assertThat(pair.getNewValue()).isEqualTo("p1-mod");
    assertThat(pair.getOldValue()).isEqualTo("p1");

    pair = state.getDirtyValues().get("beanList");
    assertThat(pair.getNewValue()).isEqualTo(null);
    assertThat((List<PlainBean>)pair.getOldValue()).hasSize(1)
      .extracting(PlainBean::getName).containsExactly("a");


    LoggedSql.start();
    DB.save(found);

    // plain_bean=?, no longer included with MD5 dirty detection
    expectedSql(0, "update ebasic_json_list set name=?, bean_list=?, version=? where id=?");

    assertThat(new TreeMap<>(EBasicJsonListPersistController.updatedValues).entrySet())
      .extracting(Map.Entry::toString)
      .containsExactly("beanList=null,[name:a]","name=p1-mod,p1","version=2,1");

    assertThat(DB.getBeanState(found).isDirty()).isFalse();

    found.getPlainBean().setName("b");
    assertThat(DB.getBeanState(found).isDirty()).isTrue();

    state = DB.getBeanState(found);
    assertThat(state.getChangedProps()).containsExactly("plainBean");
    pair = state.getDirtyValues().get("plainBean");
    assertThat(pair.getNewValue()).hasToString("name:b");
    assertThat(pair.getOldValue()).hasToString("name:a");


    LoggedSql.start();
    DB.save(found);

    // plain_bean=?, no longer included with MD5 dirty detection
    expectedSql(0, "update ebasic_json_list set plain_bean=?, version=? where id=?");

    assertThat(EBasicJsonListPersistController.updatedValues.entrySet())
      .extracting(Map.Entry::toString)
      .containsExactly("plainBean=name:b,name:a", "version=3,2");

    LoggedSql.stop();
  }

  @Test
  public void updateIncludesJsonColumn_when_list_loadedAndNotDirtyAware() {

    PlainBean contentBean = new PlainBean("a", 42);
    EBasicJsonList bean = new EBasicJsonList();
    bean.setName("p1");
    bean.setPlainBean(contentBean);
    bean.setBeanList(Arrays.asList(contentBean));

    DB.save(bean);
    final EBasicJsonList found = DB.find(EBasicJsonList.class, bean.getId());
    found.getBeanList().get(0).setName("p1-mod");

    BeanState state = DB.getBeanState(found);
    assertThat(state.getChangedProps()).containsExactly("beanList");
  }

  @Test
  public void update_with_differentDbJsonSettings() {
    PlainBeanDirtyAware contentBean1 = new PlainBeanDirtyAware("x", 42);

    EBasicJsonJackson3 bean = new EBasicJsonJackson3();
    bean.setName("b1");
    bean.setPlainValue(contentBean1);

    BeanState state = DB.getBeanState(bean);
    // a new bean is not considered as dirty (thus have no changed props)
    assertThat(state.isDirty()).isFalse();
    assertThat(state.isNewOrDirty()).isTrue();
    assertThat(state.getChangedProps()).isEmpty();

    bean.save();

    bean = DB.find(EBasicJsonJackson3.class, bean.getId());
    state = DB.getBeanState(bean);
    // a fresh loaded bean is also not considered as dirty
    assertThat(state.isDirty()).isFalse();
    assertThat(state.isNewOrDirty()).isFalse();
    assertThat(state.getChangedProps()).isEmpty();

    bean.getPlainValue().setName("a"); // has SOURCE

    assertThat(state.isDirty()).isTrue();
    assertThat(state.getChangedProps()).containsExactly("plainValue");


    Map<String, ValuePair> dirtyValues = state.getDirtyValues();
    assertThat(dirtyValues).hasSize(1).containsKeys("plainValue");

    assertThat(dirtyValues.get("plainValue")).hasToString("name:a,name:x"); // SOURCE -> origValue present

    LoggedSql.start();
    bean.save();
    expectedSql(0, "update ebasic_json_jackson3 set plain_value=?, version=? where id=?");

  }

  private void expectedSql(int i, String s) {
    assertThat(LoggedSql.collect().get(i)).contains(s);
  }
}
