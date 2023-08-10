package io.ebeaninternal.server.bind;

import io.ebeaninternal.server.bind.capture.BindCapture;
import io.ebeaninternal.server.bind.capture.BindCaptureStatement;
import io.ebeaninternal.server.core.timezone.DataTimeZone;

/**
 * Special DataBind used to capture bind values for obtaining explain plans.
 */
public final class DataBindCapture extends DataBind {

  private final BindCaptureStatement captureStatement;

  /**
   * Create given the dataTimeZone in use.
   */
  public static DataBindCapture of(DataTimeZone dataTimeZone, int maxStringSize) {
    return new DataBindCapture(dataTimeZone, new BindCaptureStatement(), maxStringSize);
  }

  private DataBindCapture(DataTimeZone dataTimeZone, BindCaptureStatement pstmt, int maxStringSize) {
    super(dataTimeZone, maxStringSize, pstmt, null);
    this.captureStatement = pstmt;
  }

  /**
   * Return the bind values capture used to obtain explain plans.
   */
  public BindCapture bindCapture() {
    return captureStatement.bindCapture();
  }

  @Override
  public void setArray(String arrayType, Object[] elements) {
    captureStatement.setArray(++pos, arrayType, elements);
  }

}
