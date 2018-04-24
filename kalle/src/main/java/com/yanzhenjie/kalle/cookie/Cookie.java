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
package com.yanzhenjie.kalle.cookie;

import android.text.TextUtils;

import java.io.Serializable;
import java.net.HttpCookie;

/**
 * <p>Cookie entity.</p>
 * Created in Dec 17, 2015 7:21:16 PM.
 */
public class Cookie implements Serializable
{
    private long id = -1;
    private String url;
    private String name;
    private String value;
    private String comment;
    private String commentURL;
    private boolean discard;
    private String domain;
    private long expiry;
    private String path;
    private String portList;
    private boolean secure;
    private int version = 1;

    public Cookie()
    {
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getCommentURL()
    {
        return commentURL;
    }

    public void setCommentURL(String commentURL)
    {
        this.commentURL = commentURL;
    }

    public boolean isDiscard()
    {
        return discard;
    }

    public void setDiscard(boolean discard)
    {
        this.discard = discard;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public long getExpiry()
    {
        return expiry;
    }

    public void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getPortList()
    {
        return portList;
    }

    public void setPortList(String portList)
    {
        this.portList = portList;
    }

    public boolean isSecure()
    {
        return secure;
    }

    public void setSecure(boolean secure)
    {
        this.secure = secure;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public void from(String url, HttpCookie httpCookie)
    {
        setUrl(url);
        setName(httpCookie.getName());
        setValue(httpCookie.getValue());
        setComment(httpCookie.getComment());
        setCommentURL(httpCookie.getCommentURL());
        setDiscard(httpCookie.getDiscard());
        setDomain(httpCookie.getDomain());

        final long maxAge = httpCookie.getMaxAge();
        if (maxAge == 0)
        {
            setExpiry(System.currentTimeMillis());
        } else if (maxAge > 0)
        {
            long expiry = (maxAge * 1000L) + System.currentTimeMillis();
            if (expiry < 0)
            {
                expiry = Long.MAX_VALUE;
            }
            setExpiry(expiry);
        } else
        {
            setExpiry(-1);
        }

        String path = httpCookie.getPath();
        if (!TextUtils.isEmpty(path) && path.length() > 1 && path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        setPath(path);
        setPortList(httpCookie.getPortlist());
        setSecure(httpCookie.getSecure());
        setVersion(httpCookie.getVersion());
    }

    public HttpCookie toHttpCookie()
    {
        final HttpCookie httpCookie = new HttpCookie(name, value);
        httpCookie.setComment(comment);
        httpCookie.setCommentURL(commentURL);
        httpCookie.setDiscard(discard);
        httpCookie.setDomain(domain);

        if (expiry == -1L)
        {
            httpCookie.setMaxAge(-1L);
        } else
        {
            final long leftSecond = (expiry - System.currentTimeMillis()) / 1000L;
            if (leftSecond > 0)
            {
                httpCookie.setMaxAge(leftSecond);
            } else
            {
                httpCookie.setMaxAge(0);
            }
        }

        httpCookie.setPath(path);
        httpCookie.setPortlist(portList);
        httpCookie.setSecure(secure);
        httpCookie.setVersion(version);
        return httpCookie;
    }

    public boolean hasExpired()
    {
        if (expiry == -1L)
        {
            return false;
        }
        return System.currentTimeMillis() > expiry;
    }
}
