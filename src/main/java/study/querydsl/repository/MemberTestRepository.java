package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.entitiy.Member;
import study.querydsl.entitiy.QTeam;
import study.querydsl.repository.support.Querydsl4RepositorySupport;

import java.util.List;

import static study.querydsl.entitiy.QMember.member;
import static study.querydsl.entitiy.QTeam.team;

@Repository
public class MemberTestRepository extends Querydsl4RepositorySupport {

    public MemberTestRepository() {
        super(Member.class);
    }

    public List<Member> basicSelect() {
        return select(member)
                .from(member)
                .fetch();
    }

    public List<Member> basicSelectFrom() {
        return selectFrom(member).fetch();
    }

    public Page<Member> applyPaginationV1(MemberSearchCondition condition, Pageable pageable) {
        JPAQuery<Member> query = selectFrom(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );

        List<Member> content = getQuerydsl().applyPagination(pageable, query).fetch();
        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    public Page<Member> applyPaginationV2(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable, query -> query
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
        );
    }

    public Page<Member> applyPaginationV3(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable,
                contentQuery -> contentQuery
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                , countQuery -> countQuery
                        .select(Wildcard.count)
                        .from(member)
                        .leftJoin(member.team, team)
                        .where(usernameEq(condition.getUsername()),
                                teamNameEq(condition.getTeamName()),
                                ageGoe(condition.getAgeGoe()),
                                ageLoe(condition.getAgeLoe()))
        );
    }

    private BooleanBuilder usernameEq(String username) {
        if (StringUtils.hasText(username)) {
            return new BooleanBuilder(member.username.eq(username));
        }
        return new BooleanBuilder();
    }

    private BooleanBuilder teamNameEq(String teamName) {
        if (StringUtils.hasText(teamName)) {
            return new BooleanBuilder(team.name.eq(teamName));
        }
        return new BooleanBuilder();
    }

    private BooleanBuilder ageGoe(Integer ageGoe) {
        if (ageGoe != null) {
            return new BooleanBuilder(member.age.goe(ageGoe));
        }
        return new BooleanBuilder();
    }

    private BooleanBuilder ageLoe(Integer ageLoe) {
        if (ageLoe != null) {
            return new BooleanBuilder(member.age.loe(ageLoe));
        }
        return new BooleanBuilder();
    }
}
