function RouteService(){
	//所有数据
	let __alldata = {
		routepoints:[], //一维数组
		danger:[],//二维数组
		speedlimit:[],//二维数组。
		prohibitarea:[],//二维数组。
		parkingarea:[],//一维数组。
		recentindex:0,//gps最近和路线中距离最靠近的点的下标。
		gps:null,//实时gps坐标。
		wucha:0.000003,//误差
		offset_value:20,  //路线偏离的误差。默认是±20m。可由上游指定。
		max_speed:80
	};
	
	//工具
	let utils={
		//计算两点间的距离。使用两点距离公式。不考虑弧面。
		calcDistance(p1,p2){
			// c*c = | lng1 - lng2 |^2  + |lat1 - lat2|^2  
			//经纬度相差一度。就是111.11KM大约。
			try{
				let rs = Math.sqrt(
						  Math.abs( p1.lng - p2.lng )  * Math.abs( p1.lng - p2.lng ) 
						  + 
						 Math.abs( p1.lat - p2.lat ) * Math.abs( p1.lat - p2.lat )
				    ) * 111110;
				return Math.floor( rs );
			}catch(e){
				
			}
		},
		//初始化直线方程。
		initLineFunction( linepoint ){
			//直线垂直于X轴时，y = b 
			//直线不垂直于X轴时，y= kx + b
			//地图上的点(lng,lat)相当于点(x,y)
			let p1 = linepoint[0]; //上一个点(lng,lat)
			let p2 = null;//下一个点(lng,lat)
			for(let i=1;i<=linepoint.length;i++){
				if(i == linepoint.length ){
					p2 = linepoint[0];
				}else{
					p2 = linepoint[i];//下一个点(lng,lat)
				}
				//先判断斜率是否存在。垂直于x轴，也就是lng值在小数点后5位必须相同。误差值在(0.000001,0.000009)
				//还可以取小一点的值。0.000006左右。
				if( Math.abs( p1.lng - p2.lng) < __alldata.wucha ){
					p1.k_exist = false;//动态添加。
				}else {//斜率存在。
					let k = (p1.lat - p2.lat)/(p1.lng - p2.lng );
					//b = y-kx 。由于直线过点p1、p2，将p1或p2带入得: b = p1.lat - k * p1.lng;
					if(Math.abs(k) <= __alldata.wucha)k = 0;
					p1.k_exist = true;//动态添加。
					p1.k = k;
				}
				p1 = p2;
			}
			
		},
		/**
		 * 用当前的GPS坐标和区域坐标进行粗略判断。
		 * true 不可能相交。 false有可能相交。
		 * */
		exclude(linepoint,gps_point){
			let  left_count = 0;
			let  right_count = 0;
			let  up_count =0;
			let  down_count = 0;
			linepoint.map((p)=>{
				//全部在右边。
				if(p.lng >= gps_point.lng)right_count++;
				//全部在左边。
				if(p.lng <= gps_point.lng)left_count++;
				
				//全部在上边。
				if(p.lat >= gps_point.lat)up_count++;
				//全部在下边。
				if(p.lat <= gps_point.lat)down_count++;
			});
			if( right_count ==  linepoint.length )return false;
			if( left_count ==  linepoint.length )return false;
			if( up_count ==  linepoint.length )return false;
			if( down_count ==  linepoint.length )return false;
			
			return true;
			
		},
		isRepeat(p1,p2){
			let L_p1_p2_sqt =  Math.abs(p1.lat - p2.lat )* Math.abs(p1.lat - p2.lat )  + Math.abs(p1.lng - p2.lng )*Math.abs(p1.lng - p2.lng ) ;
			 L_p1_p2_sqt = utils.getFloat(Math.sqrt( L_p1_p2_sqt ),8);
			if(  Math.abs(L_p1_p2_sqt) <__alldata.wucha  ){
				return true;
			}else{
				return false;
			}
		},
	    judgeXiaoJiao(p1,p2,gps){
			if( gps.lat < p1.lat && gps.lat <p2.lat ){//
				return  null;
			}else{
				//  和  y=b这种情况。
				if( p1.k_exist ){//斜率存在。 用两点式来求。不用b。
					 //y-y1 = k(x - x1);
					 // x = (y-y1)/k + x1 
					let y = gps.lat;
					let x = ( y - p1.lat ) / p1.k + p1.lng;
					//判断点(x,y)是否在点p1(lng,lat)和点p2(lng,lat)的线段内。
					 //P1P2的线段长度的平方。
					 let L_p1_p2_sqt =  Math.abs(p1.lat - p2.lat )* Math.abs(p1.lat - p2.lat )  + Math.abs(p1.lng - p2.lng )*Math.abs(p1.lng - p2.lng ) ;
					 let L_p1_jd_sqt = Math.abs(p1.lat -y )*Math.abs(p1.lat -y )  +  Math.abs(p1.lng - x )*Math.abs(p1.lng - x );
					 let L_p2_jd_sqt = Math.abs(p2.lat -y )*Math.abs(p2.lat -y )  +   Math.abs(p2.lng - x )* Math.abs(p2.lng - x ) ;
					
					 L_p1_p2_sqt = utils.getFloat(Math.sqrt( L_p1_p2_sqt ),8);
					 L_p1_jd_sqt = utils.getFloat(Math.sqrt( L_p1_jd_sqt ),8);
					 L_p2_jd_sqt = utils.getFloat(Math.sqrt( L_p2_jd_sqt ),8);
					 
					 //未生效。
					if(  x<gps.lng //由于规定是向左的射线。所以交点必须要小于原点的x。
						 && L_p1_jd_sqt < L_p1_p2_sqt 
						 && L_p2_jd_sqt<  L_p1_p2_sqt
						 && Math.abs(  L_p1_p2_sqt - L_p1_jd_sqt - L_p2_jd_sqt ) <__alldata.wucha 
					    ){
						return  {lng:x,lat:y}
					}else{
						return  null;
					}
				}else{//斜率不存在。线段垂直于X轴，也就是垂直于维度。只需要判断y是否在其中即可。
					if( (gps.lng < p1.lng && gps.lng < p2.lng ) &&   //左射线判断。
						(gps.lat >p1.lat && gps.lat <p2.lat )|| (gps.lat >p2.lat && gps.lat <p1.lat ) 
												){
						return  {lng:p1.lng,lat:gps.lat} 
					}else{
						return null;
					}
				}
			}
		},
		getFloat(num,n){
			return num.toFixed(n);
		} 
	};
	 
	let init={
		load(data){
			init.initData(data);
			init.initRouteLineDistance();
			init.initDangerousArea();
			init.initProhibitArea();
			init.initSpeedLimitArea();
			init.initParkingArea();
		},
		//设置数据。
		initData(data){
			//防止上游不传，报错。
			if(!data.routepoints)data.routepoints=[];
			if(!data.danger)data.danger=[];
			if(!data.speedlimit)data.speedlimit=[];
			if(!data.prohibitarea)data.prohibitarea=[];
			if(!data.parkingarea)data.parkingarea=[];
			
		    //路线，一维数组处理。
			for(let i=0;i<data.routepoints.length;i++){
				let item = data.routepoints[i];
				item.lat = Number( item.lat );
				item.lng = Number( item.lng );
			}
			//危险区域。  二维数组
			for(let i=0;i<data.danger.length;i++){
				for(let j=0;j<data.danger[i].length;j++){
					let item = data.danger[i][j];
					item.lat = Number( item.lat );
					item.lng = Number( item.lng );
				}
			}
			//限速区域。  二维数组   [   [{},{}] ]]
			for(let i=0;i<data.speedlimit.length;i++){
				let rowdata = data.speedlimit[i];
				rowdata.limitspeed = Number( rowdata.limitspeed );
				for(let j=0;j<rowdata.points.length;j++){
					let item = rowdata.points[j];
					item.lat = Number( item.lat );
					item.lng = Number( item.lng );
				}
			}
			
			//禁止区域。  二维数组   [   [{},{}] ]]
			for(let i=0;i<data.prohibitarea.length;i++){
				for(let j=0;j<data.prohibitarea[i].length;j++){
					let item = data.prohibitarea[i][j];
					item.lat = Number( item.lat );
					item.lng = Number( item.lng );
				}
			}
			
			//停靠区域。一维数组。 
			for(let i =0; i < data.parkingarea.length; i++){
				let item = data.parkingarea[i];
				item.lat = Number( item.lat );
				item.lng = Number( item.lng );
			}
		 
			
			__alldata.routepoints = data.routepoints;
			__alldata.danger = data.danger;
			__alldata.speedlimit = data.speedlimit;
			__alldata.prohibitarea = data.prohibitarea;
			__alldata.parkingarea = data.parkingarea;
			__alldata.offset_value = Number(data.offset_value);//偏离值。
			__alldata.max_speed = Number( data.max_speed);//全程最大限速。
			
			if(!__alldata.routepoints)__alldata.routepoints=[];
			if(!__alldata.danger)__alldata.danger=[];
			if(!__alldata.speedlimit)__alldata.speedlimit=[];
			if(!__alldata.prohibitarea)__alldata.prohibitarea=[];
			if(!__alldata.parkingarea)__alldata.parkingarea=[];
		},
		//初始路线所有相邻两点间的距离。
		initRouteLineDistance(){
			//1计算路线两点间的距离。
			 for(let i=0 ; i <  __alldata.routepoints.length - 1 ; i++){
				 let p1 =  __alldata.routepoints[i];
				 let p2 = __alldata.routepoints[i+1];
				 let length = utils.calcDistance(p1, p2);
				 p1.length = length;
			 }
		},
		//初始化所有危险区域的参数。
		initDangerousArea(){
			//1 计算斜率
			 for(let i=0;i<__alldata.danger.length;i++){
				 let areapoints = __alldata.danger[i];
				 utils.initLineFunction( areapoints);
			 }
		},
		//初始化禁止区域参数。
		initProhibitArea(){
			//1 计算斜率
			 for(let i=0;i<__alldata.prohibitarea.length;i++){
				 let areapoints = __alldata.prohibitarea[i];
				 utils.initLineFunction( areapoints);
			 }
		},
		//初始化限速区域参数。
		initSpeedLimitArea(){
			//1 计算斜率
			 for(let i=0;i<__alldata.speedlimit.length;i++){
				 let areapoints = __alldata.speedlimit[i];
				 utils.initLineFunction( areapoints.points);
			 }
		},
		//初始化停靠区域。
		initParkingArea(){
			//把开始坐标加头，把目的地加进尾部。
			__alldata.parkingarea.unshift( __alldata.routepoints[0] );
			__alldata.parkingarea.push( __alldata.routepoints[ __alldata.routepoints.length - 1  ]);
			//初始化停停靠站点范围。默认250半径的圆。
			__alldata.parkingarea.map((point)=>{
				if(!point.offset ){//如果未定义。
					point.offset = 250;//默认以250米为半径作为停靠站点。
				}
			});
		}
	}
	
	let run={
		updateGPS(gpsinfo){
			__alldata.gps ={
				lat:Number(gpsinfo.lat),
				lng:Number(gpsinfo.lng),
				speed:Number(gpsinfo.speed)
			};
		},
		execute(gpsinfo){
			let data = {
				offset:false,//是否路线偏移。
				parking:false, //是否是停靠站点。
				reachingdestionation:false,//是否到达目的地。
				danger:false, //是否进入危险区域。
				limitspeed:false, //是否进入限速区域。
				prohibit:false //是否进入禁止区域。
			};
			//1 如果gps没有值。
			run.updateGPS(gpsinfo);
			
			//2 停靠站点分析。
			let rs0 = run.analysizParkingArea(data);
			data.parking = rs0;
			
			//3 路线偏离分析。
			let rs1 = run.analysizOffset();
			data.offset = rs1;
			
			//4 危险区域电子围栏分析。
			let rs2 = run.analysizDangerArea();
			data.danger = rs2;
			
			//5 限速区域电子围栏分析。 
			let rs3 = run.analysizSpeedLimitArea();
			data.limitspeed = rs3;
			
			//6 禁止区域电子围栏分析。 
			let rs4 = run.analysizProhibitArea();
			data.prohibit = rs4;
			
			return data;
		},
		//路线偏离分析。
		analysizOffset(){
			let rs = run.executeAnalysizOffset();
			if(rs.code =="offset")return true;
			return false;
		},
		//路线偏离分析。
		executeAnalysizOffset(){
			let rs = run.getSibling( );
			if( rs.code =="normal"){
				__alldata.recentindex = rs.index;
				return rs;
			}else if( rs.code =="offset" ){//只要偏离路线。index归零
				//偏离路线之后，需要再重新计算一次。只有这次偏离了。才是真的偏离。
				if( __alldata.recentindex -5 > 0 ){
					__alldata.recentindex -= 5;
					 //要是累计进入3次。需要清零了。
				}else{
					__alldata.recentindex = 0;
				}
				rs = run.getSibling( );
				if( rs.code =="normal"){
					__alldata.recentindex = rs.index;
				}else if( rs.code =="offset" ){
					//路线偏离。
				}
				return rs;
			}
		},
		//获取最近的点的下标。
		getSibling(){
			for( let i = __alldata.recentindex ; i < __alldata.routepoints.length-1;i++ ){
				let pre_pot =  __alldata.routepoints[i]; //上一个点的坐标。
				let next_pot =  __alldata.routepoints[i +1 ];//下一个点的坐标。
				//计算三角形的三条边长。
				let c = __alldata.routepoints[i].length;//取出该两点之间的距离。由于采集点是固定的。所以初始化时就可以算出固定值。
				let a = utils.calcDistance( pre_pot , __alldata.gps );//由当前点和gps坐标点算出距离。
				let b =  utils.calcDistance( next_pot , __alldata.gps );//由下一个点和gps坐标点算出距离。
				//特殊情况。
				if( Math.abs( c - a -b ) <=2 ){//误差为4   几乎可以认为重合在一起。
					return{
						code:"normal",
						index:i,
						offset:"<=20米"
					}
				}
				let P = (a + b + c )/2;//海伦公式中的P。
				let S = Math.sqrt( P*(P-a)*(P-b)*(P-c)); //面积。海伦公式。
				let Hx = 2*S / c; //
				let r1 = (c*c +  b*b >= a*a );
				let r2 = (c*c +  a*a >= b*b ); 
				if(r1 && r2 && Hx<= __alldata.offset_value ){//2个条件。1：是锐角三角形。2高必须少于40m。
					return{
						code:"normal",
						index:i,
						offset:Hx
					}
				}
			}
			return {
				code:"offset"
			}
		},
		//区域包含分析。 true gps_point点未进入该区域。false gps_point点进入该区域
		analysizArea( linepoint,gps_point){
			//遍历所有的直线方程。判断点的个数为基数还是偶数。
			//为简单这里假设只有一个区域。
			//当前gps_point(lng,lat)直线方程为： y = lat 
			//判断的方法： 沿着gps点向左作一条射线，判断射线于所有直线方程围成的多边形产生的交点的个数。
			//如果为奇数个，表示点被包围。否则不被包围。其中要求：交点不包含多边形的顶点。
			//算法步骤如下：
			//1先判断是否能相交。
				//1.1 如果不能相交，则跳过。
				//1.2 如果可以相交，求交点。
					//1.2.1 如果交点在线段外，则交点无效。
					//1.2.2 如果交点在线段内，则交点有效。
				//1.3 如果交代有效，判断交点是否是多边形的顶点之一。判断顶点的允许误差为  。
					//1.3.1 如果不是顶点，则交代有效。
					//1.3.2 如果是顶点，则交点无效。
			//2 如果交点有效，并符合。计数器+1。
			//3 循环结束判断计数器的奇偶。
			let count = 0;
			let p1 = linepoint[ 0];
			let p2 = null;
			for(let i=1; i<= linepoint.length;i++){
				if( i == linepoint.length){
					 p2 = linepoint[0];
				}else{
					 p2 = linepoint[i];
				}
				let rs_pointer = utils.judgeXiaoJiao(p1,p2,gps_point);
			    if(rs_pointer){//相交。
					//顶点判断。遍历当前区域。只有当前焦点和所有区域内的点，都不重合的时候。才能算是非顶点。
					//之所有要遍历所有点，只是为了防止一种特殊情况：凹多边形。
					//毕竟不能保证，所有的多边形都是凸多边形。
					let repeat_point_num = 0;
					for(let i=0;i<linepoint.length;i++){
						let flag = utils.isRepeat(p1,rs_pointer );
						if( flag ){
							repeat_point_num ++;
							break;//只要有重合。就break。
						}
					}
					if( repeat_point_num ==0 ){
						count++;
					}
				}
			    p1 = p2;
			}
			return count %2 ==0;
		},
		analysizDangerArea(){
			for(let i=0;i< __alldata.danger.length;i++){
				let areapoints = __alldata.danger[i];
				let rs = utils.exclude(   areapoints ,__alldata.gps);
				if(rs){//可能相交。
					rs = run.analysizArea( areapoints,__alldata.gps);
					if(! rs ){//进入区域。
						 return true;
					}
				}
			}
			return false;
		},
		analysizSpeedLimitArea(){
			//1 全程超速。
			console.info("当前gps速度："+ __alldata.gps.speed+"  === 最大限速"+ __alldata.max_speed);
			if( __alldata.gps.speed > __alldata.max_speed ){
				return true;
			}
			//2 区域超速。
			for(let i=0;i< __alldata.speedlimit.length;i++){
				let item = __alldata.speedlimit[i];
				let rs = utils.exclude(   item.points ,__alldata.gps);
				if(rs){//可能相交。
					rs = run.analysizArea( item.points,__alldata.gps);
					console.info( __alldata.gps.speed +"---"+ item.limitspeed );
					if(! rs && __alldata.gps.speed > item.limitspeed ){//进入限速区域，并且超速。
						 return true;
					}
				}
			}
			
			
			return false;
		},
		analysizProhibitArea(linepoint,gps_point){
			for(let i=0;i< __alldata.prohibitarea.length;i++){
				let areapoints = __alldata.prohibitarea[i];
				let rs = utils.exclude(  areapoints ,__alldata.gps);
				if(rs){//可能相交。
					rs = run.analysizArea( areapoints ,__alldata.gps);
					if(! rs ){//进入区域。
						 return true;
					}
				}
			}
			return false;
		},
		analysizParkingArea(data){
			for(let i=0;i< __alldata.parkingarea.length;i++){
				let p1 = __alldata.parkingarea[i];
				let length = utils.calcDistance(p1, __alldata.gps);
				if( length < p1.offset ){//到达停靠站点。
					if( i == __alldata.parkingarea.length -1 ){//是否到达目的地。 
						data.reachingdestionation = true;
					}
					return true;
				}
			}
			return false;
		}
		 
	}
	
	
	//对外暴露的方法。
	this.load=function(param){
		return init.load(param);
	};
	this.execute=function(gps){
		return run.execute(gps);
	};
}