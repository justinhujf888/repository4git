package weixin.popular.bean.paymch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class DownloadbillResult extends MchBase{

	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}


}
