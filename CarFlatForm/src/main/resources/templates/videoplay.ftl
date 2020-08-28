<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title>车载系统后台</title>
  <link href="http://vjs.zencdn.net/5.5.3/video-js.css" rel="stylesheet">
  <script src="http://vjs.zencdn.net/5.5.3/video.js"></script>
  <!-- IE8支持 -->
  <script src="http://vjs.zencdn.net/ie8/1.1.1/videojs-ie8.min.js"></script>
  <script type="text/javascript" src="http://cake.yuzhiculture.com/jquery/jquery-3.4.1.min.js"></script>
</head>
<body  >

 
  
  <style>
  .video{
    	height:100%;
  }
  .video_inner{
    	height:100%;
    	width:100%;
    	border-radius:5px;
    	background-color: #393D49;
    	position:relative;
    	left:0px;
    	top:0px;
  }
  .video_inner .title{
  	 position:absolute;
  	 top:0px;
  	 left:20px;
  	 width:100%;
  	 margin:0px auto;
  	 font-size:20px;
  	 color:white;
  	 font-family:"微软雅黑";
  	 z-index:9999999999999999999999999999999999999999;
  }
  
  .video-js.vjs-playing .vjs-tech {
    pointer-events: auto;
}
.vjs-paused .vjs-big-play-button,
.vjs-paused.vjs-has-started .vjs-big-play-button {
    display: block;
}
/*播放时间*/
.video-js .vjs-time-control{display:none;}
.video-js .vjs-remaining-time{display: none;}

  </style>
  
  
 <table style="width:100%;height:600px;">
 	<tr>
	 	<td width="50%" height="50%">
				<video id="my-video-01"   class="video-js vjs-big-play-centered" style="width:100%;height:100%" class="video-js" autoplay="autoplay" controls preload="auto"  data-setup="{}">
			     <source src="${rtmpurl!}/${devId!}1" type="rtmp/flv">
			 	</video>
			 	<embed width="90" height="50" class="openFlash" style="position:absolute;left:calc( (100% - 90px ) / 2); top:calc( (100% - 50px ) / 2); z-Index:9999;" type="application/x-shockwave-flash">
			 	</embed>   	
	 	</td>
	 	<td>
				<video id="my-video-02"   class="video-js vjs-big-play-centered" style="width:100%;height:100%" class="video-js" autoplay="autoplay" controls preload="auto"  data-setup="{}">
			     <source src="${rtmpurl!}/${devId!}2" type="rtmp/flv">
			 	</video>
			 	<embed width="90" height="50" class="openFlash" style="position:absolute;left:calc( (100% - 90px ) / 2); top:calc( (100% - 50px ) / 2); z-Index:9999;" type="application/x-shockwave-flash">
			 	</embed>   	
	 	</td>
 	</tr>
 	<tr>
	 	<td>
				<video id="my-video-03"   class="video-js vjs-big-play-centered" style="width:100%;height:100%" class="video-js" autoplay="autoplay" controls preload="auto"  data-setup="{}">
			     <source src="${rtmpurl!}/${devId!}3" type="rtmp/flv">
			 	</video>
			 	<embed width="90" height="50" class="openFlash" style="position:absolute;left:calc( (100% - 90px ) / 2); top:calc( (100% - 50px ) / 2); z-Index:9999;" type="application/x-shockwave-flash">
			 	</embed>   	
	 	</td>
	 	<td>
				<video id="my-video-04"   class="video-js vjs-big-play-centered" style="width:100%;height:100%" class="video-js" autoplay="autoplay" controls preload="auto"  data-setup="{}">
			     <source src="${rtmpurl!}/${devId!}4" type="rtmp/flv">
			 	</video>
			 	<embed width="90" height="50" class="openFlash" style="position:absolute;left:calc( (100% - 90px ) / 2); top:calc( (100% - 50px ) / 2); z-Index:9999;" type="application/x-shockwave-flash">
			 	</embed>   	
	 	</td>
 	</tr>
 	
 </table>
 
<script>
 
 
 function flashChecker() {
     var hasFlash = 0;         //是否安装了flash
     var flashVersion = 0; //flash版本
     var isIE = 0;      //是否IE浏览器

     if (isIE) {
         var swf = new ActiveXObject('ShockwaveFlash.ShockwaveFlash');
         if (swf) {
             hasFlash = 1;
             VSwf = swf.GetVariable("$version");
             flashVersion = parseInt(VSwf.split(" ")[1].split(",")[0]);
         }
     } else {
         if (navigator.plugins && navigator.plugins.length > 0) {
             var swf = navigator.plugins["Shockwave Flash"];
             if (swf) {
                 hasFlash = 1;
                 var words = swf.description.split(" ");
                 for (var i = 0; i < words.length; ++i) {
                     if (isNaN(parseInt(words[i]))) continue;
                     flashVersion = parseInt(words[i]);
                 }
             }
         }
     }
     return {f: hasFlash, v: flashVersion};
 }


 
 
//JavaScript代码区域
$(document).ready(function(){
	    var fls = flashChecker();
		 var s = "";
		 if (fls.f) {
			 $(".openFlash").hide();
		 }else{
			 alert("您没有安装flash。请点击安装FLash。");
		 }
});
</script>
</body>
</html>
      