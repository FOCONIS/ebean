package io.ebean.event;

import java.util.List;

/**
 * An extended persistController, that allows to prepare the batches.
 * </p>
 */
public interface BeanPersistBatchController extends BeanPersistController {

  /**
   * Prior to the insert perform some action. You can modify the beans in this step.
   */
  default void preInsert(List<BeanPersistRequest<?>> requests) {
  }

  /**
   * Prior to the update perform some action. You can modify the beans in this step.
   */
  default void preUpdate(List<BeanPersistRequest<?>> requests) {
  }

  /**
   * Prior to the delete perform some action. You can modify the beans in this step.
   */
  default void preDelete(List<BeanPersistRequest<?>> requests) {
  }

  /**
   * Prior to a soft delete perform some action. You can modify the beans in this step.
   */
  default void preSoftDelete(List<BeanPersistRequest<?>> requests) {
  }

  /**
   * Prior to a delete by id perform some action.
   */
//  void preDelete(BeanDeleteIdRequest request);


}
