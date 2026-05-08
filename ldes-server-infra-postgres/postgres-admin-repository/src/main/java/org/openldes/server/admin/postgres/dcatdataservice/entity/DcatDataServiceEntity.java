package org.openldes.server.admin.postgres.dcatdataservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.openldes.server.admin.postgres.ModelConverter;
import org.openldes.server.admin.postgres.view.entity.ViewEntity;

@Entity
@Table(name = "dcat_dataservices")
public class DcatDataServiceEntity {

	@Id
	@Column(name = "view_id", unique = true, nullable = false)
	private Integer id;

	@MapsId
	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "view_id", nullable = false)
	private ViewEntity viewEntity;

	@Column(nullable = false, columnDefinition = "text")
	@Convert(converter = ModelConverter.class)
	private Model model;

	protected DcatDataServiceEntity() {}

	public DcatDataServiceEntity(ViewEntity viewEntity) {
		this.viewEntity = viewEntity;
	}

	public String getViewName() {
		return viewEntity.getComposedViewName();
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
