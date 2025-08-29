package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

//    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepositoryV3;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
//        Connection con = dataSource.getConnection();
        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
//            con.setAutoCommit(false);  // 트랜잭션 시작

            Member fromMember = memberRepositoryV3.findById(fromId);
            Member toMember = memberRepositoryV3.findById(toId);

            memberRepositoryV3.update(fromId, fromMember.getMoney() - money);
            if (toMember.getMemberId().equals("ex")){
                throw new IllegalStateException("이체 중 예외 발생");
            }

            memberRepositoryV3.update(toId, fromMember.getMoney() + money);
            transactionManager.commit(status);
//            con.commit();

        } catch (Exception e) {
//            con.rollback();
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
    }
}
