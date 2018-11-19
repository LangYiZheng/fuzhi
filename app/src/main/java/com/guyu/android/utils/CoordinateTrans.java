package com.guyu.android.utils;

import com.esri.core.geometry.Point;

public class CoordinateTrans {
	 /// <summary>
    /// 经纬度转高斯坐标
    /// </summary>
    /// <param name="l">经度</param>
    /// <param name="b">纬度</param>
    /// <param name="x">x坐标</param>
    /// <param name="y">y坐标</param>
    /// <param name="threeBand">是否使用3度带</param>
    /// <param name="centerLongitude">中央经线</param>
    public static Point LB_To_XY80(double l, double b,  double x,  double y, boolean threeBand, double centerLongitude, boolean withNo)
    {
    	Point rtnPoint = new Point();
        double e2 = 0.006739501819473; //e2:第二偏心率，(aa*aa)/(bb*bb)-1
        double C = 6399596.65198801;   //C = aa*aa/bb
        double t;
        double ETsquare;
        double B_D;//纬度，用度表示
        double B_R;//纬度，用弧度表示
        double L_D;//经度，用度表示
        double L_R;//经度，用弧度表示　　　
        double N;
        double M;
        double L0;//经度与中央经线的夹角
        double RadianLength;//由赤道到纬度L处的子午线弧长
        //const double aa = 6378140;
        //const double bb = 6356755.28815753;
        double C0 = 111134.0047;
        double C1 = 32009.8575;
        double C2 = 133.9602;
        double C3 = 0.6976;
        double C4 = 0.0039;

        B_D = DegreeMintueSecordToDegree(b);
        B_R = DegreeToRadian(B_D);
        L_D = DegreeMintueSecordToDegree(l);
        L_R = DegreeToRadian(L_D);

        t = Math.tan(B_R);
        ETsquare = e2 * Math.cos(B_R) * Math.cos(B_R);
        N = C / Math.sqrt(1 + ETsquare);

        if (centerLongitude == 0)
            centerLongitude = GetCenterAngle(L_D, threeBand);   //获取中央经度

        L0 = L_D - centerLongitude;
        M = Math.cos(B_R) * DegreeToRadian(L0);

        RadianLength = C0 * B_D - Math.cos(B_R) * (C1 * Math.sin(B_R) + C2 * Math.pow(Math.sin(B_R), 3) + C3 * Math.pow(Math.sin(B_R), 5) + C4 * Math.pow(Math.sin(B_R), 7));

        y = RadianLength + N * t * (1 / 2.0 * Math.pow(M, 2) + 1 / 24.0 * (5 - t * t + 9 * ETsquare + 4 * ETsquare * ETsquare) * Math.pow(M, 4) + 1 / 720.0 * (6 * L_R - 58 * t * t + Math.pow(t, 4)) * Math.pow(M, 6));
        if (withNo)
            x = N * (M + 1 / 6.0 * (1 - t * t + ETsquare) * Math.pow(M, 3) + 1 / 120.0 * (5 - 18 * t * t + Math.pow(t, 4) + 14 * ETsquare - 58 * ETsquare * t * t) * Math.pow(M, 5)) + 500000;
        else
            x = N * (M + 1 / 6.0 * (1 - t * t + ETsquare) * Math.pow(M, 3) + 1 / 120.0 * (5 - 18 * t * t + Math.pow(t, 4) + 14 * ETsquare - 58 * ETsquare * t * t) * Math.pow(M, 5)) + GetAddYCoord(centerLongitude, threeBand);
        rtnPoint.setXY(x, y);
        return rtnPoint;
    }

    public static Point XY80_To_LB(double X, double Y,  double L,  double B, boolean ThreeBand, double Center_Longitude, boolean HasBandNo)
    {
    	Point rtnPoint = new Point();
        //const double aa = 6378140;
        //const double bb = 6356755.28815753;
        double C0 = 27.11162289465;
        double C1 = 9.02483657729;
        double C2 = 0.00579850656;
        double C3 = 0.000435400029;
        double C4 = 0.00004858357;
        double C5 = 0.00000215769;
        double C6 = 0.00000019404;

        double e2 = 0.006739501819473;   //e2:第二偏心率，(aa*aa)/(bb*bb)-1
        double C = 6399596.65198801;   //C = aa*aa/bb
        double nBand;   //所在带
        double B1_D, B1_R, B_D;
        double MX;   //Ｘ坐标（兆米）
        double T1;
        double ETsquare1;   //是η1
        double L_D;
        double N;

        if (Center_Longitude == 0)
        {
            if (ThreeBand)
                Center_Longitude = Math.floor(Y / 1000000) * 3;
            else
                Center_Longitude = Math.floor(Y / 1000000) * 6 - 3;
        }
        if (HasBandNo)
            nBand = GetBand(Center_Longitude, ThreeBand);
        else
            nBand = 0;

        Y = Y - nBand * 1000000 - 500000;
        MX = X / 1000000;

        B1_D = C0 + C1 * (MX - 3) - C2 * Math.pow(MX - 3, 2) - C3 * Math.pow(MX - 3, 3) + C4 * Math.pow(MX - 3, 4) + C5 * Math.pow(MX - 3, 5) + C6 * Math.pow(MX - 3, 6);
        B1_R = DegreeToRadian(B1_D);
        T1 = Math.tan(B1_R);

        ETsquare1 = e2 * Math.cos(B1_R) * Math.cos(B1_R);

        N = Y * Math.sqrt(1 + ETsquare1) / C;

        B_D = B1_D - (1 + ETsquare1) / Math.PI * T1 * (90 * N * N - 7.5 * (5 + 3 * T1 * T1 + ETsquare1 - 9 * ETsquare1 * T1 * T1) * Math.pow(N, 4) + 0.25 * (61 + 90 * T1 * T1 + 45 * Math.pow(T1, 4)) * Math.pow(N, 6));

        L_D = (180 * N - 30 * (1 + 2 * T1 * T1 + ETsquare1) * Math.pow(N, 3) + 1.5 * (5 + 28 * T1 * T1 + 24 * Math.pow(T1, 4)) * Math.pow(N, 5)) / (Math.PI * Math.cos(B1_R));
        L_D = L_D + Center_Longitude;

        B = B_D;
        L = L_D;
        rtnPoint.setXY(L, B);
        return rtnPoint;
    }

    //度转化为度分秒
    public static double DegreeToDegreeMintueSecond(double aDegree)
    {
        double D, M, S;
        //aDegree = Math.Round(aDegree,6);
        //D = Math.floor(aDegree);
        //M = Math.floor(Math.Round((aDegree - D) * 60, 4));
        //S = Math.Round(aDegree * 3600 - D * 3600 - M * 60, 2);
        //return 10000 * D + 100 * M + S;

        D = Math.floor(aDegree);
        M = Math.floor((aDegree - D) * 60);
        S = aDegree * 3600 - D * 3600 - M * 60;
        return 10000 * D + 100 * M + S;
    }

    /// <summary>
    /// 度分秒转化为度，度分秒DMS值1021245.34代表 102度12'45.34"
    /// </summary>
    public static double DegreeMintueSecordToDegree(double DMS)
    {
        double fD, fM, fS;
        fD = Math.floor(DMS / 10000);
        fM = Math.floor(DMS / 100) - 100 * fD;
        fS = DMS - fD * 10000 - 100 * fM;
        return fD + fM / 60 + fS / 3600;
    }
    //度数转为弧度
    public static double DegreeToRadian(double Degree)
    {
        return Degree * Math.PI / 180;
    }
    //根据所给经度计算中央经度, Longitude为度数，如102.5423
    public static double GetCenterAngle(double longitude, boolean threeband)
    {
        double nBand;
        if (threeband)
        {
            nBand = Math.floor((longitude + 1.50) / 3);
            return 3 * nBand;
        }
        else
        {
            nBand = Math.floor(longitude / 6) + 1;
            return 6 * nBand - 3;
        }
    }
    public static double GetBand(double Center_Longitude, boolean ThreeBand)
    {
        if (ThreeBand)
            return Math.floor(Center_Longitude / 3);
        else
            return Math.floor((Center_Longitude - 3) / 6 + 0.5);
    }
    //获得y坐标增加值
    public static double GetAddYCoord(double longitude, boolean threeBand)
    {
        double nBand;
        if (threeBand)
            nBand = Math.floor((longitude + 1.5) / 3);
        else
            nBand = Math.floor(longitude / 6) + 1;

        return nBand * 1000000 + 500000;
        //return 500000;
    }
    /// <summary>
    /// 从中央经线117转到118
    /// </summary>
    public static Point CoordTrans117To118(double srcX, double srcY,  double dstX,  double dstY)
    {
    	Point rtnPoint = new Point();
        double outL = 0, outB = 0;
        Point outPt = XY80_To_LB(srcY, srcX,  outL,  outB, true, 117,true);
        outL=outPt.getX();
        outB=outPt.getY();
        outL = DegreeToDegreeMintueSecond(outL);
        outB = DegreeToDegreeMintueSecond(outB);
        rtnPoint = LB_To_XY80(outL, outB,  dstX,  dstY, true, 118, true);     
        dstX = rtnPoint.getX();
        dstY = rtnPoint.getY();
        
//        dstX = Math.round(dstX, 3);
//        dstY = Math.round(dstY, 3);
        return rtnPoint;
    }
    /// <summary>
    /// 从中央经线118转到117
    /// </summary>
    public static Point CoordTrans118To117(double srcX, double srcY,  double dstX,  double dstY)
    {
    	Point rtnPoint = new Point();
        double outL = 0, outB = 0;
        XY80_To_LB(srcY, srcX,  outL,  outB, true, 118, false);
        outL = DegreeToDegreeMintueSecond(outL);
        outB = DegreeToDegreeMintueSecond(outB);
        rtnPoint = LB_To_XY80(outL, outB,  dstX,  dstY, true, 117, false);
//        dstX = Math.Round(dstX, 3);
//        dstY = Math.Round(dstY, 3);
        
        dstX = rtnPoint.getX();
        dstY = rtnPoint.getY();
        return rtnPoint;
    }

}
