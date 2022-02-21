package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.DB;
import io.ebean.config.dbplatform.h2.H2Platform;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.dbmigration.ddlgeneration.BaseDdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlOptions;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.PlatformDdlBuilder;
import io.ebeaninternal.dbmigration.model.CurrentModel;
import io.ebeaninternal.dbmigration.model.MConfiguration;
import io.ebeaninternal.dbmigration.model.ModelContainer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class H2HistoryDdlTest {

  @Test
  public void testRegenerateHistoryTriggers() throws Exception {

    SpiEbeanServer ebeanServer = (SpiEbeanServer) DB.getDefault();

    HistoryTableUpdate update = new HistoryTableUpdate("c_user");
    update.add(HistoryTableUpdate.Change.ADD, "one");
    update.add(HistoryTableUpdate.Change.DROP, "two");
    update.add(HistoryTableUpdate.Change.ALTER, "three");
    update.add(HistoryTableUpdate.Change.ALTER, "three");


    CurrentModel currentModel = new CurrentModel(ebeanServer);
    ModelContainer modelContainer = currentModel.read();
    DdlWrite write = new BaseDdlWrite(new MConfiguration(), modelContainer, new DdlOptions());

    H2Platform h2Platform = new H2Platform();
    PlatformDdl h2Ddl = PlatformDdlBuilder.create(h2Platform);
    h2Ddl.configure(ebeanServer.config());
    h2Ddl.regenerateHistoryTriggers(write, update);

    assertThat(write.applyHistoryView().isEmpty()).isFalse();
    assertThat(write.applyHistoryTrigger().isEmpty()).isFalse();
    assertThat(write.applyHistoryView().getBuffer())
      .contains("create view")
      .doesNotContain("create trigger");
    assertThat(write.applyHistoryTrigger().getBuffer())
      .contains("add one")
      .contains("create trigger")
      .doesNotContain("create view");
    assertThat(write.toString()).isEqualTo("-- drop dependencies\n" 
        + "drop view if exists c_user_with_history;\n"
        + "-- apply history view\n"
        + "create view c_user_with_history as select * from c_user union all select * from c_user_history;\n"
        + "\n"
        + "-- apply history trigger\n"
        + "-- changes: [add one, alter three, drop two]\n"
        // CHECKME: Should trigger be deleted earlier
        + "drop trigger c_user_history_upd;\n"
        + "create trigger c_user_history_upd before update,delete on c_user for each row call \"io.ebean.config.dbplatform.h2.H2HistoryTrigger\";\n");

  }
}
