package com.weavict.website.common

class GPSHelper
{

    /*
select lat,lng from gpsTable where (lat-34.12)*(lat-34.12)+(lng-113.123)*(lng-113.123)<=(0.01*5)*(0.01*5)
 34.12,113.123是中心点的gps位置。
5是公里数

select lat,lng from gpsTable where ABS(lat-34.12)+ABS(lng-113.123)<=(0.01*5)



指定一个经纬度，给定一个范围值(单位:千米)，查出在经纬度周围这个范围内的数据。
经度:113.914619
纬度:22.50128
范围:2km
longitude为数据表经度字段
latitude为数据表纬度字段
SQL在mysql下测试通过,其他数据库可能需要修改
SQL语句如下:
select * from location where sqrt(
    (
     ((113.914619-longitude)*PI()*12656*cos(((22.50128+latitude)/2)*PI()/180)/180)
     *
     ((113.914619-longitude)*PI()*12656*cos (((22.50128+latitude)/2)*PI()/180)/180)
    )
    +
    (
     ((22.50128-latitude)*PI()*12656/180)
     *
     ((22.50128-latitude)*PI()*12656/180)
    )
)<2

    */

    // 圆周率
    private static final double PI = 3.14159265358979324;
    // 赤道半径(单位m)
    private static final double EARTH_RADIUS = 6378137;

    /**
     * 转化为弧度(rad)
     * */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
    /**
     * 基于googleMap中的算法得到两经纬度之间的距离,
     * 计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
     * @param lon1 第一点的经度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的经度
     * @param lat3 第二点的纬度
     * @return 返回的距离，单位km ? m
     * */
    static double GetDistance(double lon1,double lat1,double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    static void main(args)
    {
        println GPSHelper.GetDistance(116.527113,39.936773,117.527113,39.936773);
    }
}
