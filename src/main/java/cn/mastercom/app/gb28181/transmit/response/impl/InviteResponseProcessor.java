package cn.mastercom.app.gb28181.transmit.response.impl;


import cn.mastercom.app.conf.SipConfig;
import cn.mastercom.app.gb28181.SipLayer;
import cn.mastercom.app.gb28181.transmit.SIPProcessorFactory;
import cn.mastercom.app.gb28181.transmit.response.ISIPResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sip.*;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * @Description:处理INVITE响应
 * @author: songww
 * @date: 2020年5月3日 下午4:43:52
 */
@Component
public class InviteResponseProcessor implements ISIPResponseProcessor {

	private final static Logger logger = LoggerFactory.getLogger(SIPProcessorFactory.class);

	/**
	 * 处理invite响应
	 * 
	 * @param evt 响应消息
	 * @throws ParseException
	 */
	@Override
	public void process(ResponseEvent evt, SipLayer layer, SipConfig config) throws ParseException {
		try {
			Response response = evt.getResponse();
			int statusCode = response.getStatusCode();
			// trying不会回复
			if (statusCode == Response.TRYING) {
			}
			// 成功响应
			// 下发ack
			if (statusCode == Response.OK) {
				 ClientTransaction clientTransaction = evt.getClientTransaction();
				 if(clientTransaction == null){
				 logger.error("回复ACK时，clientTransaction为null >>> {}",response);
				 return;
				  }
				 Dialog clientDialog = clientTransaction.getDialog();

				 CSeqHeader clientCSeqHeader = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
				 long cseqId = clientCSeqHeader.getSeqNumber();
				// /*
				// createAck函数，创建的ackRequest，会采用Invite响应的200OK，中的contact字段中的地址，作为目标地址。
				// 有的终端传上来的可能还是内网地址，会造成ack发送不出去。接受不到音视频流
				// 所以在此处统一替换地址。和响应消息的Via头中的地址保持一致。
				// */
				 Request ackRequest = clientDialog.createAck(cseqId);
//				 SipURI requestURI = (SipURI) ackRequest.getRequestURI();
				 String to = ((ToHeader)response.getHeader(ToHeader.NAME)).toString();
				 String ip = "";
				 String domain = "";
				 if (to != null && (!"".equals(to))){
				 	String [] tempA = to.split("sip:");
				 	if (tempA.length == 2){
				 		String tempB = tempA[1];
						String [] tempC = tempB.split(">;");
						if (tempC.length == 2){
							String tempD = tempC[0];
							String [] tempE  = tempD.split("@");
							ip = tempE[0];
							domain = tempE[1];
						}
					}
				 }
				SipURI requestURI;
				 if (!"".equals(ip) && (!"".equals(domain)))
				 {
					 requestURI =  layer.getAddressFactory().createSipURI(ip, domain);
				 }
				 else {
					 requestURI =  layer.getAddressFactory().createSipURI("34020000001320000064", "192.168.1.230:5060");
				 }
				 ViaHeader viaHeader = (ViaHeader) response.getHeader(ViaHeader.NAME);
				try {
				 requestURI.setHost(viaHeader.getHost());
				 } catch (Exception e) {
				 e.printStackTrace();
				 }
				 requestURI.setPort(viaHeader.getPort());
				 clientDialog.sendAck(ackRequest);

				/*Dialog dialog = evt.getDialog();
				CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
				Request reqAck = dialog.createAck(cseq.getSeqNumber());
				SipURI requestURI = (SipURI) reqAck.getRequestURI();
				ViaHeader viaHeader = (ViaHeader) response.getHeader(ViaHeader.NAME);*/
				// String viaHost =viaHeader.getHost();
				//getHost()函数取回的IP地址是“[xxx.xxx.xxx.xxx:yyyy]”的格式，需用正则表达式截取为“xxx.xxx.xxx.xxx"格式
				// Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
				// Matcher matcher = p.matcher(viaHeader.getHost());
				// if (matcher.find()) {
				// 	requestURI.setHost(matcher.group());
				// }
				/*requestURI.setHost(viaHeader.getHost());
				requestURI.setPort(viaHeader.getPort());
				reqAck.setRequestURI(requestURI);
				dialog.sendAck(reqAck);*/
			}
		} catch (SipException | InvalidArgumentException e) {
			e.printStackTrace();
		}
	}

}
