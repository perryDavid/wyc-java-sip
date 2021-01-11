package cn.mastercom.app.gb28181.transmit.cmd;

import cn.mastercom.app.gb28181.bean.Device;

/**
 * @Description:设备能力接口，用于定义设备的控制、查询能力   
 * @author: songww
 * @date:   2020年5月3日 下午9:16:34     
 */
public interface ISIPCommander {

	/**
	 * 云台方向放控制，使用配置文件中的默认镜头移动速度
	 * 
	 * @param device  控制设备
	 * @param channelId  预览通道
	 * @param leftRight  镜头左移右移 0:停止 1:左移 2:右移
     * @param upDown     镜头上移下移 0:停止 1:上移 2:下移
	 */
	 boolean ptzdirectCmd(Device device, String channelId, int leftRight, int upDown);

	/**
	 * 云台方向放控制
	 *
	 * @param device  控制设备
	 * @param channelId  预览通道
	 * @param leftRight  镜头左移右移 0:停止 1:左移 2:右移
     * @param upDown     镜头上移下移 0:停止 1:上移 2:下移
     * @param moveSpeed  镜头移动速度
	 */
	 boolean ptzdirectCmd(Device device, String channelId, int leftRight, int upDown, int moveSpeed);

	/**
	 * 云台缩放控制，使用配置文件中的默认镜头缩放速度
	 *
	 * @param device  控制设备
	 * @param channelId  预览通道
     * @param inOut      镜头放大缩小 0:停止 1:缩小 2:放大
	 */
	 boolean ptzZoomCmd(Device device, String channelId, int inOut);

	/**
	 * 云台缩放控制
	 *
	 * @param device  控制设备
	 * @param channelId  预览通道
     * @param inOut      镜头放大缩小 0:停止 1:缩小 2:放大
	 */
	 boolean ptzZoomCmd(Device device, String channelId, int inOut, int moveSpeed);

	/**
	 * 云台控制，支持方向与缩放控制
	 *
	 * @param device  控制设备
	 * @param channelId  预览通道
	 * @param leftRight  镜头左移右移 0:停止 1:左移 2:右移
     * @param upDown     镜头上移下移 0:停止 1:上移 2:下移
     * @param inOut      镜头放大缩小 0:停止 1:缩小 2:放大
     * @param moveSpeed  镜头移动速度
     * @param zoomSpeed  镜头缩放速度
	 */
	 boolean ptzCmd(Device device, String channelId, int leftRight, int upDown, int inOut, int moveSpeed, int zoomSpeed);

	/**
	 * 请求预览视频流
	 *
	 * @param device  视频设备
	 * @param channelId  预览通道
	 */
	 String playStreamCmd(Device device, String deviceId, String channelId);

	/**
	 * 请求回放视频流
	 *
	 * @param device  视频设备
	 * @param channelId  预览通道
	 * @param startTime 开始时间,格式要求：yyyy-MM-dd HH:mm:ss
	 * @param endTime 结束时间,格式要求：yyyy-MM-dd HH:mm:ss
	 */
	 String playbackStreamCmd(Device device, String channelId, String startTime, String endTime);

	/**
	 * 视频流停止
	 *
	 * @param ssrc  ssrc
	 */
	 void streamByeCmd(String ssrc);

	/**
	 * 语音广播
	 *
	 * @param device  视频设备
	 * @param channelId  预览通道
	 */
	 boolean audioBroadcastCmd(Device device, String channelId);

	/**
	 * 音视频录像控制
	 *
	 * @param device  视频设备
	 * @param channelId  预览通道
	 */
	 boolean recordCmd(Device device, String channelId);

	/**
	 * 报警布防/撤防命令
	 *
	 * @param device  视频设备
	 */
	 boolean guardCmd(Device device);

	/**
	 * 报警复位命令
	 *
	 * @param device  视频设备
	 */
	 boolean alarmCmd(Device device);

	/**
	 * 强制关键帧命令,设备收到此命令应立刻发送一个IDR帧
	 *
	 * @param device  视频设备
	 * @param channelId  预览通道
	 */
	 boolean iFameCmd(Device device, String channelId);
	
	/**
	 * 看守位控制命令
	 * 
	 * @param device  视频设备
	 */
	 boolean homePositionCmd(Device device);
	
	/**
	 * 设备配置命令
	 * 
	 * @param device  视频设备
	 */
	 boolean deviceConfigCmd(Device device);
	
	
	/**
	 * 查询设备状态
	 * 
	 * @param device 视频设备
	 */
	 boolean deviceStatusQuery(Device device);
	
	/**
	 * 查询设备信息
	 * 
	 * @param device 视频设备
	 * @return 
	 */
	 boolean deviceInfoQuery(Device device);
	
	/**
	 * 查询目录列表
	 * 
	 * @param device 视频设备
	 */
	 boolean catalogQuery(Device device);
	
	/**
	 * 查询录像信息
	 * 
	 * @param device 视频设备
	 * @param startTime 开始时间,格式要求：yyyy-MM-dd HH:mm:ss
	 * @param endTime 结束时间,格式要求：yyyy-MM-dd HH:mm:ss
	 */
	 boolean recordInfoQuery(Device device, String channelId, String startTime, String endTime);
	
	/**
	 * 查询报警信息
	 * 
	 * @param device 视频设备
	 */
	 boolean alarmInfoQuery(Device device);
	
	/**
	 * 查询设备配置
	 * 
	 * @param device 视频设备
	 */
	 boolean configQuery(Device device);
	
	/**
	 * 查询设备预置位置
	 * 
	 * @param device 视频设备
	 */
	 boolean presetQuery(Device device);
	
	/**
	 * 查询移动设备位置数据
	 * 
	 * @param device 视频设备
	 */
	 boolean mobilePostitionQuery(Device device);
}
