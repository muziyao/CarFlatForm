/***
 * 地图横着的是纬度，竖着的是经度。
 * 纬度在二维坐标系中，相当于一条条 y = b (b 为常数) 所在的直线。
 * 经度在二维坐标系中，相当于一条条 x = b (b 为常数) 所在的直线。
 * 
 * 中国经度范围：73°33′E至135°05′E。纬度范围：3°51′N至53°33′N。
 * 西北地区经度是东经73度至东经123度，纬度是北纬37度至北纬50度
 * 刚好：
 * 以西经-->东经的方向。正好是逐渐变大的，这与X轴的正方向一致。
 * 以赤道-->北纬的方向。正好是逐渐变大的，这与Y轴的正方向一致。
 * 如果忽略球面弧度。把我国的在地图上的分布，映射为在二维坐标系中的分布。
 * 就可以利用平面直角坐标系中的直线方程进行一系列的点和点、点和线、点和多边形的位置关系
 * 的判断。当然计算要在一定误差允许范围内。
 * 
 * Point(经度,纬度) ===> Point(lng,lat) <===> Point(x,y)。
 */
//[1]初始化百度地图,位置为钦州.
var map = new BMap.Map("l-map");
map.enableScrollWheelZoom();
let alldata ={
	routeline:null
}; 
//2 导航.获取导航路线的坐标点.
	var driving = new BMap.DrivingRoute(map, {   renderOptions: {map: map,  autoViewport: true},
									onSearchComplete: function(results){
										if (driving.getStatus() == BMAP_STATUS_SUCCESS){
											console.info(results);
											  alldata.routeline = results.Fr[0].ik[0].Hr
											  start_point = alldata.routeline[0];
											  target_point = alldata.routeline[alldata.routeline.length-1];
											  calcPointsDistance();
											  setTimeout(function() {
												 map.clearOverlays();
												 var options = {
													size: BMAP_POINT_SIZE_SMALL,
													shape: BMAP_POINT_SHAPE_STAR,
													color: '#d340c3'
												 }
												 var pointCollection = new BMap.PointCollection(alldata.routeline, options);  // 初始化PointCollection
												 map.addOverlay(pointCollection);  // 添加Overlay
												 //将第一个点设置为视野区域。
												 map.centerAndZoom( alldata.routeline[0] , 19);
											 }, 2000);
											 
										}    
									} 
								});
driving.search("钦州离合江","久隆镇");

let wucha = 0.000003;
let line_point = [ ];//电子围栏显示在地图上点数组。双重用。折线和标注都用。
let start_point = null;//开始出发点。
let target_point = null;//目的地地点。
let temp_linepoint = [];
let points_distance = [];//路线中两点的直线距离。  
let flag= "border"; //标志位。
let gps_marker = null;
let gps_point = null;
var offset_param={
		openflag:false,
		open(){
			this.openflag = true;
			this.rate = 1;
		},
		close(){
			this.openflag = false;
		},
		offset(){
			if(!this.openflag){
				return 0;
			}else{
				if( Math.floor( Math.random()*100 ) % 4 == 0 ){
					let rs = Math.floor( Math.random()*1000 )-1;
					return (rs /1000000);
				}else{
					return 0;
				}
				
			}
		}
}

function startOffset(){ offset_param.open();}
function closeOffset(){ offset_param.close();}

//3 添加点击事件。
//4 绘制折线,并获取折线地图坐标.
//5 用数遍模拟当期的车辆的动态坐标.
map.addEventListener("click", function(e){  
		 let point = new BMap.Point(e.point.lng, e.point.lat ) ;
		 var marker = new BMap.Marker(point);// 创建标注
		  if(flag =="border"){
			  temp_linepoint.push( point );
			  map.addOverlay(marker);   //增加折线
		  }else if(flag =="gps"){
			  if(gps_marker){
				  map.removeOverlay( gps_marker );   //增加折线
			  } 
			  map.addOverlay( marker );   //增加折线
			  gps_marker = marker;
			  gps_point = point;//更新GPS坐标点。
		  }
		  
	});
//合成限速区域。 line_point[0]
function limitspeed(){
	line_point[0] = [];
	temp_linepoint.map(function(p){
		line_point[0].push( p );
	});
	temp_linepoint = [];
	transform(0,"blue");
}
//合成危险路段区域。 line_point[1]
function dangerarea(){
	line_point[1] = [];
	temp_linepoint.map(function(p){
		line_point[1].push( p );
	});
	temp_linepoint = [];
	transform(1,"red");
}
function transform( i ,color ){
	line_point[i].push( line_point[i][0] );
  	var polyline = new BMap.Polyline( line_point[i], {strokeColor:color, strokeWeight:5, strokeOpacity:0.5});  //创建多边形
	 map.addOverlay(polyline);   //增加折线
	 line_point[i].pop();
	 //初始化直线方程。
	 initLineFunction( line_point[i] );
	
}
//初始化直线方程。
function initLineFunction( linepoint ){
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
		if( Math.abs( p1.lng - p2.lng) < wucha ){
			p1.k_exist = false;//动态添加。
		}else {//斜率存在。
			let k = (p1.lat - p2.lat)/(p1.lng - p2.lng );
			//b = y-kx 。由于直线过点p1、p2，将p1或p2带入得: b = p1.lat - k * p1.lng;
			if(Math.abs(k)<=wucha)k = 0;
			p1.k_exist = true;//动态添加。
			p1.k = k;
		}
		p1 = p2;
	}
	
}


let polygon_temp = new BMap.Polygon([ ], {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5});  //创建多边形

let recentpoint ={
	index :0,
	point:null
}
//6 运行算法分析.
function triggle(){
	//1 路线偏离分析。
	analysiz_offset();
	//2 区域进入分析。
	if( line_point [0] ){
		//
		let rs = exclude(   line_point[0]  );
		if(rs){//可能相交。
			rs = analysiz_area( line_point[0] );
			if(! rs ){//进入区域。
				playSoundsTips("mp3/limit_speed.mp3");
			}
		}else{
			console.info("不可能相交");
		}
	}
	
	if( line_point [1] ){
		rs = exclude(   line_point[1]  );
		if(rs){//可能相交。
			rs = analysiz_area(line_point[1] );
			if(!rs ){
				playSoundsTips("mp3/limit.mp3");
			}
		}else{
			console.info("不可能相交");
		}
	}
	//3 出发点判断。
	judgeStartPoint();
	//4 目的地判断。
	judgeTargetPoint();
}

function judgeStartPoint(){
	let distance = getDistance( start_point, gps_point );
	if( distance <200 ){ //小于两百米认为。停留在目的地。
		console.info("正在出发");
	}else{
	    console.info("行驶途中。。");
	}
}
function judgeTargetPoint(){
	let distance = getDistance( target_point, gps_point );
	if( distance <200 ){ //小于两百米认为。到达目的地。
		console.info("到达目的地");
	}else{
		console.info("行驶途中。。");
	}
}
/**
 * 
 * 用当前的GPS坐标和区域坐标进行粗略判断。
 * true 不可能相交。 false有可能相交。
 * */
function exclude(linepoint){
	
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
	
}
//区域包含分析。
function analysiz_area( linepoint){
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
		let rs_pointer = judgeXiaoJiao(p1,p2,gps_point);
	    if(rs_pointer){//相交。
			//顶点判断。
			let flag1 = isRepeat(p1,rs_pointer );
			let flag2 = isRepeat(p2,rs_pointer );
			if(!flag1  && !flag2 ){
				count++;
			}
		}
	    p1 = p2;
	}
	return count %2 ==0;
}
 
function isRepeat(p1,p2){
	let L_p1_p2_sqt =  Math.abs(p1.lat - p2.lat )* Math.abs(p1.lat - p2.lat )  + Math.abs(p1.lng - p2.lng )*Math.abs(p1.lng - p2.lng ) ;
	 L_p1_p2_sqt = getFloat(Math.sqrt( L_p1_p2_sqt ),8);
	if(  Math.abs(L_p1_p2_sqt) <wucha  ){
		return true;
	}else{
		return false;
	}
} 
/** 判断点的射线与直线上的制定线段是否相交。
 *  
 */
function judgeXiaoJiao(p1,p2,gps){
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
			
			 L_p1_p2_sqt = getFloat(Math.sqrt( L_p1_p2_sqt ),8);
			 L_p1_jd_sqt = getFloat(Math.sqrt( L_p1_jd_sqt ),8);
			 L_p2_jd_sqt = getFloat(Math.sqrt( L_p2_jd_sqt ),8);
			 
			if(  x<gps.lng //由于规定是向左的射线。所以交点必须要小于原点的x。
				 && L_p1_jd_sqt < L_p1_p2_sqt 
				 && L_p2_jd_sqt<  L_p1_p2_sqt
				 && Math.abs(  L_p1_p2_sqt - L_p1_jd_sqt - L_p2_jd_sqt ) <wucha 
			    ){
				return  new BMap.Point(x,y)
			}else{
				return  null;
			}
		}else{//斜率不存在。线段垂直于X轴，也就是垂直于维度。只需要判断y是否在其中即可。
			if( (gps.lat >p1.lat && gps.lat <p2.lat )||
				(gps.lat >p2.lat && gps.lat <p1.lat )){
				return  new BMap.Point(p1.lng,gps.lat);
			}else{
				return null;
			}
		}
	}
}
 
function getFloat(num,n){
	return num.toFixed(n);
} 
//路线偏离分析。
function analysiz_offset(){
	let rs = getTheRecentPoint( );
	if( rs.code =="normal"){
		recentpoint.index = rs.index;
	}else if( rs.code =="offset" ){//只要偏离路线。index归零
		//偏离路线之后，需要再重新计算一次。只有这次偏离了。才是真的偏离。
		if(recentpoint.index -5 > 0 ){
			 recentpoint.index -= 5;
			 //要是累计进入3次。需要清零了。
		}else{
			recentpoint.index = 0;
		}
		rs = getTheRecentPoint( );
		if( rs.code =="normal"){
			recentpoint.index = rs.index;
		}else if( rs.code =="offset" ){
			playSoundsTips("mp3/py.mp3")
		}
	}
}

function getTheRecentPoint(){
	for( let i=recentpoint.index;i<alldata.routeline.length-1;i++ ){
		let pre_pot = alldata.routeline[i]; //上一个点的坐标。
		let next_pot = alldata.routeline[i +1 ];//下一个点的坐标。
		//计算三角形的三条边长。
		let c = points_distance [ i ];//取出该两点之间的距离。由于采集点是固定的。所以初始化时就可以算出固定值。
		let a = getDistance( pre_pot , gps_point );//由当前点和gps坐标点算出距离。
		let b = getDistance( next_pot , gps_point );//由下一个点和gps坐标点算出距离。
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
		if(r1 && r2 && Hx<=20){//2个条件。1：是锐角三角形。2高必须少于40m。
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
}

function centerPointes( index ){
	map.setCenter( alldata.routeline[index]);
}
	

function getDistance(p1,p2){
	// c*c = | lng1 - lng2 |^2  + |lat1 - lat2|^2 
	try{
		let rs = Math.sqrt(
				  Math.abs( p1.lng - p2.lng )  * Math.abs( p1.lng - p2.lng ) 
				  + 
				 Math.abs( p1.lat - p2.lat ) * Math.abs( p1.lat - p2.lat )
		    ) * 111110;
		return Math.floor( rs );
	}catch(e){
		console.info(p1);
		console.info(p2);
		debugger;
	}
	//经纬度相差一度。就是111.11KM大约。
	
}

function getDistanceCenter(p1,p2){
	// c*c = | lng1 - lng2 |^2  + |lat1 - lat2|^2 
	let lng =  (p1.lng + p2.lng )/2;
	let lat = (p1.lat + p2.lat) /2;
	return new BMap.Point(lng,lat );
}

//计算直线中两点的直线距离。
function calcPointsDistance(){
	 for(let i=0;i<alldata.routeline.length -1 ;i++){
		 points_distance[i] =  getDistance(alldata.routeline[i],alldata.routeline[i+1]); 
			 //两点间的距离。为严格按照模型计算，不能调用百度的map.getDistance()
	 }
}
function goBackRun(){
	//如果目的地跑到了最后。要调转过来。
	if( target_point == alldata.routeline [ alldata.routeline.length -1 ] ){
		let list = [];
		let i = alldata.routeline.length;
		while( i > 0 ){
			list.push( alldata.routeline[ i -1 ] );
			i--;
			if(i==0)break;
		}
		alldata.routeline = list;
		calcPointsDistance();//重新计算两条点间的线段。放在前一个。
	}
	//开始运行。
	restartRun();
}
function sendOutRun(){
	stopRun();
	//如果开始跑到了最后。
	if( start_point == alldata.routeline [ alldata.routeline.length -1 ] ){
		let list = [];
		let i = alldata.routeline.length;
		while( i > 0 ){
			list.push( alldata.routeline[ i -1 ] );
			i--;
			if(i==0)break;
		}
		alldata.routeline = list;
		calcPointsDistance();//重新计算两条点间的线段。放在前一个。
	}
	//开始运行。
	restartRun();
}

function addRunTime(){
	if( speed_time <=2000){
		speed_time+=50;
		$("#time_show").text(speed_time );
		
	}

}
function subRunTime(){
	if( speed_time >50 ){
		speed_time -=50;
		$("#time_show").text(speed_time);
		
	}
}

let runtimer;
let runtime = 0;
let speed_time = 400;
$("#time_show").text(speed_time);

function startRun(){
	stopRun();
	runtimer = setInterval(function(){
		let point = alldata.routeline[runtime++];
		point = new BMap.Point (point.lng ,point.lat );
		point.lng += offset_param.offset();
		var marker = new BMap.Marker(point);// 创建标注
		if(gps_marker){
			map.removeOverlay( gps_marker );   //增加折线
		} 
		map.addOverlay( marker );   //增加折线
		gps_marker = marker;
		gps_point = point;//更新GPS坐标点。
		triggle();
		map.setCenter( gps_point );
		if( runtime >= alldata.routeline.length -1 ){
			clearInterval( runtimer );
		}
	},speed_time);
	
}

function stopRun(){
	clearInterval( runtimer );
}

function restartRun(){
	recentpoint.index = 0;
	stopRun();
	runtime = 0;
	startRun();
}



let play_stop_flag = {
		
}
function playSoundsTips(url){
  if( play_stop_flag[url])return;
  	   play_stop_flag[url] = true;
  var audio = document.createElement('audio');
  audio.addEventListener('ended', function(){
	  eventListener(url);
  }, false);
  var source = document.createElement('source');   
  source.type = "audio/mpeg";
  source.type = "audio/mpeg";
  source.src = url;   
  source.autoplay = "autoplay";
  source.controls = "controls";
  audio.appendChild(source); 
  audio.play();
 
}


function eventListener(url){
	play_stop_flag[url] = false; 
}
 
 