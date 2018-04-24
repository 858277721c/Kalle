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
package com.yanzhenjie.kalle.request;

import com.yanzhenjie.kalle.Headers;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.Params;
import com.yanzhenjie.kalle.request.body.RequestBody;
import com.yanzhenjie.kalle.RequestMethod;
import com.yanzhenjie.kalle.Url;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created in Nov 4, 2015 8:28:50 AM.
 */
public abstract class Request
{
    private final RequestMethod mMethod;
    private final Headers mHeaders;
    private final Url mUrl;
    private final Params mParams;

    private final Proxy mProxy;
    private final SSLSocketFactory mSSLSocketFactory;
    private final HostnameVerifier mHostnameVerifier;
    private final int mConnectTimeout;
    private final int mReadTimeout;
    private final Object mTag;

    protected <T extends Api<T>> Request(Api<T> api)
    {
        mMethod = api.mMethod;
        mHeaders = api.mHeaders;
        mUrl = api.mUrlBuilder.build();
        mParams = api.mParamsBuilder.build();

        mProxy = api.mProxy;
        mSSLSocketFactory = api.mSSLSocketFactory;
        mHostnameVerifier = api.mHostnameVerifier;
        mConnectTimeout = api.mConnectTimeout;
        mReadTimeout = api.mReadTimeout;
        mTag = api.mTag;
    }

    /**
     * Get url.
     */
    public Url url()
    {
        return mUrl;
    }

    /**
     * Get params.
     */
    public Params params()
    {
        return mParams;
    }

    /**
     * Get request body.
     */
    public abstract RequestBody body();

    /**
     * Get method.
     */
    public RequestMethod method()
    {
        return mMethod;
    }

    /**
     * Get headers.
     */
    public Headers headers()
    {
        return mHeaders;
    }

    /**
     * Get proxy server.
     */
    public Proxy proxy()
    {
        return mProxy;
    }

    /**
     * Get SSLSocketFactory.
     */
    public SSLSocketFactory sslSocketFactory()
    {
        return mSSLSocketFactory;
    }

    /**
     * Get the HostnameVerifier.
     */
    public HostnameVerifier hostnameVerifier()
    {
        return mHostnameVerifier;
    }

    /**
     * Get the connection timeout time, Unit is a millisecond.
     */
    public int connectTimeout()
    {
        return mConnectTimeout;
    }

    /**
     * Get the readResponse timeout time, Unit is a millisecond.
     */
    public int readTimeout()
    {
        return mReadTimeout;
    }

    /**
     * Get tag.
     */
    public Object tag()
    {
        return mTag;
    }

    public static abstract class Api<T extends Api<T>>
    {
        private final RequestMethod mMethod;
        private final Headers mHeaders = new Headers();
        private final Url.Builder mUrlBuilder;
        protected final Params.Builder mParamsBuilder;

        private Proxy mProxy = Kalle.getConfig().getProxy();
        private SSLSocketFactory mSSLSocketFactory = Kalle.getConfig().getSSLSocketFactory();
        private HostnameVerifier mHostnameVerifier = Kalle.getConfig().getHostnameVerifier();
        private int mConnectTimeout = Kalle.getConfig().getConnectTimeout();
        private int mReadTimeout = Kalle.getConfig().getReadTimeout();
        private Object mTag;

        protected Api(String url, RequestMethod method)
        {
            mUrlBuilder = new Url.Builder(url);
            mParamsBuilder = new Params.Builder();
            mMethod = method;
            mHeaders.add(Kalle.getConfig().getHeaders());
        }

        /**
         * Add a new header.
         */
        public T addHeader(String key, String value)
        {
            mHeaders.add(key, value);
            return (T) this;
        }

        /**
         * If the target key exists, replace it, if not, add.
         */
        public T setHeader(String key, String value)
        {
            mHeaders.set(key, value);
            return (T) this;
        }

        /**
         * Set headers.
         */
        public T setHeaders(Headers headers)
        {
            mHeaders.set(headers);
            return (T) this;
        }

        /**
         * Remove the key from the information.
         */
        public T removeHeader(String key)
        {
            mHeaders.remove(key);
            return (T) this;
        }

        /**
         * Remove all header.
         */
        public T clearHeaders()
        {
            mHeaders.clear();
            return (T) this;
        }

        /**
         * Overlay path.
         */
        public T addPath(String value)
        {
            mUrlBuilder.addPath(value);
            return (T) this;
        }

        public T putQuery(String key, String value)
        {
            mUrlBuilder.putQuery(key, value);
            return (T) this;
        }

        /**
         * Put a String parameters. The entry will be removed if the value is null
         */
        public abstract T putString(String key, String value);

        /**
         * Proxy information for this request.
         */
        public T proxy(Proxy proxy)
        {
            this.mProxy = proxy;
            return (T) this;
        }

        /**
         * SSLSocketFactory for this request.
         */
        public T sslSocketFactory(SSLSocketFactory sslSocketFactory)
        {
            this.mSSLSocketFactory = sslSocketFactory;
            return (T) this;
        }

        /**
         * HostnameVerifier for this request.
         */
        public T hostnameVerifier(HostnameVerifier hostnameVerifier)
        {
            this.mHostnameVerifier = hostnameVerifier;
            return (T) this;
        }

        /**
         * Connect timeout for this request.
         */
        public T connectTimeout(int timeout, TimeUnit timeUnit)
        {
            long time = timeUnit.toMillis(timeout);
            this.mConnectTimeout = (int) Math.min(time, Integer.MAX_VALUE);
            return (T) this;
        }

        /**
         * Read timeout for this request.
         */
        public T readTimeout(int timeout, TimeUnit timeUnit)
        {
            long time = timeUnit.toMillis(timeout);
            this.mConnectTimeout = (int) Math.min(time, Integer.MAX_VALUE);
            return (T) this;
        }

        /**
         * Tag.
         */
        public T tag(Object tag)
        {
            this.mTag = tag;
            return (T) this;
        }
    }
}