/*
 * Copyright © 2018 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.kalle.request.body;

import android.text.TextUtils;

import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.Params;
import com.yanzhenjie.kalle.util.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import static com.yanzhenjie.kalle.Headers.VALUE_APPLICATION_FORM;

/**
 * Created by YanZhenjie on 2018/2/9.
 */
public class FormBody extends BasicOutData<FormBody> implements RequestBody
{
    private static final String BOUNDARY = createBoundary();
    private static final String NEXTLINE = "\r\n";

    private final Params mParams;
    private final Charset mCharset;
    private final String mContentType;

    private FormBody(Builder builder)
    {
        if (builder.mParams == null)
        {
            throw new NullPointerException("builder.mParams must not be null");
        }

        mParams = builder.mParams;
        mCharset = builder.mCharset == null ? Kalle.getConfig().getCharset() : builder.mCharset;
        mContentType = TextUtils.isEmpty(builder.mContentType) ? VALUE_APPLICATION_FORM : builder.mContentType;
    }

    @Override
    public long length()
    {
        final CounterStream stream = new CounterStream();
        try
        {
            onWrite(stream);
        } catch (IOException ignored)
        {
        }
        return stream.getLength();
    }

    @Override
    public String contentType()
    {
        return mContentType + "; boundary=" + BOUNDARY;
    }

    @Override
    protected void onWrite(OutputStream writer) throws IOException
    {
        final Set<String> setString = mParams.keySetString();
        for (String key : setString)
        {
            final String value = mParams.getString(key);
            writeFormString(writer, key, value);
            IOUtils.write(writer, NEXTLINE, mCharset);
        }

        final Set<String> setBinary = mParams.keySetBinary();
        for (String key : setBinary)
        {
            final List<Binary> values = mParams.getBinary(key);
            for (Binary value : values)
            {
                writeFormBinary(writer, key, value);
                IOUtils.write(writer, NEXTLINE, mCharset);
            }
        }

        IOUtils.write(writer, "--" + BOUNDARY + "--", mCharset);
    }

    private void writeFormString(OutputStream writer, String key, String value) throws IOException
    {
        IOUtils.write(writer, "--" + BOUNDARY, mCharset);
        IOUtils.write(writer, NEXTLINE, mCharset);
        IOUtils.write(writer, "Content-Disposition: form-data", mCharset);
        IOUtils.write(writer, "; name=\"" + key + "\"", mCharset);
        IOUtils.write(writer, NEXTLINE, mCharset);
        IOUtils.write(writer, NEXTLINE, mCharset);
        IOUtils.write(writer, value, mCharset);
    }

    private void writeFormBinary(OutputStream writer, String key, Binary value) throws IOException
    {
        IOUtils.write(writer, "--" + BOUNDARY, mCharset);
        IOUtils.write(writer, NEXTLINE, mCharset);
        IOUtils.write(writer, "Content-Disposition: form-data", mCharset);
        IOUtils.write(writer, "; name=\"" + key + "\"", mCharset);
        IOUtils.write(writer, "; filename=\"" + value.name() + "\"", mCharset);
        IOUtils.write(writer, NEXTLINE, mCharset);
        IOUtils.write(writer, "Content-Type: " + value.contentType(), mCharset);
        IOUtils.write(writer, NEXTLINE, mCharset);
        IOUtils.write(writer, NEXTLINE, mCharset);

        if (writer instanceof CounterStream)
        {
            ((CounterStream) writer).write(value.length());
        } else
        {
            value.writeTo(writer);
        }
    }

    private static String createBoundary()
    {
        StringBuilder sb = new StringBuilder("-------FormBoundary");
        for (int t = 1; t < 12; t++)
        {
            long time = System.currentTimeMillis() + t;
            if (time % 3L == 0L)
            {
                sb.append((char) (int) time % '\t');
            } else if (time % 3L == 1L)
            {
                sb.append((char) (int) (65L + time % 26L));
            } else
            {
                sb.append((char) (int) (97L + time % 26L));
            }
        }
        return sb.toString();
    }

    public static class Builder
    {
        private Charset mCharset;
        private String mContentType;
        private Params mParams;

        /**
         * Data charset.
         */
        public Builder charset(Charset charset)
        {
            this.mCharset = charset;
            return this;
        }

        /**
         * Content type.
         */
        public Builder contentType(String contentType)
        {
            this.mContentType = contentType;
            return this;
        }

        /**
         * Add parameters.
         */
        public Builder params(Params params)
        {
            mParams = params;
            return this;
        }

        public FormBody build()
        {
            return new FormBody(this);
        }
    }
}