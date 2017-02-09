package io.ebeaninternal.server.transaction;

import io.ebeaninternal.api.SpiTransaction;
import io.ebeaninternal.util.JdbcClose;

import javax.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction factory when no multi-tenancy is used.
 */
class TransactionFactoryBasic extends TransactionFactory {

  private final DataSourceSupplier dataSourceSupplier;

  TransactionFactoryBasic(TransactionManager manager, DataSourceSupplier dataSourceSupplier) {
    super(manager);
    this.dataSourceSupplier = dataSourceSupplier;
  }

  @Override
  public SpiTransaction createQueryTransaction(Object tenantId) {
    return create(tenantId, false);
  }

  @Override
  public SpiTransaction createTransaction(Object tenantId, boolean explicit, int isolationLevel) {
    SpiTransaction t = create(tenantId, explicit);
    return setIsolationLevel(t, explicit, isolationLevel);
  }

  private SpiTransaction create(Object tenantId, boolean explicit) {
    Connection c = null;
    try {
      c = dataSourceSupplier.getConnection(tenantId);
      return manager.createTransaction(explicit, c, counter.incrementAndGet());

    } catch (PersistenceException ex) {
      JdbcClose.close(c);
      throw ex;

    } catch (SQLException ex) {
      throw new PersistenceException(ex);
    }
  }

}
