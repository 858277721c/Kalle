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
package com.yanzhenjie.kalle;

import android.net.Uri;
import android.text.TextUtils;

import com.yanzhenjie.kalle.request.body.Binary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by YanZhenjie on 2018/2/23.
 */
public class Params
{
    private final Map<String, String> mMapString;
    private final Map<String, List<Binary>> mMapBinary;

    private Params(Builder builder)
    {
        mMapString = builder.mMapString;
        mMapBinary = builder.mMapBinary;
    }

    /**
     * Get a String.
     */
    public String getString(String key)
    {
        return mMapString.get(key);
    }

    /**
     * Get a Binary
     */
    public List<Binary> getBinary(String key)
    {
        return Collections.unmodifiableList(mMapBinary.get(key));
    }

    /**
     * Returns a {@link Set} view of String
     */
    public Set<String> keySetString()
    {
        return Collections.unmodifiableSet(mMapString.keySet());
    }

    /**
     * Returns a {@link Set} view of Binary
     */
    public Set<String> keySetBinary()
    {
        return Collections.unmodifiableSet(mMapBinary.keySet());
    }

    public int sizeString()
    {
        return mMapString.size();
    }

    public int sizeBinary()
    {
        return mMapBinary.size();
    }

    /**
     * ReBuilder.
     */
    public Builder builder()
    {
        return new Builder().putParams(this);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        for (String key : keySetString())
        {
            final String value = getString(key);
            final String valueEncode = Uri.encode(value);
            builder.append("&").append(key).append("=").append(valueEncode);
        }
        if (builder.length() > 0) builder.deleteCharAt(0);
        return builder.toString();
    }

    public static class Builder
    {
        private final Map<String, String> mMapString = new LinkedHashMap<>();
        private final Map<String, List<Binary>> mMapBinary = new LinkedHashMap<>();

        public Builder()
        {
        }

        public Builder putParams(Params params)
        {
            mMapString.putAll(params.mMapString);
            mMapBinary.putAll(params.mMapBinary);
            return this;
        }

        /**
         * Put a String. The entry will be removed if the value is null
         */
        public Builder putString(String key, String value)
        {
            if (!TextUtils.isEmpty(key))
            {
                if (value == null)
                {
                    mMapString.remove(key);
                } else
                {
                    mMapString.put(key, value);
                }
            }
            return this;
        }

        /**
         * Put a Binary. The entry will be removed if the value is null
         */
        public Builder putBinary(String key, Binary binary)
        {
            if (!TextUtils.isEmpty(key))
            {
                mMapBinary.remove(key);
                if (binary != null)
                {
                    final List<Binary> list = new ArrayList<>(1);
                    list.add(binary);
                    mMapBinary.put(key, list);
                }
            }
            return this;
        }

        /**
         * Add a Binary.
         */
        public Builder addBinary(String key, Binary binary)
        {
            if (!TextUtils.isEmpty(key))
            {
                if (binary != null)
                {
                    if (!mMapBinary.containsKey(key))
                    {
                        mMapBinary.put(key, new ArrayList<Binary>(1));
                    }
                    mMapBinary.get(key).add(binary);
                }
            }
            return this;
        }

        /**
         * Remove String parameters.
         */
        public Builder clearString()
        {
            mMapString.clear();
            return this;
        }

        /**
         * Remove Binary parameters.
         */
        public Builder clearBinary()
        {
            mMapBinary.clear();
            return this;
        }

        public Params build()
        {
            return new Params(this);
        }
    }
}