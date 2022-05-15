package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;

import static study.querydsl.entitiy.QMember.member;
import static study.querydsl.entitiy.QTeam.team;

public class MemberCustomRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory queryFactory;

    public MemberCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                                member.id,
                                member.username,
                                member.age,
                                team.id,
                                team.name
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetch();
    }

    private BooleanBuilder usernameEq(String username) {
        if(StringUtils.hasText(username)) {
            return new BooleanBuilder(member.username.eq(username));
        }
        return new BooleanBuilder();
    }

    private BooleanBuilder teamNameEq(String teamName) {
        if(StringUtils.hasText(teamName)) {
            return new BooleanBuilder(team.name.eq(teamName));
        }
        return new BooleanBuilder();
    }

    private BooleanBuilder ageGoe(Integer ageGoe) {
        if(ageGoe != null) {
            return new BooleanBuilder(member.age.goe(ageGoe));
        }
        return new BooleanBuilder();
    }

    private BooleanBuilder ageLoe(Integer ageLoe) {
        if(ageLoe != null) {
            return new BooleanBuilder(member.age.loe(ageLoe));
        }
        return new BooleanBuilder();
    }
}
