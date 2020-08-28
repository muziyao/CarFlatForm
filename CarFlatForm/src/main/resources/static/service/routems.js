 
var map = new BMap.Map("l-map");
map.enableScrollWheelZoom();
 
let savedatas ={
		devId:"",
		routepoints:[],
		danger:[],
		speedlimit:[],
		prohibitarea:[],
		parkingarea:[]
}
//2 导航.获取导航路线的坐标点.
	var driving = new BMap.DrivingRoute(map, {   renderOptions: {map: map,  autoViewport: true},
									onSearchComplete: function(results){
										if (driving.getStatus() == BMAP_STATUS_SUCCESS){
											console.info(results);
											  //保存所有路线坐标。
											  savedatas.routepoints = results.Fr[0].ik[0].Hr;
										  
											  setTimeout(function() {
												 map.clearOverlays();
												 var options = {
													size: BMAP_POINT_SIZE_SMALL,
													shape: BMAP_POINT_SHAPE_STAR,
													color: '#d340c3'
												 }
												 var pointCollection = new BMap.PointCollection( savedatas.routepoints, options);  // 初始化PointCollection
												 map.addOverlay(pointCollection);  // 添加Overlay
												 //将第一个点设置为视野区域。
												 map.centerAndZoom(  savedatas.routepoints[0] , 16);
											 }, 2000);
											 
										}    
									} 
								});
//地图点击事件。
map.addEventListener("click", function(e){  
	if(point_type == null ){
		alert("您还未点击对应按钮");
		return;
	}
	 let point = new BMap.Point(e.point.lng, e.point.lat ) ;
	 var marker = new BMap.Marker(point);// 创建标注
	 map.addOverlay( marker );   //增加折线
	 tempmarkers.push( marker );
	 temppoints.push({lng:point.lng,lat:point.lat});
});

function searchRoute(){
	let v2 = $("#start_input").val();
	let v3 = $("#target_input").val();
	driving.search(v2,v3);
}
function saveFile(){
	console.info(savedatas);
	$.ajax({
		url:"test/json.action",
		type:"POST",
		contentType:false,
		data:JSON.stringify(savedatas),
		success(rs){
			alert("保存成功");
		}
	});
}


//生成数据。
let temppoints =[];
let tempmarkers = [];
function buildData(){
	if(!point_type)return;
	let color = "#1E9FFF";
	if( point_type == "speedlimit" ){
		//全部限速60 
		savedatas.speedlimit.push({
			limitspeed:60,
			points:temppoints
		});
		 color = "#1E9FFF";
	}else if(  point_type =="danger" ){
		 color = "#FF5722";
		savedatas.danger.push(temppoints);
		
	}else if(  point_type =="prohibitarea" ){
		color = "#d2d2d2";
		savedatas.prohibitarea.push(temppoints);
	}else if(  point_type =="parkingarea" ){
		savedatas.parkingarea.push( temppoints[0] );
		//加Label。
		var label = new BMap.Label("停靠站点"+savedatas.parkingarea.length,{offset:new BMap.Size(-20,-20)});
		tempmarkers[tempmarkers.length-1].setLabel(label);
	}
	//画折线
	if( point_type !="parkingarea" ){
		let line_points = [];
		temppoints.map((it)=>{
			line_points.push( new BMap.Point(it.lng, it.lat ) );
		});
		console.info( line_points );
		line_points.push( line_points[0] );
	  	var polyline = new BMap.Polyline( line_points, {strokeColor:color, strokeWeight:5, strokeOpacity:0.5});  //创建多边形
		map.addOverlay(polyline);   //增加折线
		line_points = null;
	}
	//清除操作。
	temppoints =[];
	tempmarkers = [];
	clearFlag();
	console.info(savedatas)
}
//清除标记。
function clearFlag(){
	point_type = null;
}

let point_type = null;
// 设置标记。 
function setFlag(flag){
	point_type = flag;
}

 