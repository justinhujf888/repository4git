package weixin.popular.bean.paymch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 
 * 退款回调数据
 * 
 * @author liyi
 * @since 2.8.19
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class SecapiPayRefundNotify extends MchBase{

	private String req_info;

	public String getReq_info() {
		return req_info;
	}

	public void setReq_info(String req_info) {
		this.req_info = req_info;
	}
	
}
