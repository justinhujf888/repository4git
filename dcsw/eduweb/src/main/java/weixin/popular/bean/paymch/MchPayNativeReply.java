package weixin.popular.bean.paymch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * native 回复
 * @author LiYi
 *
 */
@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class MchPayNativeReply extends MchBase{

	@XmlElement
	private String prepay_id;

	public String getPrepay_id() {
		return prepay_id;
	}

	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}
}
