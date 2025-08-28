package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepositoryV2;
    private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false);  // 트랜잭션 시작

            Member fromMember = memberRepositoryV2.findById(con, fromId);
            Member toMember = memberRepositoryV2.findById(con, toId);

            memberRepositoryV2.update(con, fromId, fromMember.getMoney() - money);
            if (toMember.getMemberId().equals("ex")){
                throw new IllegalStateException("이체 중 예외 발생");
            }

            memberRepositoryV2.update(con, toId, fromMember.getMoney() + money);
            con.commit();

        } catch (Exception e) {
            con.rollback();
            throw new IllegalStateException(e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);  // 커넥션 풀 고려
                    con.close();
                } catch (Exception e) {
                    log.info("error", e);
                }
            }
        }
    }
}
