let ws = null;
ws = new WebSocket(`wss://${window.location.host}/carmonitorsys/voice/speaking`);
ws.binaryType = 'arraybuffer';
let devId = null;
ws.onopen=(e)=>{
	voice_tips.innerText = "";
	$.get("openapi/test/token.action",(rs)=>{
		let token = rs.data;
		devId = window.prompt();
		token = encodeURIComponent(token);
		let tk = `devId=${devId}&token=${token}\r\n`;
		console.info(tk);
		let b = new Blob([tk]);
		ws.send(b);
		console.info("打开连接成功");
	})
}
//let tk = `devId=8986011980162410404E&token=6899fa62-86b3-4fe3-925e-8afd31dd3370\r\n`;
ws.onmessage=(e)=>{
	voice_tips.innerText = "驾驶员正在讲话...";
	//生成一个audio标签听录音结果
	let buf = e.data;
	var array = new Int8Array(buf);
	player.feed(array);
	
}
ws.onerror=ws.onclose=(e)=>{
	console.info("关闭")
	ws.close();
	voice_tips.innerText = "车载主机语音已断开";
	 
}
 
function startRecord(){
	$.get("openapi/test/token.action",(rs)=>{
		let token = rs.data;
		callClient({devId:devId,token:token});//jQuery自动编码。
	});
	
	let callClient=(param)=>{
		$.get("openapi/voice/connection.action",param,()=>{
			isRecording = true;
			voice_tips.innerText = "请开始说话吧";
		})
	}
}
function stopRecord(){
	isRecording = false;
	chunks = [];
	$.get("openapi/test/token.action",(rs)=>{
		let token = rs.data;
		callClient({devId:devId,token:token});//jQuery自动编码。
	});
	
	let callClient=(param)=>{
		$.get("openapi/voice/offline.action",param,()=>{
			voice_tips.innerText = "关闭";
		})
	}
	
}
 
var player = new PCMPlayer({
    encoding: '16bitInt',
    channels: 1,
    sampleRate: 44100,
    flushingTime: 1500
});

 

let isRecording = false;
let constraints = { audio: true };
let duration = 10;
let sampleRate = 44100;
let chunks = [];
let as;

//采集媒体流
navigator.mediaDevices.getUserMedia(constraints)
    .then((stream) => {
        ac = new AudioContext({ sampleRate: sampleRate });

        //创建音频处理的源节点
        let source = ac.createMediaStreamSource(stream);
        //创建自定义处理节点
        let scriptNode = ac.createScriptProcessor(512, 1, 1);
        //创建音频处理的输出节点
        let dest = ac.createMediaStreamDestination();

        //串联连接
        source.connect(scriptNode);
        scriptNode.connect(dest);

        //定义stream chunk的事件处理
        scriptNode.onaudioprocess = function (audioProcessingEvent) {
        	//获取输入流位置
            var inputBuffer = audioProcessingEvent.inputBuffer;
            //获取输出流位置
            var outputBuffer = audioProcessingEvent.outputBuffer;
            for (var channel = 0; channel < outputBuffer.numberOfChannels; channel++) {
                var inputData = inputBuffer.getChannelData(channel); // Float32Array
			    var outputData = outputBuffer.getChannelData(channel);
                if (isRecording) {
                    for (let i = 0; i < inputData.length; i = i + 1) {
                        chunks.push(inputData[i]);
                    }
                    processPCMData();
                }
            };
        }
    })
    .catch(err => {
        console.log(err);
    });



//处理原始数据
function processPCMData() {
	 
	let buffer = new ArrayBuffer(chunks.length * 2);
	let view = new DataView(buffer);	
	floatTo16BitPCM(view, 0, chunks);
    //生成一个audio标签听录音结果
    let blob = new Blob([view]);
    //发送。
    chunks =[];
   
    try{
    	 ws.send(blob);
    }catch (e) {
    	stopRecord();
    	voice_tips.innerText = "车载主机语音已断开";
	} 
}


//生成下载
function createDownload(blob) {
    let url = URL.createObjectURL(blob);
    const downloadEl = document.createElement('a');
    downloadEl.style = 'margin-left:400px;';
    downloadEl.innerHTML = '点击下载';
    downloadEl.href = url;
    downloadEl.download = 'record.pcm';
    document.getElementById("containersds").appendChild(downloadEl);
}

//float32转换16bit位深pcm
function floatTo16BitPCM(output, offset, input) {
    for (let i = 0; i < input.length; i++ , offset += 2) {
        let s = Math.max(-1, Math.min(1, input[i]));
        output.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
    }
}
