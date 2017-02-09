package io.ebeaninternal.server.cache;

import io.ebean.cache.ServerCacheFactory;
import io.ebean.cache.ServerCacheOptions;
import io.ebeaninternal.server.core.NoopTenantContext;

import org.tests.model.basic.Contact;
import org.tests.model.basic.Customer;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.junit.Assert.assertTrue;

public class DefaultServerCacheManagerTest {

  private String tenantId;

  private final ServerCacheFactory cacheFactory = new DefaultServerCacheFactory();

  private DefaultServerCacheManager manager = new DefaultServerCacheManager(true, new SingleTenantProv(), cacheFactory, new ServerCacheOptions(), new ServerCacheOptions());

  private DefaultServerCacheManager multiTenantManager = new DefaultServerCacheManager(true, new MultiTenantProv(), cacheFactory, new ServerCacheOptions(), new ServerCacheOptions());

  @Test
  public void getCache_normal() throws Exception {


    DefaultServerCache cache = cache(manager, Customer.class);
    assertThat(cache.getName()).isEqualTo("org.tests.model.basic.Customer_B");

    DefaultServerCache cache1 = cache(manager, Customer.class);
    assertThat(cache1).isSameAs(cache);

    DefaultServerCache cache2 = cache(manager, Contact.class);
    assertThat(cache1).isNotSameAs(cache2);
    assertThat(cache2.getName()).isEqualTo("org.tests.model.basic.Contact_B");


    DefaultServerCache natKeyCache = (DefaultServerCache) manager.getNaturalKeyCache(Customer.class);
    assertThat(natKeyCache.getName()).isEqualTo("org.tests.model.basic.Customer_N");

    DefaultServerCache queryCache = (DefaultServerCache) manager.getQueryCache(Customer.class);
    assertThat(queryCache.getName()).isEqualTo("org.tests.model.basic.Customer_Q");

    DefaultServerCache collCache = (DefaultServerCache) manager.getCollectionIdsCache(Customer.class, "contacts");
    assertThat(collCache.getName()).isEqualTo("org.tests.model.basic.Customer.contacts_C");
  }

  private DefaultServerCache cache(DefaultServerCacheManager manager, Class<?> beanType) {
    return (DefaultServerCache) manager.getBeanCache(beanType);
  }

  @Test
  public void getCache_multiTenant() throws Exception {

    tenantId = "ten1";
    DefaultServerCache cache = cache(multiTenantManager, Customer.class);
    assertThat(cache.getName()).isEqualTo("org.tests.model.basic.Customer_B");
    
    cache.put("1", "tenant1");

    tenantId = "ten2";
  
    assertThat(cache.get("1")).isNull();
    
  }

  @Test
  public void getCache_singleTenant() throws Exception {

    tenantId = "ten1";
    DefaultServerCache cache = cache(manager, Customer.class);
    assertThat(cache.getName()).isEqualTo("org.tests.model.basic.Customer_B");
    
    cache.put("1", "tenant1");

    tenantId = "ten2";
  
    assertThat(cache.get("1")).isEqualTo("tenant1");
    
  }
  
  @Test
  public void isLocalL2Caching() throws Exception {

    assertTrue(manager.isLocalL2Caching());
    assertTrue(multiTenantManager.isLocalL2Caching());
  }


  private class MultiTenantProv extends NoopTenantContext {

    @Override
    public boolean isMultiTenant() {
      return true;
    }

    @Override
    public Object getTenantId() {
      return tenantId;
    }
  }

  private class SingleTenantProv extends NoopTenantContext {

    @Override
    public boolean isMultiTenant() {
      return false;
    }

    @Override
    public Object getTenantId() {
      return tenantId;
    }
  }
}
