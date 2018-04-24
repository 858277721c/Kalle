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
import android.webkit.URLUtil;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Add the mPath to the URL, such as:
 * <pre>
 * Url url = Url.newBuilder("http://www.example.com/xx")
 *      .scheme("https")
 *      .port(8080)
 *      .path("yy")
 *      .query("abc", "123")
 *      .setFragment("article")
 *      .send();
 * ...
 * The real url is: <code>https://www.example.com:8080/xx/yy?abc=123#article</code>.
 * </pre>
 * <pre>
 * Url url = Url.newBuilder("http://www.example.com/xx/yy?abc=123")
 *      .setPath("/aa/bb/cc")
 *      .setQuery("mln=456&ijk=789")
 *      .send();
 * ...
 * The real url is: <code>http://www.example.com/aa/bb/cc?mln=456&ijk=789</code>.
 * </pre>
 * <pre>
 * Url url = Url.newBuilder("http://www.example.com/user/photo/search?name=abc").build();
 * Url newUrl = url.location("../../get?name=mln");
 * ...
 * The new url is: <code>http://www.example.com/get?name=abc</code>.
 * </pre>
 * Created by YanZhenjie on 2018/2/9.
 */
public class Url
{
    private final String mScheme;
    private final String mHost;
    private final int mPort;
    private final List<String> mPath;
    private final Map<String, String> mQuery;
    private final String mFragment;

    private Url(Builder builder)
    {
        this.mScheme = builder.mScheme;
        this.mHost = builder.mHost;
        this.mPort = builder.mPort;
        this.mPath = builder.mPath;
        this.mQuery = builder.mQuery;
        this.mFragment = builder.mFragment;
    }

    public String getScheme()
    {
        return mScheme;
    }

    public String getHost()
    {
        return mHost;
    }

    public int getPort()
    {
        return mPort;
    }

    public String getPath()
    {
        return path(mPath);
    }

    public String getQuery()
    {
        return query(mQuery);
    }

    public String getFragment()
    {
        return mFragment;
    }

    public Builder builder()
    {
        return new Builder(toString());
    }

    @Override
    public String toString()
    {
        return mScheme + "://" + mHost + port(mPort) + path(mPath) + query(mQuery) + fragment(mFragment);
    }

    public Url location(String location)
    {
        if (TextUtils.isEmpty(location)) return null;

        if (URLUtil.isNetworkUrl(location))
        {
            return new Url.Builder(location).build();
        }

        URI newUri = URI.create(location);
        if (location.startsWith("/"))
        {
            return builder()
                    .setPath(newUri.getPath())
                    .setQuery(newUri.getQuery())
                    .setFragment(newUri.getFragment())
                    .build();
        } else if (location.contains("../"))
        {
            List<String> oldPathList = convertPath(getPath());
            List<String> newPathList = convertPath(newUri.getPath());

            int start = newPathList.lastIndexOf("..");
            newPathList = newPathList.subList(start + 1, newPathList.size());
            if (!oldPathList.isEmpty())
            {
                oldPathList = oldPathList.subList(0, oldPathList.size() - start - 2);
                oldPathList.addAll(newPathList);
                String path = TextUtils.join("/", oldPathList);
                return builder()
                        .setPath(path)
                        .setQuery(newUri.getQuery())
                        .setFragment(newUri.getFragment())
                        .build();
            }
            String path = TextUtils.join("/", newPathList);
            return builder()
                    .setPath(path)
                    .setQuery(newUri.getQuery())
                    .setFragment(newUri.getFragment())
                    .build();
        } else
        {
            List<String> oldPathList = convertPath(getPath());
            oldPathList.addAll(convertPath(newUri.getPath()));
            String path = TextUtils.join("/", oldPathList);
            return builder()
                    .setPath(path)
                    .setQuery(newUri.getQuery())
                    .setFragment(newUri.getFragment())
                    .build();
        }
    }

    public static class Builder
    {
        private String mScheme;
        private String mHost;
        private int mPort;
        private List<String> mPath;
        private Map<String, String> mQuery;
        private String mFragment;

        public Builder(String url)
        {
            final URI uri = URI.create(url);
            mScheme = uri.getScheme();
            mHost = uri.getHost();
            mPort = uri.getPort();
            mPath = convertPath(uri.getPath());
            mQuery = convertQuery(uri.getQuery());
            mFragment = uri.getFragment();
        }

        public Builder setScheme(String scheme)
        {
            this.mScheme = scheme;
            return this;
        }

        public Builder setHost(String host)
        {
            this.mHost = host;
            return this;
        }

        public Builder setPort(int port)
        {
            this.mPort = port;
            return this;
        }

        public Builder addPath(String path)
        {
            mPath.add(path);
            return this;
        }

        public Builder setPath(String path)
        {
            mPath = convertPath(path);
            return this;
        }

        public Builder putQuery(String key, String value)
        {
            if (!TextUtils.isEmpty(key))
            {
                if (value == null)
                {
                    mQuery.remove(key);
                } else
                {
                    mQuery.put(key, value);
                }
            }
            return this;
        }

        public Builder setQuery(String query)
        {
            mQuery = convertQuery(query);
            return this;
        }

        public Builder setFragment(String fragment)
        {
            this.mFragment = fragment;
            return this;
        }

        public Url build()
        {
            return new Url(this);
        }
    }

    private static List<String> convertPath(String path)
    {
        final List<String> pathList = new LinkedList<String>()
        {
            @Override
            public boolean add(String o)
            {
                return !TextUtils.isEmpty(o) && super.add(o);
            }

            @Override
            public void add(int index, String element)
            {
                if (!TextUtils.isEmpty(element))
                {
                    super.add(index, element);
                }
            }
        };
        if (!TextUtils.isEmpty(path))
        {
            while (path.startsWith("/"))
            {
                path = path.substring(1);
            }
            while (path.endsWith("/"))
            {
                path = path.substring(0, path.length() - 1);
            }

            final String[] arrPath = path.split("/");
            Collections.addAll(pathList, arrPath);
        }
        return pathList;
    }

    private static Map<String, String> convertQuery(String query)
    {
        final Map<String, String> map = new LinkedHashMap<>();

        if (!TextUtils.isEmpty(query))
        {
            final String[] arrGroup = query.split("&");

            String key = "";
            String value = "";
            for (String item : arrGroup)
            {
                final int end = item.indexOf("=");
                if (end > 0)
                {
                    key = item.substring(0, end);
                    if (end < item.length() - 1)
                    {
                        value = item.substring(end + 1, item.length());
                    }
                } else
                {
                    key = item;
                }
                map.put(key, value);
            }
        }
        return map;
    }

    private static String port(int port)
    {
        return port < 0 ? "" : String.format(Locale.getDefault(), ":%d", port);
    }

    private static String path(List<String> list)
    {
        if (list.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (String path : list)
        {
            builder.append("/").append(Uri.encode(path));
        }
        return builder.toString();
    }

    private static String query(Map<String, String> map)
    {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> item : map.entrySet())
        {
            final String key = item.getKey();
            final String value = item.getValue();
            final String valueEncode = Uri.encode(value);
            builder.append("&").append(key).append("=").append(valueEncode);
        }
        if (builder.length() > 0) builder.deleteCharAt(0);

        final String query = builder.toString();
        return TextUtils.isEmpty(query) ? "" : String.format("?%s", query);
    }

    private static String fragment(String fragment)
    {
        return TextUtils.isEmpty(fragment) ? "" : String.format("#%s", Uri.encode(fragment));
    }
}