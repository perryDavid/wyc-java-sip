package cn.mastercom.app.gb28181.session;

import org.springframework.stereotype.Component;

import javax.sip.ClientTransaction;
import java.util.concurrent.ConcurrentHashMap;

/**    
 * @Description:视频流session管理器，管理视频预览、预览回放的通信句柄 
 * @author: songww
 * @date:   2020年5月13日 下午4:03:02     
 */
@Component
public class VideoStreamSessionManager {

	private ConcurrentHashMap<String, ClientTransaction> sessionMap = new ConcurrentHashMap<>();

	public String createPlaySsrc(){
		String ssrc = SsrcUtil.getPlaySsrc();
		return ssrc;
	}
	
	public String createPlayBackSsrc(){
		String ssrc = SsrcUtil.getPlayBackSsrc();
		return ssrc;
	}
	
	public void put(String ssrc,ClientTransaction transaction){
		sessionMap.put(ssrc, transaction);
	}
	
	public ClientTransaction get(String ssrc){
		return sessionMap.get(ssrc);
	}
	
	public void remove(String ssrc) {
		sessionMap.remove(ssrc);
		SsrcUtil.releaseSsrc(ssrc);
	}
}
