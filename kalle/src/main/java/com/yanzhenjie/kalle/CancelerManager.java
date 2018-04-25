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
    private final List<RequestInfo> mListRequest = new ArrayList<>();

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
        mListRequest.add(new RequestInfo(request, canceller));
    }

    /**
     * Remove a task.
     *
     * @param request target request.
     */
    public synchronized void removeCancel(final Request request)
    {
        final Iterator<RequestInfo> it = mListRequest.iterator();
        while (it.hasNext())
        {
            if (it.next().mRequest == request)
            {
                it.remove();
                break;
            }
        }
    }

    /**
     * According to the tag to cancel a task.
     *
     * @param tag tag.
     */
    public synchronized void cancel(final Object tag)
    {
        for (RequestInfo item : mListRequest)
        {
            final Object tagSaved = item.mRequest.tag();
            if (tag == tagSaved || (tag != null && tagSaved != null && tag.equals(tagSaved)))
            {
                item.mCanceller.cancel();
            }
        }
    }

    private static class RequestInfo
    {
        private final Request mRequest;
        private final Canceller mCanceller;

        private RequestInfo(Request request, Canceller canceller)
        {
            this.mRequest = request;
            this.mCanceller = canceller;
        }
    }
}
