package org.tests.model.virtualprop.ext;

import io.ebean.DB;
import io.ebean.Query;
import io.ebean.plugin.BeanType;
import io.ebean.plugin.Property;
import io.ebean.test.LoggedSql;
import io.ebeaninternal.server.deploy.BeanProperty;
import org.junit.jupiter.api.Test;
import org.tests.model.virtualprop.VirtualBase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Roland Praml, FOCONIS AG
 */
public class TestVirtualProps {

  @Test
  void testCreate() {

    DB.getDefault(); // Init database to start parser
    VirtualBase base = new VirtualBase();
    base.setData("Foo");

    DB.save(base);

    VirtualBase found = DB.find(VirtualBase.class).where().isNull("virtualExtendOne").findOne();
    assertThat(found).isNotNull();

    found = DB.find(VirtualBase.class).where().isNotNull("virtualExtendOne").findOne();
    assertThat(found).isNull();

    BeanType<VirtualBase> bt = DB.getDefault().pluginApi().beanType(VirtualBase.class);
    BeanProperty prop = (BeanProperty) bt.property("virtualExtendOne");


    found = DB.find(VirtualBase.class).where().isNull("virtualExtendOne").findOne();
    VirtualExtendOne ext = new VirtualExtendOne();
    ext.setData("bar");
    prop.pathSet(found, ext);
    DB.save(found);


    found = DB.find(VirtualBase.class).where().eq("virtualExtendOne.data", "bar").findOne();
    assertThat(found).isNotNull();

    List<Object> attr = DB.find(VirtualBase.class).fetch("virtualExtendOne", "data").findSingleAttributeList();
    assertThat(attr).containsExactly("bar");

    VirtualExtendOne  oneFound = (VirtualExtendOne) prop.pathGet(found);
    assertThat(oneFound.getData()).isEqualTo("bar");

    DB.delete(oneFound); // cleanup
  }

  @Test
  void testCreateMany() {

    DB.getDefault(); // Init database to start parser
    VirtualBase base1 = new VirtualBase();
    base1.setData("Foo");
    DB.save(base1);

    VirtualBase base2 = new VirtualBase();
    base2.setData("Bar");
    DB.save(base2);

    VirtualExtendManyToMany many1 = new VirtualExtendManyToMany();
    many1.setData("Alex");
    DB.save(many1);

    VirtualExtendManyToMany many2 = new VirtualExtendManyToMany();
    many2.setData("Roland");
    DB.save(many2);

    BeanType<VirtualBase> bt = DB.getDefault().pluginApi().beanType(VirtualBase.class);
    BeanProperty prop = (BeanProperty) bt.property("virtualExtendManyToManys");
    List<VirtualExtendManyToMany> list = (List<VirtualExtendManyToMany>) prop.pathGet(base1);
    assertThat(list).isEmpty();
    list.add(many1);
    LoggedSql.start();
    DB.save(base1);
    List<String> sql = LoggedSql.stop();
    assertThat(sql).hasSize(2);
    assertThat(sql.get(0)).contains("insert into virtual_extend_many_to_many_virtual_base (virtual_base_id, virtual_extend_many_to_many_id) values (?, ?)");
    assertThat(sql.get(1)).contains("-- bind");

    many2.getBases().add(base1);
    LoggedSql.start();
    DB.save(many2);
    sql = LoggedSql.stop();
    assertThat(sql).hasSize(2);


    LoggedSql.start();
    VirtualBase found = DB.find(VirtualBase.class, base1.getId());
    list = (List<VirtualExtendManyToMany>) prop.pathGet(found);
    assertThat(list).hasSize(2).containsExactlyInAnyOrder(many1, many2);
    sql = LoggedSql.stop();
    assertThat(sql).hasSize(2);
  }

  @Test
  void testCreateDelete()  {
    DB.getDefault(); // Init database to start parser
    VirtualBase base = new VirtualBase();
    base.setData("Master");
    DB.save(base);

    VirtualExtendOne extendOne = new VirtualExtendOne();
    extendOne.setBase(base);
    extendOne.setData("Extended");
    DB.save(extendOne);

    VirtualBase found = DB.find(VirtualBase.class, base.getId());

    LoggedSql.start();
    DB.delete(found);
    List<String> sql = LoggedSql.stop();
    assertThat(sql).hasSize(5);
    assertThat(sql.get(0)).contains("delete from virtual_extend_one where id = ?"); // delete OneToOne
    assertThat(sql.get(1)).contains("-- bind");
    assertThat(sql.get(2)).contains("delete from virtual_extend_many_to_many_virtual_base where virtual_base_id = ?"); // intersection table
    assertThat(sql.get(3)).contains("-- bind");
    assertThat(sql.get(4)).contains("delete from virtual_base where id=?"); // delete entity itself

  }
}
