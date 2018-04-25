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

import android.text.TextUtils;

import com.yanzhenjie.kalle.RequestMethod;
import com.yanzhenjie.kalle.request.body.RequestBody;

/**
 * Created by YanZhenjie on 2018/2/13.
 */
public class UrlRequest extends Request
{
    protected UrlRequest(Api api)
    {
        super(api);
    }

    @Override
    public RequestBody body()
    {
        throw new AssertionError("It should not be called.");
    }

    public static class Api<T extends Api<T>> extends Request.Api<T>
    {
        protected Api(String url, RequestMethod method)
        {
            super(url, method);
        }

        @Override
        public T putString(String key, String value)
        {
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value))
            {
                putQuery(key, value);
            }
            return super.putString(key, value);
        }
    }

    public static class Builder extends Api<UrlRequest.Builder>
    {
        public Builder(String url, RequestMethod method)
        {
            super(url, method);
        }

        public UrlRequest build()
        {
            return new UrlRequest(this);
        }
    }
}