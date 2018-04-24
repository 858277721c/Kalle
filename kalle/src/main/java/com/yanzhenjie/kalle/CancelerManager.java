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

import com.yanzhenjie.kalle.request.Request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by YanZhenjie on 2018/2/27.
 */
public class CancelerManager
{
    private final List<RequestInfo> listRequest = new ArrayList<>();

    /**
     * Add a task to cancel.
     *
     * @param request   target request.
     * @param canceller canceller.
     */
    public synchronized void addCancel(Request request, Canceller canceller)
    {
        if (request == null || canceller == null)
        {
            return;
        }
        listRequest.add(new RequestInfo(request, canceller));
    }

    /**
     * Remove a task.
     *
     * @param request target request.
     */
    public synchronized void removeCancel(Request request)
    {
        final Iterator<RequestInfo> it = listRequest.iterator();
        while (it.hasNext())
        {
            final RequestInfo item = it.next();
            if (item.request == request)
            {
                it.remove();
            }
        }
    }

    /**
     * According to the tag to cancel a task.
     *
     * @param tag tag.
     */
    public synchronized void cancel(Object tag)
    {
        for (RequestInfo item : listRequest)
        {
            final Object tagSaved = item.request.tag();
            if (tag == tagSaved || (tag != null && tagSaved != null && tag.equals(tagSaved)))
            {
                item.canceller.cancel();
            }
        }
    }

    private static class RequestInfo
    {
        private final Request request;
        private final Canceller canceller;

        private RequestInfo(Request request, Canceller canceller)
        {
            this.request = request;
            this.canceller = canceller;
        }
    }
}
