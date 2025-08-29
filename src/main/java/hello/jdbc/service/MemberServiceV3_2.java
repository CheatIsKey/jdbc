package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

@Slf4j
public class MemberServiceV3_2 {

//    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepositoryV3;
    // 트랜잭션 템플릿 사용 (중복 코드 최소화)
    private final TransactionTemplate template;

    public MemberServiceV3_2(MemberRepositoryV3 memberRepositoryV3, PlatformTransactionManager transactionManager) {
        this.memberRepositoryV3 = memberRepositoryV3;
        this.template = new TransactionTemplate(transactionManager);
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        template.executeWithoutResult(transactionStatus -> {
            try {
                Member fromMember = memberRepositoryV3.findById(fromId);
                Member toMember = memberRepositoryV3.findById(toId);

                memberRepositoryV3.update(fromId, fromMember.getMoney() - money);
                if (toMember.getMemberId().equals("ex")){
                    throw new IllegalStateException("이체 중 예외 발생");
                }

                memberRepositoryV3.update(toId, fromMember.getMoney() + money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
