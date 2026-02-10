package org.openldes.server.ingest.validation.defaultimpl;

import org.openldes.server.domain.events.admin.ShaclChangedEvent;
import org.openldes.server.domain.events.admin.ShaclDeletedEvent;
import org.openldes.server.ingest.entities.IngestedMember;
import org.openldes.server.ingest.validation.MemberIngestValidator;
import org.openldes.server.ingest.validation.defaultimpl.modelingestvalidator.ModelIngestValidator;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

public class MemberIngestValidatorImpl implements MemberIngestValidator {

	private final ModelIngestValidatorFactory validatorFactory;
	private final Map<String, ModelIngestValidator> validators = new HashMap<>();

	public MemberIngestValidatorImpl(ModelIngestValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
	}

	@EventListener
	public void handleShaclChangedEvent(ShaclChangedEvent event) {
		final String collectionName = event.getCollection();

		validators.compute(collectionName,
				(key, oldValue) -> validatorFactory.createValidator(event.getModel()));
	}

	@EventListener
	public void handleShaclDeletedEvent(ShaclDeletedEvent event) {
		validators.remove(event.collectionName());
	}

	@Override
	public void validate(IngestedMember member) {
		var validator = validators.get(member.getCollectionName());
		if (validator != null) {
			validator.validate(member.getModel());
		}
	}

}
