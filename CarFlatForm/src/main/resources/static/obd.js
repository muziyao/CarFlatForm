   let app = new Vue({
	  el: '#app',
	  data: {
		  obdmodels:{},
		  obdstatus:"设备未连接"
	  } 
	});
 
let ws = new WebSocket(`ws://${window.location.host}/obd`);
ws.onmessage=(e)=>{
	 let data = JSON.parse(e.data);
	 app.obdmodels[data.devId] = data;
}