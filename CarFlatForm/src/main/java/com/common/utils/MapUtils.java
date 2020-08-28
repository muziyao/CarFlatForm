package com.common.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
	 private static final double M_PI = 3.14159265358979324;
	 private static final double  a = 6378245.0;
	 private static final double  ee = 0.00669342162296594323;
	 private static final double  x_pi = M_PI * 3000.0 / 180.0;
	 

	 private static double wgs2gcj_lat(double x, double y) {
		 double ret1 = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y  + 0.2 * Math.sqrt(Math.abs(x));
	        ret1 += (20.0 * Math.sin(6.0 * x * M_PI) + 20.0 * Math.sin(2.0 * x  * M_PI)) * 2.0 / 3.0;
	        ret1 += (20.0 * Math.sin(y * M_PI) + 40.0 * Math.sin(y / 3.0 * M_PI)) * 2.0 / 3.0;
	        ret1 += (160.0 * Math.sin(y / 12.0 * M_PI) + 320 * Math.sin(y * M_PI  / 30.0)) * 2.0 / 3.0;
	        return ret1;
	    }

	 private static double  wgs2gcj_lng(double x, double y) {
		 double ret2 = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1  * Math.sqrt(Math.abs(x));
	        ret2 += (20.0 * Math.sin(6.0 * x * M_PI) + 20.0 * Math.sin(2.0 * x  * M_PI)) * 2.0 / 3.0;
	        ret2 += (20.0 * Math.sin(x * M_PI) + 40.0 * Math.sin(x / 3.0 * M_PI)) * 2.0 / 3.0;
	        ret2 += (150.0 * Math.sin(x / 12.0 * M_PI) + 300.0 * Math.sin(x / 30.0  * M_PI)) * 2.0 / 3.0;
	        return ret2;
	    }

	 private static InnerPoint wgs2gcj(double lat,double lng) {
		 InnerPoint poi2 = new InnerPoint();
	     double dLat = wgs2gcj_lat(lng - 105.0, lat - 35.0);
	     double dLon = wgs2gcj_lng(lng - 105.0, lat - 35.0);
	     double radLat = lat / 180.0 * M_PI;
	     double magic = Math.sin(radLat);
	        magic = 1 - ee * magic * magic;
	      double sqrtMagic = Math.sqrt(magic);
	        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * M_PI);
	        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * M_PI);
	        poi2.lat = lat + dLat;
	        poi2.lng = lng + dLon;
	        return poi2;
	   }

	 
 
	 private static InnerPoint gcj2bd(double lat,double lng) {
		InnerPoint poi2 = new InnerPoint();
		double x = lng, y = lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		poi2.lng = z * Math.cos(theta) + 0.0065;
		poi2.lat = z * Math.sin(theta) + 0.006;
		return poi2;
	}

	private static  class InnerPoint{
		public double lat;
		public double lng;
		@Override
		public String toString() {
			return "InnerPoint [lat=" + lat + ", lng=" + lng + "]";
		}
		
		
	}
	/**
	 * 原始GPS坐标转换为百度地图显示用的坐标。
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static Map<String,String> toBMapPoint(String lat,String lng){
		 InnerPoint p0 = wgs2gcj(Double.parseDouble( lat),Double.parseDouble( lng ));
		 InnerPoint p1 = gcj2bd(p0.lat,p0.lng);
		 Map<String,String> map = new HashMap<String,String>();
		 map.put("lat", p1.lat+"");
		 map.put("lng", p1.lng+"");
		 return map;
	}
	
	public static String format( String  src ) {
		if( src !=null && src.length() !=0 ) {
			StringBuilder sb = new StringBuilder();
			sb.append(src);
			int index = sb.indexOf(".");
			if(index >= 2 ) {//12345.456
				//有小数点。
				sb.delete(index, index+1);
				sb.insert(index-2, '.');
				return sb.toString();
			}
		}
		return src;
	}
	
	public static void main(String[] args) {
		System.out.println( toBMapPoint("26.6005725", "107.9685006667") );
		//{lng=107.97907240875972, lat=26.60303494342427}
		System.out.println( toBMapPoint("26.60126733333", "107.9682558333") );
		//{lng=107.97882755954198, lat=26.60372482487645}
	}
}
