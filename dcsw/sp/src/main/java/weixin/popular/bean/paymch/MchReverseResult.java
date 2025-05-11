package weixin.popular.bean.paymch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 退款查询
 *
 * @author Yi
 *
 */

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class MchReverseResult extends MchBase{

	@XmlElement
	private String recall;

	public String getRecall() {
		return recall;
	}

	public void setRecall(String recall) {
		this.recall = recall;
	}


}
