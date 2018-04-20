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
package com.yanzhenjie.kalle.download;

import com.yanzhenjie.kalle.Response;
import com.yanzhenjie.kalle.connect.http.Call;

import java.io.IOException;

/**
 * Created by YanZhenjie on 2018/3/18.
 */
public class UrlWorker extends BasicWorker<UrlDownload>
{

    UrlWorker(UrlDownload download)
    {
        super(download);
    }

    @Override
    protected Response requestNetwork(UrlDownload download) throws IOException
    {
        return new Call(download).execute();
    }
}