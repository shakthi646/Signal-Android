package org.thoughtcrime.securesms.jobs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.thoughtcrime.securesms.jobmanager.SafeData;
import org.thoughtcrime.securesms.logging.Log;

import org.thoughtcrime.securesms.jobmanager.JobParameters;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.whispersystems.libsignal.InvalidVersionException;
import org.whispersystems.signalservice.api.messages.SignalServiceEnvelope;

import java.io.IOException;

import androidx.work.Data;

public class PushContentReceiveJob extends PushReceivedJob {

  private static final long   serialVersionUID = 5685475456901715638L;
  private static final String TAG              = PushContentReceiveJob.class.getSimpleName();

  private static final String KEY_DATA = "data";

  private String data;

  public PushContentReceiveJob() {
    super(null);
  }

  public PushContentReceiveJob(Context context) {
    super(context);
    this.data = null;
  }

  public PushContentReceiveJob(Context context, String data) {
    super(context);
    this.data = data;
  }

  @WorkerThread
  @Override
  protected @NonNull JobParameters getJobParameters() {
    return JobParameters.newBuilder()
                        .withGroupId(PushContentReceiveJob.class.getSimpleName())
                        .create();
  }

  @Override
  protected void initialize(@NonNull SafeData data) {
    this.data = data.getNullableString(KEY_DATA);
  }

  @Override
  protected @NonNull Data serialize(@NonNull Data.Builder dataBuilder) {
    return dataBuilder.putString(KEY_DATA, data).build();
  }

  @Override
  public void onRun() {
    try {
      String                sessionKey = TextSecurePreferences.getSignalingKey(context);
      SignalServiceEnvelope envelope   = new SignalServiceEnvelope(data, sessionKey);

      handle(envelope);
    } catch (IOException | InvalidVersionException e) {
      Log.w(TAG, e);
    }
  }

  @Override
  public void onCanceled() {

  }

  @Override
  public boolean onShouldRetry(Exception exception) {
    return false;
  }
}
