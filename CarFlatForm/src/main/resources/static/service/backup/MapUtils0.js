(function(c){
	 //暂时不需要。
	 var M_PI = 3.14159265358979324;
	    var a = 6378245.0;
	    var ee = 0.00669342162296594323;
	    var x_pi = M_PI * 3000.0 / 180.0;

	    function wgs2gcj_lat(x, y) {
	        var ret1 = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y  + 0.2 * Math.sqrt(Math.abs(x));
	        ret1 += (20.0 * Math.sin(6.0 * x * M_PI) + 20.0 * Math.sin(2.0 * x  * M_PI)) * 2.0 / 3.0;
	        ret1 += (20.0 * Math.sin(y * M_PI) + 40.0 * Math.sin(y / 3.0 * M_PI)) * 2.0 / 3.0;
	        ret1 += (160.0 * Math.sin(y / 12.0 * M_PI) + 320 * Math.sin(y * M_PI  / 30.0)) * 2.0 / 3.0;
	        return ret1;
	    }

	    function wgs2gcj_lng(x, y) {
	        var ret2 = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1  * Math.sqrt(Math.abs(x));
	        ret2 += (20.0 * Math.sin(6.0 * x * M_PI) + 20.0 * Math.sin(2.0 * x  * M_PI)) * 2.0 / 3.0;
	        ret2 += (20.0 * Math.sin(x * M_PI) + 40.0 * Math.sin(x / 3.0 * M_PI)) * 2.0 / 3.0;
	        ret2 += (150.0 * Math.sin(x / 12.0 * M_PI) + 300.0 * Math.sin(x / 30.0  * M_PI)) * 2.0 / 3.0;
	        return ret2;
	    }

	    function wgs2gcj(lat,lng) {
	        var poi2 = {};
	        var dLat = wgs2gcj_lat(lng - 105.0, lat - 35.0);
	        var dLon = wgs2gcj_lng(lng - 105.0, lat - 35.0);
	        var radLat = lat / 180.0 * M_PI;
	        var magic = Math.sin(radLat);
	        magic = 1 - ee * magic * magic;
	        var sqrtMagic = Math.sqrt(magic);
	        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * M_PI);
	        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * M_PI);
	        poi2.lat = lat + dLat;
	        poi2.lng = lng + dLon;
	        return poi2;
	    }


	//火星坐标转百度坐标
	function gcj2bd(lat,lng) {
		var poi2 = {};
		var x = lng, y = lat;
		var z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		var theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		poi2.lng = z * Math.cos(theta) + 0.0065;
		poi2.lat = z * Math.sin(theta) + 0.006;
		return poi2;
	}
	
	c.BaiduAPIMap=function(p){
		let p0 = wgs2gcj(p.lat,p.lng);
		let p1 = gcj2bd(p0.lat,p0.lng);
		return {
			lat:p1.lat,
			lng:p1.lng
		}
	}	
	
})(window || global);

