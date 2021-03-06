(function(){
	new Vue({
		 el: '#rootcontainer',
		data(){
			return{ 
				domain:`${window.location.host}`,
				http:"https://",
				ws:"wss://",
				model:{
					devId:"",
					date:"",
					num:1
				},
				returnmodel:{
					num:"",
					date:"",
					filename:""
				},
				fileList:[],
				downloadurl:""
				 
			}
		},
		mounted(){
			 this.initCarList();
			 this.initWebSocket();
		},
		methods:{
			getUrlParam(){
				 let str = location.search;
				 let op = {};
				 if(str.startsWith("?")){
					 str = str.substring(1,str.length);
					 let kvs = str.split("&");
					 if(kvs.length > 0 ){
						 for(let i=0;i<kvs.length;i++){
							  let kv = kvs[i].split("=");
							  let k = kv[0];
							  let v = kv[1];
							  op[k] = v;
						 }
					 }
				 }
				 return op;
			},
			
			showRowDataByDevId(devId){
				let container = this.$('#carNumTableListContainer');//定义滚动列表div容器
				let trs = container.find("tbody tr");
				
				for(let i=0;i<trs.length;i++){
					if(!devId){
						trs.eq(i).show();
					}else{
						let srcdevId = trs.eq(i).find("td").eq(1).attr("data-content");
						if( srcdevId == devId ){
							trs.eq(i).show();
						}else{
							trs.eq(i).hide();
						}
					}
				}
			},
			initCarList(){
				let _this = this;
			    layui.use(['table','laydate',"jquery","layer"], ()=>{
			      let table = layui.table;
				  let laydate = layui.laydate;
				  let $ = layui.jquery;
				  _this.layer = layui.layer;
			      //第一个实例
			      table.render({
			        elem: '#carNumTableList'
			        ,where:{
			        	from:"outer"
			        } 
			        ,done(){
			        	let op = _this.getUrlParam();
			        	_this.showRowDataByDevId(op.devId);
			        }
			        ,url: `${_this.http}${_this.domain}/upgrade/getcarlist.action` //数据接口
			        ,cols: [[ //表头
			          {field: 'carNum', title: '车牌号' },
			          {field: 'devId',hide:true,templet(d){
			        	  return `<a id="${d.devId}"></a>`;
			          }},
					    {field: 'online', title: '状态', width: 80,
							templet: function(d){
							        if( d.online == 'Y')return  `<font color="green">在线</font>`;
									else return `<font color="#c0c0c0">离线</font>`;
							    }
						},
			          {field: 'wealth', title: '选择', width: 80, type:"radio"}
			        ]]
			      });
				  
				  table.on('radio(carNumTableList)', function(obj){
						 _this.model.devId = obj.data.devId;
				  });
				  
				  //执行一个laydate实例
				  laydate.render({
				    elem: '#showdate' //指定元素
				  });
				  
				  _this.table = table;
				  _this.laydate = laydate;
				  _this.$ = $;
				  
			    });
			},
			initWebSocket(){
				this.websocket = new WebSocket(`${this.ws}${this.domain}/videoreview`);
				let _this = this;
				this.websocket.onmessage=function(e){
				   _this.onMessage(JSON.parse( e.data ));
				}
			},
			queryFileDir(){
				
				if(!this.model.devId){
					 this.layer.msg("请选择车辆");
					return;
				}
				
				this.model.date = this.$("#showdate").val();
				if(!this.model.date){
					 this.layer.msg("请选择日期");
					return;
				}
				
				 this.layer.msg("正在查询，请稍后...");
				 this.$.get(`${this.http}${this.domain}/carmonitorsys/video/sendgetfilemsg.action`,this.model,(rs)=>{
					 
				 })
			},
			onMessage(rs){
				if(rs.msgtype =="filelist"){
					if(rs.code){
						this.layer.msg("查询成功");
						this.fileList = rs.data;
						this.returnmodel.date = rs.date;
						this.returnmodel.num = rs.num;
					}else{
						 this.layer.msg("当天无视频");
					}
				}else if(rs.msgtype =="download"){
				  //修改video标签的src属性。
				  this.layer.msg("即将播放"+this.returnmodel.filename);
				  this.downloadurl = this.http+this.domain+"/carmonitorsys/video/download.action?filename=" + encodeURIComponent(this.returnmodel.filename);
				}
			},
			playVideo(filename){
				if(!this.model.date){
					 this.layer.msg("请选择车辆");
					return;
				}
				 this.layer.msg("正在下载请稍后...");
				 this.returnmodel.devId = this.model.devId;
				 this.returnmodel.filename = filename;
				 let _this = this;
				 this.$.get(`${this.http}${this.domain}/carmonitorsys/video/senddownloadmsg.action`,
						 this.returnmodel,(rs)=>{
						 _this.layer.msg("正在下载请稍后..."); 
				 })
			}
		}
	});
})();
