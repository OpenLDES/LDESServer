package org.openldes.server.pagination.postgres;

import java.util.List;
import org.openldes.server.pagination.postgres.repository.FragmentationMemberEntityRepository;
import org.openldes.server.pagination.repositories.MemberRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FragmentationMemberPostgresRepository implements MemberRepository {
    private final FragmentationMemberEntityRepository fragmentationMemberEntityRepository;

    public FragmentationMemberPostgresRepository(FragmentationMemberEntityRepository fragmentationMemberEntityRepository) {
        this.fragmentationMemberEntityRepository = fragmentationMemberEntityRepository;
    }

    @Override
    @Transactional
    public void updateIsFragmented(boolean isFragmented, List<Long> memberIds) {
        fragmentationMemberEntityRepository.updateIsFragmented(isFragmented, memberIds);
    }
}
