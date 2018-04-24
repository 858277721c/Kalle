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

import com.yanzhenjie.kalle.request.body.FormBody;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.Params;
import com.yanzhenjie.kalle.request.body.RequestBody;
import com.yanzhenjie.kalle.RequestMethod;
import com.yanzhenjie.kalle.request.body.UrlBody;

/**
 * Created by YanZhenjie on 2018/2/13.
 */
public class BodyRequest extends Request
{
    private final RequestBody mBody;

    protected BodyRequest(Api api)
    {
        super(api);

        if (api.mBody != null)
        {
            mBody = api.mBody;
        } else
        {
            final Params params = params();
            if (params.sizeBinary() > 0)
            {
                mBody = new FormBody.Builder().params(params).build();
            } else
            {
                mBody = new UrlBody.Builder().params(params).build();
            }
        }
    }

    /**
     * Get the define body to send.
     */
    @Override
    public RequestBody body()
    {
        return mBody;
    }

    public static class Api<T extends Api<T>> extends Request.Api<T>
    {
        private RequestBody mBody;

        protected Api(String url, RequestMethod method)
        {
            super(url, method);
            mParamsBuilder.putParams(Kalle.getConfig().getParams());
        }

        /**
         * Set request body.
         */
        public T body(RequestBody body)
        {
            mBody = body;
            return (T) this;
        }

        @Override
        public T putString(String key, String value)
        {
            mParamsBuilder.putString(key, value);
            return (T) this;
        }

        public T putParams(Params params)
        {
            mParamsBuilder.putParams(params);
            return (T) this;
        }
    }

    public static class Builder extends BodyRequest.Api<BodyRequest.Builder>
    {
        public Builder(String url, RequestMethod method)
        {
            super(url, method);
        }

        public BodyRequest build()
        {
            return new BodyRequest(this);
        }
    }
}