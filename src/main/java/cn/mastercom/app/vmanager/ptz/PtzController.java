package cn.mastercom.app.vmanager.ptz;

import cn.mastercom.app.gb28181.bean.Device;
import cn.mastercom.app.gb28181.transmit.cmd.impl.SIPCommander;
import cn.mastercom.app.storager.IVideoManagerStorager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class PtzController {
	
	private final static Logger logger = LoggerFactory.getLogger(PtzController.class);
	
	@Autowired
	private SIPCommander cmder;
	
	@Autowired
	private IVideoManagerStorager storager;

	/***
	 * http://localhost:8080/api/ptz/34020000001320000002_34020000001320000008?leftRight=1&upDown=0&inOut=0&moveSpeed=50&zoomSpeed=0
	 * @param deviceId
	 * @param channelId
	 * @param leftRight
	 * @param upDown
	 * @param inOut
	 * @param moveSpeed
	 * @param zoomSpeed
	 * @return
	 */
	@PostMapping("/ptz/{deviceId}/{channelId}")
	public ResponseEntity<String> ptz(@PathVariable String deviceId, @PathVariable String channelId, int leftRight, int upDown, int inOut, int moveSpeed, int zoomSpeed){
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("设备云台控制 API调用，deviceId：%s ，channelId：%s ，leftRight：%d ，upDown：%d ，inOut：%d ，moveSpeed：%d ，zoomSpeed：%d",deviceId, channelId, leftRight, upDown, inOut, moveSpeed, zoomSpeed));
		}
		Device device = storager.queryVideoDevice(deviceId);
		
		cmder.ptzCmd(device, channelId, leftRight, upDown, inOut, moveSpeed, zoomSpeed);
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}
}
