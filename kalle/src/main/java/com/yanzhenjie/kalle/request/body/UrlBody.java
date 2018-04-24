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

import static com.yanzhenjie.kalle.Headers.VALUE_APPLICATION_URLENCODED;

/**
 * Created by YanZhenjie on 2018/2/24.
 */
public class UrlBody extends BasicOutData<StringBody> implements RequestBody
{
    private final Params mParams;
    private final Charset mCharset;
    private final String mContentType;

    private UrlBody(Builder builder)
    {
        if (builder.mParams == null)
        {
            throw new NullPointerException("builder.mParams must not be null");
        }

        mParams = builder.mParams;
        mCharset = builder.mCharset == null ? Kalle.getConfig().getCharset() : builder.mCharset;
        mContentType = TextUtils.isEmpty(builder.mContentType) ? VALUE_APPLICATION_URLENCODED : builder.mContentType;
    }

    @Override
    public long length()
    {
        final String body = mParams.toString();
        if (TextUtils.isEmpty(body)) return 0;
        return IOUtils.toByteArray(body, mCharset).length;
    }

    @Override
    public String contentType()
    {
        return mContentType;
    }

    @Override
    protected void onWrite(OutputStream writer) throws IOException
    {
        final String body = mParams.toString();
        IOUtils.write(writer, body, mCharset);
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
         * Add parameter.
         */
        public Builder params(Params params)
        {
            mParams = params;
            return this;
        }

        public UrlBody build()
        {
            return new UrlBody(this);
        }
    }
}