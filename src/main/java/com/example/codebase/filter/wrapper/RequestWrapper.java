package com.example.codebase.filter.wrapper;

import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RequestWrapper extends HttpServletRequestWrapper {

    private byte[] cachedInputStream;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedInputStream = StreamUtils.copyToByteArray(requestInputStream);
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {
            private InputStream cachedBodyInputStream = new ByteArrayInputStream(cachedInputStream);

            @Override
            public boolean isFinished() {
                try {
                    return cachedBodyInputStream.available() == 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() throws IOException {
                return cachedBodyInputStream.read();
            }
        };
    }
}
