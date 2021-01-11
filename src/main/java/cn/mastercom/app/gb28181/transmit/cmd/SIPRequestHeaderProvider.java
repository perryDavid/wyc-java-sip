package cn.mastercom.app.gb28181.transmit.cmd;

import cn.mastercom.app.conf.SipConfig;
import cn.mastercom.app.gb28181.SipLayer;
import cn.mastercom.app.gb28181.bean.Device;
import cn.mastercom.app.gb28181.bean.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @Description:摄像头命令request创造器 TODO 冗余代码太多待优化
 * @author: songww
 * @date: 2020年5月6日 上午9:29:02
 */
@Component
public class SIPRequestHeaderProvider {

	@Autowired
	private SipLayer layer;

	@Autowired
	private SipConfig sipConfig;

	@Value("${sip.ip}")
	private String sipIP;

	@Value("#{${sip.port}}")
	private int sipPort;
	
	public Request createMessageRequest(Device device, String content, String viaTag, String fromTag, String toTag) throws ParseException, InvalidArgumentException {
		Request request = null;
		Host host = device.getHost();
		// sipuri
		SipURI requestURI = layer.getAddressFactory().createSipURI(device.getDeviceId(), host.getAddress());
		// via
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = layer.getHeaderFactory().createViaHeader(sipConfig.getSipIp(), sipConfig.getSipPort(),
				device.getTransport(), viaTag);
		viaHeader.setRPort();
		viaHeaders.add(viaHeader);
		// from
		SipURI fromSipURI = layer.getAddressFactory().createSipURI(sipConfig.getSipId(),
				sipConfig.getSipIp() + ":" + sipConfig.getSipPort());
		Address fromAddress = layer.getAddressFactory().createAddress(fromSipURI);
		FromHeader fromHeader = layer.getHeaderFactory().createFromHeader(fromAddress, fromTag);
		// to
		SipURI toSipURI = layer.getAddressFactory().createSipURI(device.getDeviceId(), sipConfig.getSipDomain());
		Address toAddress = layer.getAddressFactory().createAddress(toSipURI);
		ToHeader toHeader = layer.getHeaderFactory().createToHeader(toAddress, toTag);
		// callid
		CallIdHeader callIdHeader = device.getTransport().equals("TCP") ? layer.getTcpSipProvider().getNewCallId()
				: layer.getUdpSipProvider().getNewCallId();
		// Forwards
		MaxForwardsHeader maxForwards = layer.getHeaderFactory().createMaxForwardsHeader(70);
		// ceq
		CSeqHeader cSeqHeader = layer.getHeaderFactory().createCSeqHeader(1L, Request.MESSAGE);

		request = layer.getMessageFactory().createRequest(requestURI, Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
				toHeader, viaHeaders, maxForwards);
		ContentTypeHeader contentTypeHeader = layer.getHeaderFactory().createContentTypeHeader("Application", "MANSCDP+xml");
		request.setContent(content, contentTypeHeader);
		return request;
	}





	
	public Request createInviteRequest(Device device, String channelId, String content, String viaTag, String fromTag, String toTag) throws ParseException, InvalidArgumentException {
		Request request = null;
		Host host = device.getHost();
		//请求行
		SipURI requestLine = layer.getAddressFactory().createSipURI(channelId, host.getAddress());
		//via
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		// ViaHeader viaHeader = layer.getHeaderFactory().createViaHeader(sipConfig.getSipIp(), sipConfig.getSipPort(), device.getTransport(), viaTag);
		ViaHeader viaHeader = layer.getHeaderFactory().createViaHeader(device.getHost().getIp(), device.getHost().getPort(), device.getTransport(), viaTag);
		viaHeader.setRPort();
		viaHeaders.add(viaHeader);
		//from
		SipURI fromSipURI = layer.getAddressFactory().createSipURI(sipConfig.getSipId(),sipConfig.getSipDomain());
		Address fromAddress = layer.getAddressFactory().createAddress(fromSipURI);
		//FromHeader fromHeader = layer.getHeaderFactory().createFromHeader(fromAddress, fromTag); //必须要有标记，否则无法创建会话，无法回应ack
		FromHeader fromHeader = layer.getHeaderFactory().createFromHeader(fromAddress, id());
		//to
		SipURI toSipURI = layer.getAddressFactory().createSipURI(channelId,sipConfig.getSipDomain()); 
		Address toAddress = layer.getAddressFactory().createAddress(toSipURI);
		ToHeader toHeader = layer.getHeaderFactory().createToHeader(toAddress,null);
		StringBuilder stringBuilder = new StringBuilder();
		//callid
		CallIdHeader callIdHeader = null;
		if(device.getTransport().equals("TCP")) {
			callIdHeader = layer.getTcpSipProvider().getNewCallId();
		}
		if(device.getTransport().equals("UDP")) {
			callIdHeader = layer.getUdpSipProvider().getNewCallId();
		}
		
		//Forwards
		MaxForwardsHeader maxForwards = layer.getHeaderFactory().createMaxForwardsHeader(70);
		
		//ceq
		CSeqHeader cSeqHeader = layer.getHeaderFactory().createCSeqHeader(21L, Request.INVITE);
		request = layer.getMessageFactory().createRequest(requestLine, Request.INVITE, callIdHeader, cSeqHeader,fromHeader, toHeader, viaHeaders, maxForwards);



		Address concatAddress = layer.getAddressFactory().createAddress(layer.getAddressFactory().createSipURI(sipConfig.getSipId(), sipConfig.getSipIp()+":"+sipConfig.getSipPort()));
		// Address concatAddress = layer.getAddressFactory().createAddress(layer.getAddressFactory().createSipURI(sipConfig.getSipId(), device.getHost().getIp()+":"+device.getHost().getPort()));
		request.addHeader(layer.getHeaderFactory().createContactHeader(concatAddress));
		
		ContentTypeHeader contentTypeHeader = layer.getHeaderFactory().createContentTypeHeader("Application", "SDP");
		request.setContent(content, contentTypeHeader);
		return request;
	}

	public Request createInviteRequest(Device device, String channelId, String content) throws ParseException, InvalidArgumentException {
		Request request = null;
		Host host = device.getHost();
		//请求行
		SipURI requestLine = layer.getAddressFactory().createSipURI(channelId, host.getAddress());
		//via
		ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
		ViaHeader viaHeader = layer.getHeaderFactory().createViaHeader(sipIP, sipPort, device.getTransport(), null);
		viaHeader.setRPort();
		viaHeaders.add(viaHeader);
		//from
		SipURI fromSipURI = layer.getAddressFactory().createSipURI(sipConfig.getSipId(),sipIP + ":" + sipPort);
		Address fromAddress = layer.getAddressFactory().createAddress(fromSipURI);
		FromHeader fromHeader = layer.getHeaderFactory().createFromHeader(fromAddress, id()); //必须要有标记，否则无法创建会话，无法回应ack
		//to
		SipURI toSipURI = layer.getAddressFactory().createSipURI(sipConfig.getSipId(),device.getHost().getIp() + ":" + device.getHost().getPort());
		Address toAddress = layer.getAddressFactory().createAddress(toSipURI);
		ToHeader toHeader = layer.getHeaderFactory().createToHeader(toAddress,null);
		//callid
		CallIdHeader callIdHeader = null;
		if(device.getTransport().equals("TCP")) {
			callIdHeader = layer.getTcpSipProvider().getNewCallId();
		}
		if(device.getTransport().equals("UDP")) {
			callIdHeader = layer.getUdpSipProvider().getNewCallId();
		}
		//Forwards
		MaxForwardsHeader maxForwards = layer.getHeaderFactory().createMaxForwardsHeader(70);

		//ceq
		CSeqHeader cSeqHeader = layer.getHeaderFactory().createCSeqHeader(20L, Request.INVITE);
		request = layer.getMessageFactory().createRequest(requestLine, Request.INVITE, callIdHeader, cSeqHeader,fromHeader, toHeader, viaHeaders, maxForwards);

		Address concatAddress = layer.getAddressFactory().createAddress(layer.getAddressFactory().createSipURI(sipConfig.getSipId(), sipConfig.getSipIp()+":"+sipConfig.getSipPort()));
		// Address concatAddress = layer.getAddressFactory().createAddress(layer.getAddressFactory().createSipURI(sipConfig.getSipId(), device.getHost().getIp()+":"+device.getHost().getPort()));
		request.addHeader(layer.getHeaderFactory().createContactHeader(concatAddress));

		ContentTypeHeader contentTypeHeader = layer.getHeaderFactory().createContentTypeHeader("Application", "SDP");
		request.setContent(content, contentTypeHeader);
		return request;
	}


	/**
	 *
	 * sssssss
	 * s*/
	public Request createInviteRequest(Device device, String channelId, String content,String deviceId) throws ParseException, InvalidArgumentException {
		Request request = null;
		Host host = device.getHost();
		//请求行
		SipURI requestLine = layer.getAddressFactory().createSipURI(channelId, host.getAddress());
		//via
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		 ViaHeader viaHeader = layer.getHeaderFactory().createViaHeader(sipConfig.getSipIp(), sipConfig.getSipPort(), device.getTransport(), null);
		//ViaHeader viaHeader = layer.getHeaderFactory().createViaHeader(device.getHost().getIp(), device.getHost().getPort(), device.getTransport(), null);
		viaHeader.setRPort();
		viaHeaders.add(viaHeader);
		//from
		/*SipURI fromSipURI = layer.getAddressFactory().createSipURI(sipConfig.getSipId(),sipConfig.getSipDomain());*/
		SipURI fromSipURI = layer.getAddressFactory().createSipURI(sipConfig.getSipId(),sipIP + ":" + sipPort);
		Address fromAddress = layer.getAddressFactory().createAddress(fromSipURI);
		//FromHeader fromHeader = layer.getHeaderFactory().createFromHeader(fromAddress, fromTag); //必须要有标记，否则无法创建会话，无法回应ack
		FromHeader fromHeader = layer.getHeaderFactory().createFromHeader(fromAddress, id());
		//to
		SipURI toSipURI = layer.getAddressFactory().createSipURI(channelId,device.getHost().getIp() + ":" + device.getHost().getPort());
		Address toAddress = layer.getAddressFactory().createAddress(toSipURI);
		ToHeader toHeader = layer.getHeaderFactory().createToHeader(toAddress,null);

		SubjectHeader subjectHeader = layer.getHeaderFactory().createSubjectHeader(channelId + ":1"  + "," + sipConfig.getSipId() + ":1");
		AllowHeader allowHeader = layer.getHeaderFactory().createAllowHeader(Request.INVITE + "," + Request.ACK + "," + Request.CANCEL + "," + Request.BYE + "," + Request.MESSAGE + "," + Request.UPDATE + "," + Request.INFO + "," + Request.PRACK);
		SupportedHeader supportedHeader = layer.getHeaderFactory().createSupportedHeader("100rel");

		//callid
		CallIdHeader callIdHeader = null;
		if(device.getTransport().equals("TCP")) {
			callIdHeader = layer.getTcpSipProvider().getNewCallId();
		}
		if(device.getTransport().equals("UDP")) {
			callIdHeader = layer.getUdpSipProvider().getNewCallId();
		}

		//Forwards
		MaxForwardsHeader maxForwards = layer.getHeaderFactory().createMaxForwardsHeader(70);

		//ceq
		CSeqHeader cSeqHeader = layer.getHeaderFactory().createCSeqHeader(18467L, Request.INVITE);
		request = layer.getMessageFactory().createRequest(requestLine, Request.INVITE, callIdHeader, cSeqHeader,fromHeader, toHeader, viaHeaders, maxForwards);



		Address concatAddress = layer.getAddressFactory().createAddress(layer.getAddressFactory().createSipURI(sipConfig.getSipId(), sipConfig.getSipIp()+":"+sipConfig.getSipPort()));
		// Address concatAddress = layer.getAddressFactory().createAddress(layer.getAddressFactory().createSipURI(sipConfig.getSipId(), device.getHost().getIp()+":"+device.getHost().getPort()));
		request.addHeader(layer.getHeaderFactory().createContactHeader(concatAddress));
		request.addHeader(subjectHeader);
		request.addHeader(allowHeader);
		request.addHeader(supportedHeader);
		ContentTypeHeader contentTypeHeader = layer.getHeaderFactory().createContentTypeHeader("Application", "SDP");
		request.setContent(content, contentTypeHeader);
		return request;
	}

	public static String id() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}


	
	public Request createPlaybackInviteRequest(Device device, String channelId, String content, String viaTag, String fromTag, String toTag) throws ParseException, InvalidArgumentException {
		Request request = null;
		Host host = device.getHost();
		//请求行
		SipURI requestLine = layer.getAddressFactory().createSipURI(device.getDeviceId(), host.getAddress());
		//via
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		// ViaHeader viaHeader = layer.getHeaderFactory().createViaHeader(sipConfig.getSipIp(), sipConfig.getSipPort(), device.getTransport(), viaTag);
		ViaHeader viaHeader = layer.getHeaderFactory().createViaHeader(device.getHost().getIp(), device.getHost().getPort(), device.getTransport(), viaTag);
		viaHeader.setRPort();
		viaHeaders.add(viaHeader);
		//from
		SipURI fromSipURI = layer.getAddressFactory().createSipURI(sipConfig.getSipId(),sipConfig.getSipDomain());
		Address fromAddress = layer.getAddressFactory().createAddress(fromSipURI);
		FromHeader fromHeader = layer.getHeaderFactory().createFromHeader(fromAddress, fromTag); //必须要有标记，否则无法创建会话，无法回应ack
		//to
		SipURI toSipURI = layer.getAddressFactory().createSipURI(channelId,sipConfig.getSipDomain()); 
		Address toAddress = layer.getAddressFactory().createAddress(toSipURI);
		ToHeader toHeader = layer.getHeaderFactory().createToHeader(toAddress,null);

		//callid
		CallIdHeader callIdHeader = null;
		if(device.getTransport().equals("TCP")) {
			callIdHeader = layer.getTcpSipProvider().getNewCallId();
		}
		if(device.getTransport().equals("UDP")) {
			callIdHeader = layer.getUdpSipProvider().getNewCallId();
		}
		
		//Forwards
		MaxForwardsHeader maxForwards = layer.getHeaderFactory().createMaxForwardsHeader(70);
		
		//ceq
		CSeqHeader cSeqHeader = layer.getHeaderFactory().createCSeqHeader(1L, Request.INVITE);
		request = layer.getMessageFactory().createRequest(requestLine, Request.INVITE, callIdHeader, cSeqHeader,fromHeader, toHeader, viaHeaders, maxForwards);
		
		Address concatAddress = layer.getAddressFactory().createAddress(layer.getAddressFactory().createSipURI(sipConfig.getSipId(), sipConfig.getSipIp()+":"+sipConfig.getSipPort()));
		// Address concatAddress = layer.getAddressFactory().createAddress(layer.getAddressFactory().createSipURI(sipConfig.getSipId(), device.getHost().getIp()+":"+device.getHost().getPort()));
		request.addHeader(layer.getHeaderFactory().createContactHeader(concatAddress));
		
		ContentTypeHeader contentTypeHeader = layer.getHeaderFactory().createContentTypeHeader("Application", "SDP");
		request.setContent(content, contentTypeHeader);
		return request;
	}
}
