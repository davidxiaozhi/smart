/*
 * Copyright (C) 2012-2013 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.si.jupiter.smart.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.util.HashedWheelTimer;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public final class SmartTimer extends  HashedWheelTimer  implements Closeable {
    /**
     * 我们这里把hashWheel 理解成一个表盘unit是分钟 表的精度就是5分钟 300s 表盘大小12 那么整个表盘表示的时间就是12*5=60 分钟
     * <p>
     * 下面我们介绍一下概念
     * <p>
     * 除了构造函数参数, 还有一个比较重要的概念, 轮(Round) :  一轮的时长为 tickDuration * ticksPerWheel, 也就是转一圈的时长.
     * 其中Worker线程为HashedWheelTimer的核心, 主要负责每过tickDuration时间就累加一次tick. 同时, 也负责执行到期的timeout任务,
     * 同时, 也负责添加timeou任务到指定的wheel中.当添加Timeout任务的时候, 会根据设置的时间, 来计算出需要等待的时间长度, 根据时间长度,
     * 进而算出要经过多少次tick, 然后根据tick的次数来算出经过多少轮, 最终得出task在wheel中的位置.
     * <p>
     * 例如, 如果任务设置为在100s后执行. 如果按照默认的HashedWheelTimer配置(tickDuration为100ms, wheel长为512)则:
     * 任务需要经过的tick数为: (100 * 1000) / 100 = 1000次 (等待时长 / tickDuration)
     * 任务需要经过的轮数为  : 1000次 / 512次/轮 = 1轮     (tick总次数 / ticksPerWheel)
     * 任务存放的wheel索引为 : 1000 - 512 = 488            (走完n轮时间后, 还要多少个tick)
     * 所以这里任务需要经过一轮后, 还要等待488次tick, 才会执行, 进而任务存放的wheel位置也就是488.
     *
     * @param prefix        前缀
     * @param tickDuration  //时间刻度之间的时长(默认100ms), 通俗的说, 就是多久tick++一次.
     * @param unit          //tickDuration的单位.
     * @param ticksPerWheel //类似于Clock中的wheel的长度(默认512).
     */
    public SmartTimer(String prefix, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        // Worker, 内部负责添加任务, 累加tick, 执行任务等.
        super(new ThreadFactoryBuilder().setNameFormat(prefix + "-timer-%s").setDaemon(true).build(),
                tickDuration,
                unit,
                ticksPerWheel);
    }

    public SmartTimer(String prefix) {
        this(prefix, 100, TimeUnit.MILLISECONDS, 512);
    }

    @PreDestroy
    public void close() {
        stop();
    }
}
