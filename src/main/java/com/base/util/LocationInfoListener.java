package com.base.util;

import com.baidu.location.BDLocation;

/**
 * Created by marson on 2016/4/23.
 */
public interface LocationInfoListener {
    public void onReceiveLocation(BDLocation location);
}
