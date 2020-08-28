let alldata = null;
var map = new BMap.Map("l-map");
map.enableScrollWheelZoom();

//分析算法库。
let rservice = new RouteService();
let gpsmarker = null;

map.addEventListener("click", function(e){  
	 updateLocation( e.point );
});

function updateLocation(point){
	 let p = new BMap.Point(point.lng, point.lat ) ;
	 gpsmarker.setPosition(p);
	 $("#lat_input").val(p.lat);
	 $("#lng_input").val(p.lng);
	 //map.centerAndZoom( p , 16);
}

function initRoute(){
	$("#route_list").empty();
	 for(let i=0;i<rs.length;i++){
		 $("#route_list").append("<option value='"+rs[i]+"'>"+rs[i]+"</option>");
	 }
}

$("#readlocationbtn").click(()=>{
	let index = $("#input_index").val();
	index= parseInt(index);
	let p = alldata.routepoints[index];
	$("#lat_input").val(p.lat);
	$("#lng_input").val(p.lng);
});

 
//加载路线。
$("#loadroutebtn").click(()=>{
	
	$.get("carmonitorsys/routeline/get.action",{devId:$("#devId").val()},(rs)=>{
		alldata = rs.data;
		loadDataIntoMap();
		gpsmarker = new BMap.Marker(new BMap.Point(alldata.routepoints[0].lng ,alldata.routepoints[0].lat));
		map.addOverlay( gpsmarker);
		rservice.load( alldata );
		 
	});
});

//运行测试。
$("#executebtn").click(()=>{
	let lat = lat_input.value;
	let lng = lng_input.value;
	let gps={
		lat:Number(lat),
		lng:Number(lng),
		speed:$("#gpsspeed").val()
	};
	gpsmarker.setPosition( new BMap.Point(gps.lng,gps.lat));
	let data = rservice.execute(gps);
	showResult(data);
});

let index = 0;
let ttt;
$("#autorunbtn").click(()=>{
	ttt = setInterval(()=>{
		let gps = alldata.routepoints[index];
		//1执行路线分析。
		let data = rservice.execute( gps );
		//2将路线分析结果输出在面板上。
		showResult(data);
		//更新当前gps位置，并居中显示。
		updateLocation(gps);
		if(index == alldata.routepoints){
			clearInterval(ttt);
		}
		index++;
	},30);
	
});
 

function showResult(data){
	let div =`<div>${Date().toString().substring(10,24)}-
		路线:${data.offset?"<font color='red'>偏移</font>":"<font color='green'>正常</font>"}   
		进入危险区域:${ data.danger?"<font color='red'>是</font>":"<font color='green'>否</font>"} 
		进入限速区域:${ data.limitspeed?"<font color='red'>是</font>":"<font color='green'>否</font>"} 
		进入禁止区域:${ data.prohibit?"<font color='red'>是</font>":"<font color='green'>否</font>"} 
		靠近停靠站点:${ data.parking?"<font color='red'>是</font>":"<font color='green'>否</font>"} 
		</div>`;
	$("#result_panel").append( div );
	//滚动到最底部。
	$("#result_panel").scrollTop($("#result_panel").get(0).scrollHeight);
	if(data.offset && !data.parking){
		playSoundsTips("../mp3/offset.mp3");
	}
	if(data.limitspeed){
		playSoundsTips("../mp3/speedlimit.mp3");
	}
	if(data.prohibit){
		playSoundsTips("../mp3/prohibitarea.mp3");
	}
	if(data.danger){
		playSoundsTips("../mp3/danger.mp3");
	}
	if(data.parking){
		playSoundsTips("../mp3/parking.mp3");
	}
}
function loadDataIntoMap(){
	 map.clearOverlays();
	 //渲染路线
	 renderRouteLine();
	 //渲染危险区域。
	 renderDangerArea();
	 //渲染禁止区域
	 renderProhitArea();
	 //渲染限速区域
	 renderLimitingSpeedArea();
	 //渲染停靠点。
	 renderParkingArea();
}

function renderRouteLine(){
	 var options = {
				size: BMAP_POINT_SIZE_SMALL,
				shape: BMAP_POINT_SHAPE_STAR,
				color: '#d340c3'
			 }
	 //渲染路线。
	 let ps =[];
	 if(!alldata.routepoints)alldata.routepoints=[];
	 alldata.routepoints.map((p)=>{
		 ps.push( new BMap.Point(p.lng, p.lat )  );
	 });
	 if(ps.length>0){
		 var pointCollection = new BMap.PointCollection( ps, options);  // 初始化PointCollection
		 map.addOverlay(pointCollection);  // 添加Overlay
		 //将第一个点设置为视野区域。
		 map.centerAndZoom( ps[0] , 16);
	 }
}

function renderDangerArea(){
	 if(!alldata.danger)alldata.danger=[];
	 for(let i=0; i < alldata.danger.length;i++ ){
		 let line_points = [];
		 let danger = alldata.danger[i];
		 let lng = 0;
		 let lat = 0;
		 danger.map((p)=>{
			 line_points.push( new BMap.Point(p.lng, p.lat )  );
			 lng +=p.lng;
			 lat +=p.lat;
		 });
		 lng /= line_points.length;
		 lat /= line_points.length;
		 addLabel( "危险区域", new BMap.Point(lng, lat )  );
		line_points.push( line_points[0] );
	  	var polyline = new BMap.Polyline( line_points, {strokeColor:"#FF5722", strokeWeight:5, strokeOpacity:0.5});  //创建多边形
		map.addOverlay(polyline);   //增加折线
		line_points = null;
	 }
}
//禁止区域
function renderProhitArea(){
	 if(!alldata.prohibitarea)alldata.prohibitarea=[];
	 for(let i=0; i < alldata.prohibitarea.length;i++ ){
		 let line_points = [];
		 let lng = 0;
		 let lat = 0;
		 alldata.prohibitarea[i].map((p)=>{
			 line_points.push( new BMap.Point(p.lng, p.lat )  );
			 lng +=p.lng;
			 lat +=p.lat;
		 });
		 lng /= line_points.length;
		 lat /= line_points.length;
		 addLabel( "禁止区域", new BMap.Point(lng, lat )  );
		line_points.push( line_points[0] );
	  	var polyline = new BMap.Polyline( line_points, {strokeColor:"black", strokeWeight:5, strokeOpacity:0.5});  //创建多边形
		map.addOverlay(polyline);   //增加折线
		line_points = null;
	 }
}
//限速区域
function renderLimitingSpeedArea(){
	 if(!alldata.speedlimit)alldata.speedlimit=[];
	 for(let i=0; i < alldata.speedlimit.length;i++ ){
		 let line_points = [];
		 let item = alldata.speedlimit[i];
		 if(!item.points)continue;
		 let lng = 0;
		 let lat = 0;
		 item.points.map((p)=>{
			 line_points.push( new BMap.Point(p.lng, p.lat )  );
			 lng +=p.lng;
			 lat +=p.lat;
		 });
		 lng /= line_points.length;
		 lat /= line_points.length;
		 addLabel( "限速区域，限速"+item.limitspeed,new BMap.Point(lng, lat ) );
		line_points.push( line_points[0] );
	  	var polyline = new BMap.Polyline( line_points, {strokeColor:"#1E9FFF", strokeWeight:5, strokeOpacity:0.5});  //创建多边形
		map.addOverlay(polyline);   //增加折线
		line_points = null;
	 }
}

function renderParkingArea(){
	 if(!alldata.parkingarea)alldata.parkingarea=[];
	 for(let i=0; i < alldata.parkingarea.length;i++ ){
		 let point = new BMap.Point(alldata.parkingarea[i].lng, alldata.parkingarea[i].lat )  
		 addLabel( "停靠站点"+(i+1),point );
	 }
}

function addLabel(text,point){
	var opts = {
			  position : point,    // 指定文本标注所在的地理位置
			  offset   : new BMap.Size(30, -30)    //设置文本偏移量
			}
	var label = new BMap.Label(text, opts);  // 创建文本标注对象
		label.setStyle({
			 fontSize : "16px",
			 height : "20px",
			 lineHeight : "16px"
		 });
	map.addOverlay(label); 
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
 

 /*
let savedatas ={
		devId:"",
		routepoints:[],
		danger:[[],[]],
		speedlimit:[{speed:xxx,points:[]}],
		prohibitarea:[],
		parkingarea:[]
}
 */  
 