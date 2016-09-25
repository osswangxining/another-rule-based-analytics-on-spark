package cloud.iot.ai.analytics.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

public abstract class ContextElement extends Rich {
	private static final long serialVersionUID = -6684477384802992082L;
	private Schema schema = null;

	private String id = null;

	private String name = null;

	private boolean _abstract = false;

	private List<P> paras = new ArrayList<P>();

	private List<Inclusion> inclusions = new ArrayList<Inclusion>();

	protected Map<QName, Let> lets = new LinkedHashMap<QName, Let>();

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isAbstract() {
		return this._abstract;
	}

	public void setAbstract(boolean _abstract) {
		this._abstract = _abstract;
	}

	public Map<QName, Let> getLets() {
		return this.lets;
	}

	public void addLet(Let let) {
		this.lets.put(let.getQName(), let);
	}

	public void addLets(Map<QName, Let> lets) {
		this.lets.putAll(lets);
	}

	public void setLets(Map<QName, Let> lets) {
		if ((lets == null) || (!(lets instanceof LinkedHashMap)))
			this.lets = new LinkedHashMap<QName, Let>();
		else
			this.lets = lets;
	}

	public Map<QName, Let> getNeedPopulatedLets() {
		Map<QName, Let> nonPopulatedLets = new LinkedHashMap<QName, Let>();
		for (Iterator<Map.Entry<QName, Let>> iter = this.lets.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<QName, Let> entry = (Map.Entry<QName, Let>) iter.next();
			if (!((Let) entry.getValue()).isConstant()) {
				nonPopulatedLets.put((QName) entry.getKey(), (Let) entry.getValue());
			}
		}
		return nonPopulatedLets;
	}

	public List<Inclusion> getInclusions() {
		return this.inclusions;
	}

	public void addInclusion(Inclusion inclusion) {
		this.inclusions.add(inclusion);
	}

	public void addInclusions(List<Inclusion> inclusions) {
		this.inclusions.addAll(inclusions);
	}

	public Element toElement() {
		return null;
	}

	public String getName() {
		return this.name;
	}

	public List<P> getParas() {
		return this.paras;
	}

	public void addPara(P para) {
		this.paras.add(para);
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract Schema getRoot();

	public Schema getSchema() {
		return this.schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}
}
