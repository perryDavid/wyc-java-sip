package cn.mastercom.app.vmanager.device;

import cn.mastercom.app.gb28181.bean.Device;
import cn.mastercom.app.gb28181.event.DeviceOffLineDetector;
import cn.mastercom.app.gb28181.transmit.callback.DeferredResultHolder;
import cn.mastercom.app.gb28181.transmit.cmd.impl.SIPCommander;
import cn.mastercom.app.storager.IVideoManagerStorager;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class DeviceController {
	
	private final static Logger logger = LoggerFactory.getLogger(DeviceController.class);
	
	@Autowired
	private IVideoManagerStorager storager;
	
	@Autowired
	private SIPCommander cmder;
	
	@Autowired
	private DeferredResultHolder resultHolder;
	
	@Autowired
	private DeviceOffLineDetector offLineDetector;
	
	@GetMapping("/devices/{deviceId}")
	public ResponseEntity<Device> devices(@PathVariable String deviceId){
		
		if (logger.isDebugEnabled()) {
			logger.debug("查询视频设备API调用，deviceId：" + deviceId);
		}
		
		Device device = storager.queryVideoDevice(deviceId);
		return new ResponseEntity<>(device, HttpStatus.OK);
	}
	
	@GetMapping("/devices")
	public ResponseEntity<List<Device>> devices(){
		
		if (logger.isDebugEnabled()) {
			logger.debug("查询所有视频设备API调用");
		}
		
		List<Device> deviceList = storager.queryVideoDeviceList(null);
		return new ResponseEntity<>(deviceList, HttpStatus.OK);
	}
	
	@PostMapping("/devices/{deviceId}/sync")
	public DeferredResult<ResponseEntity<Device>> devicesSync(@PathVariable String deviceId){
		
		if (logger.isDebugEnabled()) {
			logger.debug("设备信息同步API调用，deviceId：" + deviceId);
		}
		
		Device device = storager.queryVideoDevice(deviceId);
        cmder.catalogQuery(device);
        DeferredResult<ResponseEntity<Device>> result = new DeferredResult<ResponseEntity<Device>>();
        resultHolder.put(DeferredResultHolder.CALLBACK_CMD_CATALOG+deviceId, result);
        return result;
	}
	
	@PostMapping("/devices/{deviceId}/delete")
	public ResponseEntity<String> delete(@PathVariable String deviceId){
		
		if (logger.isDebugEnabled()) {
			logger.debug("设备信息删除API调用，deviceId：" + deviceId);
		}
		
		if (offLineDetector.isOnline(deviceId)) {
			return new ResponseEntity<String>("不允许删除在线设备！", HttpStatus.NOT_ACCEPTABLE);
		}
		boolean isSuccess = storager.delete(deviceId);
		if (isSuccess) {
			JSONObject json = new JSONObject();
			json.put("deviceId", deviceId);
			return new ResponseEntity<>(json.toString(), HttpStatus.OK);
		} else {
			logger.warn("设备预览API调用失败！");
			return new ResponseEntity<String>("设备预览API调用失败！", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
