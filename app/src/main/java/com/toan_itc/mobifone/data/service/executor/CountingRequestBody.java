package com.toan_itc.mobifone.data.service.executor;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Toan.IT
 * Created by vantoan on 3/2/17.
 * Email: huynhvantoan.itc@gmail.com
 */

public class CountingRequestBody extends RequestBody {

  protected RequestBody delegate;
  protected Listener listener;

  protected CountingSink countingSink;

  public CountingRequestBody(RequestBody delegate, Listener listener) {
    this.delegate = delegate;
    this.listener = listener;
  }

  @Override public MediaType contentType() {
    return delegate.contentType();
  }

  @Override public long contentLength() {
    try {
      return delegate.contentLength();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return -1;
  }

  @Override public void writeTo(BufferedSink sink) throws IOException {
    BufferedSink bufferedSink;

    countingSink = new CountingSink(sink);
    bufferedSink = Okio.buffer(countingSink);

    delegate.writeTo(bufferedSink);

    bufferedSink.flush();
  }

  protected final class CountingSink extends ForwardingSink {

    private long bytesWritten = 0;

    public CountingSink(Sink delegate) {
      super(delegate);
    }

    @Override public void write(Buffer source, long byteCount) throws IOException {
      super.write(source, byteCount);

      bytesWritten += byteCount;
      listener.onRequestProgress(bytesWritten, contentLength());
    }
  }

  public static interface Listener {

    public void onRequestProgress(long bytesWritten, long contentLength);
  }
}